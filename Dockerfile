FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Leverage Docker layer caching for dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code
COPY src ./src

# Ensure Maven wrapper is executable
RUN chmod +x mvnw

# Build Spring Boot application
RUN ./mvnw clean package -DskipTests

# Expose default port (Render will set PORT env var at runtime)
EXPOSE 8080

# Run the built JAR
ENTRYPOINT ["java", "-jar", "target/lms-0.0.1-SNAPSHOT.jar"]


