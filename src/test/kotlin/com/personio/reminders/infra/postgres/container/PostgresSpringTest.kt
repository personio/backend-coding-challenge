package com.personio.reminders.infra.postgres.container

import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Identifies test classes requiring a running PostgreSQL instance.
 *
 * The PostgreSQL instance is started with [Test Containers](http://testcontainers.org), which
 * downloads and runs a Docker image containing PostgreSQL. It runs [Flyway](http://flywaydb.org)
 * migrations found in the `src/main/resources/db/migration` directory against the database before
 * running the tests.
 *
 * To use this, just annotate the test class with this annotation.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Testcontainers
@Import(DBTestConfiguration::class, ApplicationShutDownConfiguration::class)
annotation class PostgresSpringTest
