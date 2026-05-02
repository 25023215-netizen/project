# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create directory for H2 database persistence (optional for ephemeral clouds)
RUN mkdir -p data

# Port exposed by the application
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
