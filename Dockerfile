# ==========================
# 🌱 Stage 1: Build the App
# ==========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# ==========================
# 🚀 Stage 2: Run the App
# ==========================
FROM openjdk:17-jdk-slim

# Set working directory for runtime container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port (Render uses 8080)
EXPOSE 8080

# Set environment variables for MongoDB connection and port
ENV MONGO_URI=${MONGO_URI}
ENV PORT=8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
