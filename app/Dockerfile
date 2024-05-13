FROM amazoncorretto:17.0.7-alpine
COPY build/libs/cloudnativeapp-1-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080