FROM gradle:7.2.0-jdk17 AS builder
WORKDIR /app
RUN gradle bootJar

FROM amazoncorretto:17.0.7-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/cloudnativeapp-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080