FROM gradle:7.2.0-jdk17 AS builder
RUN gradle bootJar

FROM amazoncorretto:17.0.7-alpine
COPY --from=builder /build/libs/cloudnativeapp-cicd-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080