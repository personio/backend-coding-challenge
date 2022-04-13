## Introduction

We have reccently added a simple reminder system to Personio. You have been allocated to be part of the team that supports this service.

As you are new to the team, a very simple task has been assigned to you so you have the chance to contribute to the codebase while getting familiar with the project.

## The Reminder System

The reminder system has a few requirements that have been already implemented and that are listed below for your better understanding.

### 1. Display reminders in the dashboard
The reminder is displayed on the dashboard on the day of the reminder at 00:00. Reminders must display the text entered by the user, and have a “Done” button. Reminders marked as “done” will not appear anymore. Past reminders, not marked as done, must still appear.

Example: A reminder set for 2018-12-20 will first appear on the dashboard on 2018-12-20 at 00:00. On 2018-12-21, if not marked as “Done” by the user, the reminder will still appear.

### 2. Send reminders by email
The user can also optionally enter a time of day (in addition to the date). The reminder will be sent by email on that date & time to the user’s email address, unless the reminder was already marked as ”done” prior to that time. The precision is 5 minutes (e.g. the user can select e.g. 14:00, 14:05, 14:10, etc.). The email must be only sent **once** for each reminder, *i.e. even if the reminder is not marked as done, and continue appearing in the dashboard, the email must not repeat every day*.

The acceptable delay for sending the email is **within 5 minutes** of the chosen time.

We are serving users in very different time zones. We want to make sure that the reminder email is always sent using the correct time zone of each user.

###  3. Recurring reminders
In addition to one-time reminders described above, the user is also able to optionally create a recurrence rule. The recurrence rule is limited to 2 parameters: **Frequency** (one of: DAILY, WEEKLY, MONTHLY or YEARLY) and **Interval** (an integer, e.g. every X days, every X months, etc.).

The recurrence is optional, and defined by the user when he creates the reminder. If a recurrence rule is set, a new copy (called **occurrence**) of the reminder will be created at the given frequency & interval. The user must be able to mark each occurrence as “Done” independently from each other.

> **Example:** A **daily** (freq=DAILY, interval=1) reminder “Buy milk” will appear every day on the dashboard. If the previous day’s reminder is not marked as done, then the user will see **two** “Buy milk” reminders on his dashboard.

When combining recurring reminders with the “send email at a certain time” feature, the user will receive one email per occurrence.

###  4. Technical requirements
We want to build this as a microservice, and add as little code as possible to the legacy codebase.

We are using a 3rd party provider to deliver emails, that exposes both a SMTP API, and a HTTP/REST API.

## Technical Details

The service is written in Kotlin and uses Spring Boot as an underlying framework. 
We also use [Flyway](http://flywaydb.org/) to manage database migrations and [JOOQ](http://jooq.org/) for writing SQL queries.

### Code Structure

The service strives to follow the clean architecture approach

![clean architecture diagram](docs/clean_architecture.png)

All API endpoints are located in `com.personio.reminders.api` package. The OpeanAPI documentation for the API is available in [openapi.yaml](openapi.yaml).

Interfacing with external systems is limited to `com.personio.reminders.infrastructure` package.
Use cases contain the application logic and depend only on domain entities.

## Running the service

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

