
FROM postgres:latests

FROM maven:3.9.9 AS build
COPY pom.xml .
COPY /src ./src/
RUN mvn clean package -DskipTests

FROM openjdk:17 AS prod
COPY --from=build target/corporate-service-0.0.1-SNAPSHOT.jar corporate-service.jar
EXPOSE 9000

ENTRYPOINT ["java", "-jar", "corporate-service.jar"]