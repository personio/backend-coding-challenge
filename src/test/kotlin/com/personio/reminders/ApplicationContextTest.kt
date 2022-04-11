package com.personio.reminders

import com.personio.reminders.infra.configuration.DefaultTestConfiguration
import com.personio.reminders.infra.postgres.container.PostgresSpringTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * This is an integration test, these tests are useful to expose Spring configuration problems
 * once they start the whole Spring Framework during their execution.
 */
@SpringBootTest
@ActiveProfiles("test")
@PostgresSpringTest
@ContextConfiguration(classes = [DefaultTestConfiguration::class])
internal class ApplicationContextTest {

    /**
     * This test will break when there is a configuration problem in Spring.
     * This kind of problem is not catch by unit tests, only by these integration tests.
     */
    @Test
    internal fun `Application context is correctly set up`() {
        assertTrue(true)
    }
}
