# Reminders Service

### Start from a clean state

```sh
./gradlew generateJooqClasses
./gradlew bootJar 
docker-compose up
```

### Start/Update service

```sh
./gradlew bootJar && docker-compose up -d --build
```

### Stop service 

```sh
docker-compose down
```

### Watch application logs
```sh
docker logs personio-reminders-service -f
```

