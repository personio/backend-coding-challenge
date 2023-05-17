FROM eclipse-temurin:17-jre-alpine
COPY build/libs/personio-coding-challenge-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
