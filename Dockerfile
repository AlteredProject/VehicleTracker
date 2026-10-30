# Start with Amazon Corretto 17 Alpine base image
FROM eclipse-temurin:25-jdk-alpine

# Install curl on Alpine
RUN apk update && apk add --no-cache curl

# Copy the jar file into the image
COPY target/app.jar /app.jar

# Expose the port your app runs on
EXPOSE 7070

# Command to run your app
CMD ["java", "-jar", "/app.jar"]