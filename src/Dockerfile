# Use the official Maven image to build the application
# This image includes OpenJDK 21 and Maven
FROM maven:3.8.5-openjdk-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Use the official OpenJDK image for running the application
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/recipie-api-0.0.1-SNAPSHOT.jar /app/recipie-api.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/recipie-api.jar"]
