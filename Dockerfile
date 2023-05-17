FROM eclipse-temurin:17-jre-alpine
COPY build/libs/personio-coding-challenge.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
