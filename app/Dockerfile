FROM python:3.11-alpine
LABEL maintainer="Pezas Gaëtan"

# Create a specific user to run the Python application
RUN adduser -D my-user -u 1000

# Create and change working directory
WORKDIR /app

# Add application requirements
COPY requirements.txt .

# Install requirements
RUN pip install -r requirements.txt

# Change use post-install to have all dependancies available
USER 1000

# Add application
COPY app.py .
COPY config.py .

# Launch application
ENTRYPOINT ["gunicorn"]
CMD ["--config", "config.py", "app:app"]
