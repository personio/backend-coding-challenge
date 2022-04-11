package com.personio.reminders.api.http.v1

import com.personio.reminders.domain.reminders.exceptions.ReminderNotFoundException
import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.configuration.DefaultTestConfiguration
import com.personio.reminders.usecases.reminders.create.CreateReminderUseCase
import com.personio.reminders.usecases.reminders.find.FindRemindersUseCase
import java.time.Instant
import java.util.UUID
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
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
 * Integration tests focused on the HTTP endpoints for reminders.
 * The use cases used by those endpoints are mocked in these tests
 * because these tests are focused only on granting the interactions between the clients and the HTTP endpoints.
 */
@WebMvcTest(RemindersEndpoint::class)
@AutoConfigureWebClient
@ActiveProfiles("test")
@ContextConfiguration(classes = [DefaultTestConfiguration::class])
class RemindersEndpointTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var createUseCase: CreateReminderUseCase

    @MockBean
    private lateinit var findUseCase: FindRemindersUseCase

    @Test
    fun `create should return 201 status when a reminder is created`() {
        whenever(createUseCase.create(any())).thenReturn(UUID.randomUUID())
        val jsonPayload =
            """
                {
                    "employee_id": "5737a8cc-d04d-4d5d-894c-6ed57e4f8529",
                    "text": "Buy Milk",
                    "date": "2020-01-01"
                }
            """.trimIndent()
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reminders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonPayload)
                    .characterEncoding("utf-8")
            )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist())
    }

    @Test
    fun `create should return 400 status when request payload is incomplete`() {
        val jsonPayload =
            """
                {
                    "employee_id": "5737a8cc-d04d-4d5d-894c-6ed57e4f8529",
                    "text": "Buy milk"
                }
            """.trimIndent()
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reminders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonPayload)
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
    fun `findAll should return 200 with the existing reminders`() {
        val id1 = UUID.fromString("4e8549a3-6d92-4f89-8ccd-6a21ca63cb39")
        val id2 = UUID.fromString("a11fc72b-baee-4980-8c64-d1da015db59d")
        val employeeId = UUID.fromString("8989c9c5-fb2e-45ef-b92a-c279ddf20f25")
        whenever(findUseCase.findAll(employeeId))
            .thenReturn(
                listOf(
                    MotherObject.reminders().new(
                        id1, employeeId, "Buy milk", Instant.ofEpochMilli(1648818004000)
                    ),
                    MotherObject.reminders().new(
                        id2, employeeId, "Hire great engineers", Instant.ofEpochMilli(1648738592000)
                    )
                )
            )
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reminders?employeeId=8989c9c5-fb2e-45ef-b92a-c279ddf20f25")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
            )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(
                MockMvcResultMatchers.content().json(
                    String(
                        ResourceUtils.getFile("classpath:expectations/find-all-reminders.json")
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
                MockMvcRequestBuilders.get("/reminders")
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
}
