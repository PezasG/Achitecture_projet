package com.example.my_batch.config;

import com.example.my_batch.model.Product;
import com.example.my_batch.repository.ProductRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

import jakarta.persistence.EntityManagerFactory;
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final ProductRepository productRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;
    private final EntityManagerFactory entityManagerFactory;

    public BatchConfiguration(ProductRepository productRepository, JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              JobLauncher jobLauncher,
                              EntityManagerFactory entityManagerFactory) {
        this.productRepository = productRepository;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jobLauncher = jobLauncher;
        this.entityManagerFactory = entityManagerFactory;
    }

    // 1. FlatFileItemReader to read CSV file
    @Bean(name = "reader")
    public FlatFileItemReader<Product> reader() {
        FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("products.csv"));
        reader.setLinesToSkip(1); // Skip header line

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "name", "price"); // Matches CSV columns to Product fields

        BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Product.class);

        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    // 2. ItemProcessor to process each Product (optional, in this case it just passes the data through)
    @Bean(name = "processor")
    public ItemProcessor<Product, Product> processor() {
        return product -> {
            final double DISCOUNT_THRESHOLD = 100.0;
            final double DISCOUNT_RATE = 0.1;

            if (product.getPrice() > DISCOUNT_THRESHOLD){
                double originalPrice = product.getPrice();
                product.setPrice(originalPrice*(1-DISCOUNT_RATE));
                System.out.println("Appliqué une réduction au produit : " + product.getName() +
                        " - ancien prix : " + originalPrice + ", nouveau prix : " + product.getPrice());
            }
            return product;
        };
    }

    // 3. ItemWriter to save Product to the database
    @Bean(name = "writer")
    public ItemWriter<Product> writer() {
        return products -> productRepository.saveAll(products);
    }

    // 4. Step to process the data using the reader, processor, and writer
    @Bean(name = "productStep")
    public Step productStep() {
        return new StepBuilder("productStep", jobRepository)
                .<Product, Product>chunk(10, transactionManager) // Processing 10 items at a time
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .transactionManager(transactionManager)
                .build();
    }

    // 5. Job to execute the batch process
    @Bean(name = "importProductJob")
    public Job importProductJob() {
        SimpleJob job = new SimpleJob("importJob");
        job.setName("importJob");
        job.setJobRepository(jobRepository);
        job.addStep(productStep());
        return job;
    }


    @Bean(name = "dbReader")
    public JpaPagingItemReader<Product> dbReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Product>()
                .name("productDbReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM Product p")
                .pageSize(10)
                .build();
    }


    @Bean(name = "csvWriter")
    public FlatFileItemWriter<Product> csvWriter() {
        FlatFileItemWriter<Product> writer = new FlatFileItemWriter<>();

        writer.setResource(new FileSystemResource("products_export.csv"));
        writer.setHeaderCallback(w -> w.write("id,name,price"));
        writer.setLineAggregator(new DelimitedLineAggregator<>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
                setNames(new String[]{"id", "name", "price"});
            }});
        }});

        return writer;
    }

    @Bean(name = "exportProductStep")
    public Step exportProductStep() {
        return new StepBuilder("exportProductStep", jobRepository)
                .<Product, Product>chunk(10, transactionManager)
                .reader(dbReader(entityManagerFactory))
                .writer(csvWriter())
                .build();
    }

    @Bean(name = "exportProductJob")
    public Job exportProductJob() {
        SimpleJob job = new SimpleJob("exportJob");
        job.setName("exportJob");

        job.setJobRepository(jobRepository);
        job.addStep(exportProductStep());
        return job;
    }

    // 6. CommandLineRunner to trigger the job when the application starts
    @Bean
    public CommandLineRunner runBatchJobs() {
        return args -> {
            try {
                System.out.println("▶️ Démarrage du job d'import...");
                jobLauncher.run(importProductJob(), new JobParametersBuilder()
                        .addLong("time-import", System.currentTimeMillis())
                        .toJobParameters());

                System.out.println("✅ Import terminé.");

                System.out.println("▶️ Démarrage du job d'export...");
                jobLauncher.run(exportProductJob(), new JobParametersBuilder()
                        .addLong("time-export", System.currentTimeMillis())
                        .toJobParameters());

                System.out.println("✅ Export terminé.");

            } catch (Exception e) {
                System.err.println("❌ Erreur pendant l'exécution du batch : " + e.getMessage());
            }
        };
    }
}

