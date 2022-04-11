package com.personio.reminders.infra.postgres.container

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait

/**
 * A test container which runs a PostgreSQL instance.
 *
 * This sets up a [DataSource] for access to the database and configures Exposed to use it. The
 * caller may use methods on this container to clear database tables and load table content in order
 * to assert on it.
 */
class PersonioPostgresContainer(imageName: String) :
    PostgreSQLContainer<PersonioPostgresContainer>(imageName) {

    private lateinit var _dataSource: HikariDataSource

    init {
        withDatabaseName("test-database")
        withUsername("postgres")
        withPassword("postgres")
    }

    override fun start() {
        if (!isRunning) {
            super.start()
            waitingFor(
                Wait.forLogMessage(".*database system is ready to accept connections*\\n", 1)
            )
            initializeDataSource()
            runMigrations()
        }
    }

    private fun initializeDataSource() {
        val config =
            HikariConfig()
                .apply {
                    jdbcUrl = this@PersonioPostgresContainer.jdbcUrl
                    username = this@PersonioPostgresContainer.username
                    password = this@PersonioPostgresContainer.password
                    driverClassName = this@PersonioPostgresContainer.driverClassName
                }
        _dataSource = HikariDataSource(config)
    }

    private fun runMigrations() {
        Flyway.configure().dataSource(dataSource).mixed(true).load().migrate()
    }

    override fun stop() {
        _dataSource.close()
        super.stop()
    }

    /**
     * A [TestDatabase] with which tests can interact with the connected PostgreSQL instance.
     *
     * If the container has not been started when this property is accessed, it is started. This may
     * be the case when injecting this value as a parameter into a test class whose lifecycle is per
     * class rather than per test.
     */
    val testDatabase
        get() = TestDatabase(dataSource)

    /**
     * A [DataSource] which may be injected into test instances.
     *
     * If the container has not been started when this property is accessed, it is started. This may
     * be the case when injecting this value as a parameter into a test class whose lifecycle is per
     * class rather than per test.
     */
    val dataSource: DataSource
        get() {
            if (!isRunning) {
                start()
            }
            return _dataSource
        }
}
