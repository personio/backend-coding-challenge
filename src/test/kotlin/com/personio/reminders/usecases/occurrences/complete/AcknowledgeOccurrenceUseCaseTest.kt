package com.personio.reminders.usecases.occurrences.complete

import com.personio.reminders.domain.occurrences.exceptions.ReminderOccurrenceNotFoundException
import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.postgres.occurrences.InMemoryRemindersOccurrencesRepository
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AcknowledgeOccurrenceUseCaseTest {
    @Test
    fun `should acknowledge existing occurrence`() {
        val occurrenceId = UUID.randomUUID()
        val reminder = MotherObject.reminders().new()
        val occurrence = MotherObject.occurrences().newFrom(reminder, id = occurrenceId)
        val repo = InMemoryRemindersOccurrencesRepository(
            mutableListOf(reminder),
            mutableListOf(occurrence),
            MotherObject.clock
        )
        val useCase = AcknowledgeOccurrenceUseCase(repo)

        useCase.acknowledge(occurrenceId)

        assertTrue(repo.findBy(occurrenceId)!!.isAcknowledged)
    }

    @Test
    fun `should not acknowledge non existing occurrence`() {
        val repo = InMemoryRemindersOccurrencesRepository(
            mutableListOf(),
            mutableListOf(),
            MotherObject.clock
        )
        val useCase = AcknowledgeOccurrenceUseCase(repo)

        assertThrows<ReminderOccurrenceNotFoundException> {
            useCase.acknowledge(UUID.randomUUID())
        }
    }
}
