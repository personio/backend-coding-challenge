package com.personio.reminders

import com.personio.reminders.infra.configuration.DefaultTestConfiguration
import com.personio.reminders.infra.postgres.container.PostgresSpringTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ActiveProfiles("test")
@PostgresSpringTest
@ContextConfiguration(classes = [DefaultTestConfiguration::class])
internal class ApplicationContextTest {

    @Test
    internal fun `Application context is correctly set up`() {
        assertTrue(true)
    }
}
