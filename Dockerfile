# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

ARG MAVEN_PROFILE=prod

RUN echo "Building with Maven profile: $MAVEN_PROFILE"
RUN mvn --batch-mode clean package -DskipTests -P${MAVEN_PROFILE}

LABEL authors="Jere"
LABEL description="Dockerfile for a Spring Boot application with Maven and JRE Alpine"
LABEL license="MIT"
LABEL repository="github.com/jerecalvet/devops-tp"
# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

RUN adduser -D myuser && chown -R myuser /app
USER myuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

