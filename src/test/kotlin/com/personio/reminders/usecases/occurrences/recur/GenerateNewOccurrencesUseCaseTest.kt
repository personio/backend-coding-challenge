package com.personio.reminders.usecases.occurrences.recur

import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.postgres.occurrences.InMemoryRemindersOccurrencesRepository
import com.personio.reminders.util.addToInstant
import java.time.Instant
import java.time.temporal.ChronoUnit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.slf4j.helpers.NOPLogger

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class GenerateNewOccurrencesUseCaseTest {

    @Test
    fun `should generate first occurrence for recurring reminder when not present`() {
        val reminderDate = Instant.now(MotherObject.clock).minus(1, ChronoUnit.DAYS)
        val reminder = MotherObject.reminders().new(
            text = "Buy the milk",
            date = reminderDate,
            isRecurring = true,
            recurringFrequency = 1,
            recurringInterval = 1
        )
        val occurrencesRepository = InMemoryRemindersOccurrencesRepository(
            mutableListOf(reminder),
            mutableListOf(),
            MotherObject.clock
        )
        val useCase = GenerateNewOccurrencesUseCase(
            NOPLogger.NOP_LOGGER,
            occurrencesRepository
        )

        useCase.generateNewOccurrences()

        assertEquals(1, occurrencesRepository.occurrences.size)
        assertEquals(reminderDate.toString(), occurrencesRepository.occurrences.single().date)
    }

    @Test
    fun `should generate occurrence for recurring reminder`() {
        val reminderDate = Instant.now(MotherObject.clock).minus(1, ChronoUnit.DAYS)
        val reminder = MotherObject.reminders().new(
            text = "Buy the milk",
            date = reminderDate,
            isRecurring = true,
            recurringFrequency = 1,
            recurringInterval = 1
        )
        val firstOccurrence = MotherObject.occurrences().newFrom(
            reminder,
            isNotificationSent = true,
            isAcknowledged = true
        )
        val occurrencesRepository = InMemoryRemindersOccurrencesRepository(
            mutableListOf(reminder),
            mutableListOf(firstOccurrence),
            MotherObject.clock
        )
        val useCase = GenerateNewOccurrencesUseCase(
            NOPLogger.NOP_LOGGER,
            occurrencesRepository
        )

        useCase.generateNewOccurrences()

        assertEquals(2, occurrencesRepository.occurrences.size)
    }

    @Test
    fun `should not generate occurrence for non recurring reminder`() {
        val reminderDate = Instant.now(MotherObject.clock).minus(1, ChronoUnit.DAYS)
        val reminder = MotherObject.reminders().new(
            text = "Finish the coding challenge",
            date = reminderDate
        )
        val firstOccurrence = MotherObject.occurrences().newFrom(
            reminder,
            isNotificationSent = true
        )
        val occurrencesRepository = InMemoryRemindersOccurrencesRepository(
            mutableListOf(reminder),
            mutableListOf(firstOccurrence),
            MotherObject.clock
        )
        val useCase = GenerateNewOccurrencesUseCase(
            NOPLogger.NOP_LOGGER,
            occurrencesRepository
        )

        useCase.generateNewOccurrences()

        assertEquals(1, occurrencesRepository.occurrences.size)
    }

    @ParameterizedTest
    @MethodSource("supportedRecurringFrequencies")
    fun `should generate occurrences for supported frequencies`(
        frequency: Int,
        correspondingChronoUnit: ChronoUnit
    ) {
        val interval = 1L
        val reminderDate = correspondingChronoUnit.addToInstant(
            Instant.now(MotherObject.clock),
            interval * -1,
            MotherObject.clock
        )
        val reminder = MotherObject.reminders().new(
            text = "Meeting",
            date = reminderDate,
            isRecurring = true,
            recurringFrequency = frequency,
            recurringInterval = interval.toInt()
        )
        val firstOccurrence = MotherObject.occurrences().newFrom(
            reminder,
            isNotificationSent = true,
            isAcknowledged = true
        )
        val occurrencesRepository = InMemoryRemindersOccurrencesRepository(
            mutableListOf(reminder),
            mutableListOf(firstOccurrence),
            MotherObject.clock
        )
        val useCase = GenerateNewOccurrencesUseCase(
            NOPLogger.NOP_LOGGER,
            occurrencesRepository
        )

        useCase.generateNewOccurrences()

        val expectedRecurrenceDate = correspondingChronoUnit.addToInstant(
            reminderDate,
            interval,
            MotherObject.clock
        ).toString()
        val generatedOccurrence = occurrencesRepository.occurrences.single { it.id != firstOccurrence.id }
        assertEquals(expectedRecurrenceDate, generatedOccurrence.date)
    }

    private fun supportedRecurringFrequencies() = listOf(
        Arguments.of(1, ChronoUnit.DAYS),
        Arguments.of(2, ChronoUnit.WEEKS),
        Arguments.of(3, ChronoUnit.MONTHS),
        Arguments.of(4, ChronoUnit.YEARS),
    )
}
