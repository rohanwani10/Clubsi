# Use an official OpenJDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy everything from your project
COPY . .

# Build the app (if Maven wrapper exists)
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Copy the generated jar
RUN cp target/*.jar app.jar

# Expose the port Render will use
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
