package com.personio.reminders.infra.configuration

import com.personio.reminders.infra.postgres.container.PostgresSpringTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.availability.ApplicationAvailability
import org.springframework.boot.availability.LivenessState
import org.springframework.boot.availability.ReadinessState
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Integration tests for the "liveness" and "readiness" operational endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@PostgresSpringTest
@ContextConfiguration(classes = [DefaultTestConfiguration::class])
class ApplicationAvailabilityIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val applicationAvailability: ApplicationAvailability
) {

    @Test
    fun `microservice should have liveness endpoint available`() {
        assertEquals(LivenessState.CORRECT, applicationAvailability.livenessState)
        mockMvc.perform(get("/health-check/liveness"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("UP"))
    }

    @Test
    fun `microservice should have readiness endpoint available`() {
        assertEquals(ReadinessState.ACCEPTING_TRAFFIC, applicationAvailability.readinessState)
        mockMvc.perform(get("/health-check/readiness"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("UP"))
    }
}
