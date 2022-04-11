package com.personio.reminders.api.http.v1

import com.personio.reminders.domain.occurrences.exceptions.OccurrenceNotFoundException
import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.configuration.DefaultTestConfiguration
import com.personio.reminders.usecases.occurrences.complete.AcknowledgeOccurrenceUseCase
import com.personio.reminders.usecases.occurrences.find.FindOccurrencesUseCase
import java.time.Instant
import java.util.UUID
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.ResourceUtils

/**
 * Integration tests focused on the HTTP endpoints for reminder's occurrences.
 * The use cases used by those endpoints are mocked in these tests
 * because these tests are focused only on granting the interactions between the clients and the HTTP endpoints.
 */
@WebMvcTest(OccurrencesEndpoint::class)
@AutoConfigureWebClient
@ActiveProfiles("test")
@ContextConfiguration(classes = [DefaultTestConfiguration::class])
class OccurrencesEndpointTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var findUseCase: FindOccurrencesUseCase

    @MockBean
    private lateinit var acknowledgeUseCase: AcknowledgeOccurrenceUseCase

    @Test
    fun `findAll should return 200 with the existing occurrences`() {
        val id1 = UUID.fromString("4e8549a3-6d92-4f89-8ccd-6a21ca63cb39")
        val id2 = UUID.fromString("a11fc72b-baee-4980-8c64-d1da015db59d")
        val employeeId = UUID.fromString("8989c9c5-fb2e-45ef-b92a-c279ddf20f25")
        whenever(findUseCase.findAll(employeeId))
            .thenReturn(
                listOf(
                    MotherObject.occurrences().newFrom(
                        MotherObject.reminders().new(
                            id1, employeeId, "Buy milk", Instant.ofEpochMilli(1648818004000)
                        ),
                        UUID.fromString("aac7f6df-0a65-460d-bb53-6d422d3db797")
                    ),
                    MotherObject.occurrences().newFrom(
                        MotherObject.reminders().new(
                            id2, employeeId, "Hire great engineers", Instant.ofEpochMilli(1648738592000)
                        ),
                        UUID.fromString("29e43fba-8504-448a-93e7-1c5f7a7346d5")
                    )
                )
            )
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get("/occurrences?employeeId=8989c9c5-fb2e-45ef-b92a-c279ddf20f25")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
            )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(
                MockMvcResultMatchers.content().json(
                    String(
                        ResourceUtils.getFile("classpath:expectations/find-all-occurrences.json")
                            .inputStream()
                            .readAllBytes()
                    )
                )
            )
    }

    @Test
    fun `findAll should return 400 if employee id is not provided`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/occurrences")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
            )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.BAD_REQUEST.value()))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.errors[0].status")
                    .value(HttpStatus.BAD_REQUEST.toString())
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.errors[0].title")
                    .value(
                        Matchers.containsString("Invalid input provided")
                    )
            )
    }

    @Test
    fun `acknowledge should return 204 status when an existing reminder is deleted`() {
        doNothing().whenever(acknowledgeUseCase)
            .acknowledge(id = UUID.fromString("a2999215-db7d-49e9-91eb-15038e50182c"))
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/occurrences/a2999215-db7d-49e9-91eb-15038e50182c")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
            )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.NO_CONTENT.value()))
            .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist())
    }

    @Test
    fun `acknowledge should return 404 status when trying to acknowledge a non-existing occurrence`() {
        whenever(acknowledgeUseCase.acknowledge(id = UUID.fromString("a2999215-db7d-49e9-91eb-15038e50182c")))
            .thenThrow(OccurrenceNotFoundException())
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/occurrences/a2999215-db7d-49e9-91eb-15038e50182c")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
            )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.NOT_FOUND.value()))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.errors[0].status")
                    .value(HttpStatus.NOT_FOUND.toString())
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.errors[0].title")
                    .value(
                        Matchers.containsString("Occurrence with such id is not found")
                    )
            )
    }
}
