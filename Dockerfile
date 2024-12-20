# Use an OpenJDK base image with a slim version of JRE (Java Runtime Environment)
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file into the Docker container
COPY target/examserver-0.0.1-SNAPSHOT.jar /app/examserver.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "/app/examserver.jar"]
