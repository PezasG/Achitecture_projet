package com.example.my_batch.util;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;

public class EmailUtil {

    public static void sendPayslipEmailWithAttachment(JavaMailSender mailSender,
                                                      String to,
                                                      String subject,
                                                      String text,
                                                      String attachmentPath) throws Exception {

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        if (attachmentPath != null) {
            File file = new File(attachmentPath);
            if (file.exists()) {
                FileSystemResource fr = new FileSystemResource(file);
                helper.addAttachment(file.getName(), fr);
            }
        }

        mailSender.send(msg);
    }
}
