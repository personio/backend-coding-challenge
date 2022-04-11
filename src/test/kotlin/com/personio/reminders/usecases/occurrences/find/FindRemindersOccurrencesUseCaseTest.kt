package com.personio.reminders.usecases.occurrences.find

import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.postgres.occurrences.InMemoryRemindersOccurrencesRepository
import java.time.Instant
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FindRemindersOccurrencesUseCaseTest {

    @Test
    fun `findAll should find all existing open occurrences for employee`() {
        val employeeId = UUID.randomUUID()
        val occurrenceId = UUID.randomUUID()
        val reminder = MotherObject.reminders().new(employeeId = employeeId)
        val occurrence = MotherObject.occurrences()
            .newFrom(reminder, id = occurrenceId, date = Instant.now(MotherObject.clock))
        val futureOccurrence = MotherObject.occurrences()
            .newFrom(reminder, id = occurrenceId, date = Instant.MAX)
        val acknowledgedOccurrence = MotherObject.occurrences()
            .newFrom(reminder, id = occurrenceId, date = Instant.now(MotherObject.clock), isAcknowledged = true)
        val repo = InMemoryRemindersOccurrencesRepository(
            mutableListOf(reminder),
            mutableListOf(occurrence, futureOccurrence, acknowledgedOccurrence),
            MotherObject.clock
        )
        val useCase = FindRemindersOccurrencesUseCase(repo, MotherObject.clock)

        val foundOccurrences = useCase.findAll(employeeId)

        assertEquals(listOf(occurrence), foundOccurrences)
    }
}
