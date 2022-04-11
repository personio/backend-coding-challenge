package com.personio.reminders.infra.postgres.container

import javax.sql.DataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

/**
 * Exposes PersonioPostgresContainer, DataSource and DSLContext as beans which can be used in Tests.
 * It is added implicitly as part of @PostgresSpringTest
 *
 */
@TestConfiguration
class DBTestConfiguration {
    /**
     * Adds PersonioPostgresContainer to the Spring Context
     * @return PersonioPostgresContainer
     */
    @Bean
    fun testContainer(): PersonioPostgresContainer {
        return PersonioPostgresContainer("postgres:13.1-alpine")
    }

    @Bean
    fun testDatabase(testContainer: PersonioPostgresContainer): TestDatabase {
        return testContainer.testDatabase
    }

    /**
     * Adds DataSource to the Spring Context
     * @return DataSource
     */
    @Bean
    fun dataSource(testContainer: PersonioPostgresContainer): DataSource {
        return testContainer.dataSource
    }

    /**
     * Adds DSLContext to the Spring Context
     * @return DSLContext
     */
    @Bean
    fun dslContext(dataSource: DataSource): DSLContext {
        return DefaultDSLContext(DefaultConfiguration().set(dataSource).set(SQLDialect.POSTGRES))
    }
}
