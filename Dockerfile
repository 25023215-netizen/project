# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Build and skip tests to save memory and time
RUN mvn clean package -DskipTests -Djava.awt.headless=true

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create directory for H2 database persistence
RUN mkdir -p data

# Ensure the app runs in headless mode (no GUI needed for backend)
ENV JAVA_OPTS="-Djava.awt.headless=true"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
