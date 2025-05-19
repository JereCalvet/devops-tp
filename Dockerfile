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

RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

ARG MAVEN_PROFILE=production
ENV NEW_RELIC_ENV=${MAVEN_PROFILE}
ARG NEW_RELIC_LICENSE_KEY=''
ENV NEW_RELIC_LICENSE_KEY=${MAVEN_PROFILE}

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -javaagent:/usr/local/newrelic/newrelic.jar -Dnewrelic.environment=$NEW_RELIC_ENV -Dnewrelic.config.license_key=$NEW_RELIC_LICENSE_KEY -jar /app/app.jar"]