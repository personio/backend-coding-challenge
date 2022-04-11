FROM adoptopenjdk:11-jre-hotspot
COPY build/libs/personio-coding-challenge-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
