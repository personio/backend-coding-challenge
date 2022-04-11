## Introduction

We have recently added a simple reminder system to Personio. You have been allocated to be part of the team that supports this service.

As you are new to the team, a very simple task will be assigned to you so you have the chance to contribute to the codebase while getting familiar with the service.

## The Reminder Service

The reminder service has a few requirements that have been already implemented and that are listed below for your better understanding.

### 1. Display reminders in the dashboard
The reminder is displayed on the dashboard on the day of the reminder at 00:00. Reminders must display the text entered by the employee, and have a “Done” button. Reminders marked as “done” will not appear anymore. Past reminders, not marked as done, must still appear.

Example: A reminder set for 2018-12-20 will first appear on the dashboard on 2018-12-20 at 00:00. On 2018-12-21, if not marked as “Done” by the employee, the reminder will still appear.

### 2. Send reminders by email
The employee can also optionally enter a time of day (in addition to the date). The reminder will be sent by email on that date & time to the employee’s email address, unless the reminder was already marked as ”done” prior to that time. The precision is 5 minutes (e.g. the employee can select e.g. 14:00, 14:05, 14:10, etc.). The email must be only sent **once** for each reminder, *i.e. even if the reminder is not marked as done, and continue appearing in the dashboard, the email must not repeat every day*.

The acceptable delay for sending the email is **within 5 minutes** of the chosen time.

We are serving employees in very different time zones. We want to make sure that the reminder email is always sent using the correct time zone of each employee.

###  3. Recurring reminders
In addition to one-time reminders described above, the employee is also able to optionally create a recurrence rule. The recurrence rule is limited to 2 parameters: **Frequency** (one of: DAILY, WEEKLY, MONTHLY or YEARLY) and **Interval** (an integer, e.g. every X days, every X months, etc.).

The recurrence is optional, and defined by the employee when they create the reminder. If a recurrence rule is set, a new copy (called **occurrence**) of the reminder will be created at the given frequency & interval. The employee must be able to mark each occurrence as “Done” independently of each other.

> **Example:** A **daily** (freq=DAILY, interval=1) reminder “Buy milk” will appear every day on the dashboard. If the previous day’s reminder is not marked as done, then the employee will see **two** “Buy milk” reminders on his dashboard.

When combining recurring reminders with the “send email at a certain time” feature, the employee will receive one email per occurrence.

## Technical Details

The service is written in Kotlin and uses Spring Boot as an underlying framework. 
We also use [Flyway](http://flywaydb.org/) to manage database migrations and [JOOQ](http://jooq.org/) for writing SQL queries.

### Code Structure

The service strives to follow the clean architecture approach

![clean architecture diagram](docs/clean_architecture.png)

(source: https://medium.com/swlh/clean-architecture-a-little-introduction-be3eac94c5d1)

All API endpoints are located in `com.personio.reminders.api` package. The OpeanAPI documentation for the API is available in [openapi.yaml](openapi.yaml).

Interfacing with external systems is limited to `com.personio.reminders.infrastructure` package.
Use cases contain the application logic and depend only on domain entities.

## Running the service with an IDE (i.e. IntelliJ IDEA)

Clone the service to your computer using the command below:
```sh
git clone git@github.com:personio/backend-coding-challenge.git
cd backend-coding-challenge
```

Generate the [JOOQ](https://www.jooq.org/) classes from the database schema using the command below. This required docker daemon running on the machine.
```sh
./gradlew generateJooqClasses
```

If running on Windows or M1 Mac, an additional env variable setting might be required for the above command to run successfully: `export DOCKER_HOST=npipe:////./pipe/docker_engine`.

Run the Postgres database using the command below:
```sh
docker-compose up db
```

Import the service in your favourite IDE, i.e. IntelliJ IDEA and execute the main class `com.personio.reminders.Application` to start the service.

## Running the service without an IDE

Clone the service to your computer using the command below:
```sh
git clone git@github.com:personio/backend-coding-challenge.git
cd backend-coding-challenge
```

Generate the [JOOQ](https://www.jooq.org/) classes from the database schema using the command below:
```sh
./gradlew generateJooqClasses
```

Start/update the service using the command below:

```sh
./gradlew bootJar && docker-compose up -d --build
```

Stop the service using the command below:

```sh
docker-compose down
```

Watch the application logs using the command below

```sh
docker logs personio-reminders-service -f
```

## Interacting with the API

### Reminders endpoints 

Run the following cURL command to create a new reminder in the service.

```sh
curl -X POST \
    -H "Content-Type: application/json" \
    -d '{
      "employee_id": "de3ffa44-7b34-46a6-a329-d3b8fb95399e",
      "text": "Remind me to buy some oat milk!",
      "date": "2022-04-28T17:30:00.00Z",
      "is_recurring": false
    }' http://localhost:8080/reminders -v
```

Run the following cURL command to retrieve all the reminders of a specific employee.

```sh
curl "http://localhost:8080/reminders?employeeId=de3ffa44-7b34-46a6-a329-d3b8fb95399e" -v
```

### Occurrences endpoints

Run the following cURL command to retrieve all the occurrences of a specific employee.

```sh
curl "http://localhost:8080/occurrences?employeeId=de3ffa44-7b34-46a6-a329-d3b8fb95399e" -v
```

Run the following cURL command to acknowledge a specific occurrence. Change the id in the URL by your occurrence's id.

```sh
curl -X PUT "http://localhost:8080/occurrences/ce0f42b3-0fdb-4f91-8ce3-a853c1f06c06" -v
```
