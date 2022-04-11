package com.personio.reminders.infra.postgres.container

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.net.InetAddress
import java.net.UnknownHostException
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
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
        Database.connect(dataSource)
    }

    private fun runMigrations() {
        Flyway.configure().dataSource(dataSource).mixed(true).load().migrate()
    }

    internal fun createNewUserWithCredentials(user: String, password: String) {
        dataSource.connection
            .use {
                it.createStatement()
                    .use { statement ->
                        statement.execute("CREATE USER $user with password '$password';")
                        statement.execute(
                            "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $user;"
                        )
                        statement.execute(
                            "GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public to $user;"
                        )
                    }
            }
    }

    internal fun dataSourceWithCredentials(
        usernameCredential: String,
        passwordCredential: String
    ): HikariDataSource {
        return HikariDataSource(
            HikariConfig()
                .apply {
                    jdbcUrl = this@PersonioPostgresContainer.jdbcUrl
                    username = usernameCredential
                    password = passwordCredential
                    driverClassName = this@PersonioPostgresContainer.driverClassName
                }
        )
    }

    override fun getJdbcUrl(): String {
        return "jdbc:postgresql://${getAddress()}:${getMappedPort(POSTGRESQL_PORT)}/" +
            "$databaseName?loggerLevel=OFF"
    }

    private fun getAddress(): String {
        try {
            return System.getenv("GATEWAY") ?: InetAddress.getLocalHost().canonicalHostName
        } catch (e: UnknownHostException) {
            throw RuntimeException("Unable to find HOST hostname", e)
        }
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

/** Returns a new [PersonioPostgresContainer] with which a PostgreSQL instance is run. */
internal fun personioPostgresContainer(): PersonioPostgresContainer =
    PersonioPostgresContainer("postgres:13.1-alpine")
