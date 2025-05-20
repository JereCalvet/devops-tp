# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

ARG MAVEN_PROFILE=production

RUN echo "Building with Maven profile: $MAVEN_PROFILE"
RUN mvn --batch-mode clean package -DskipTests -P${MAVEN_PROFILE}

LABEL authors="Jere", description="Dockerfile for a Spring Boot application with Maven, New Relic and JRE Alpine", license="MIT", repository="github.com/jerecalvet/devops-tp"
# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]