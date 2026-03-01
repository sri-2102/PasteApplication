FROM eclipse-temurin:17-jdk-alpine

# copy the built JAR into the image; the build step should run `mvn package`
COPY target/pasteapp-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]
