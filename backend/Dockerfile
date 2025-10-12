FROM maven:3.9.11-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests --quiet

# Этап 2: Запуск JAR-файла
FROM eclipse-temurin:21-jre-alpine-3.22
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]