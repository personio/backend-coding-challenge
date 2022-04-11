package com.personio.reminders.usecases.occurrences.complete

import com.personio.reminders.domain.occurrences.exceptions.OccurrenceNotFoundException
import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.postgres.occurrences.InMemoryOccurrencesRepository
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for the AcknowledgeOccurrenceUseCase class.
 */
internal class AcknowledgeOccurrenceUseCaseTest {
    @Test
    fun `should acknowledge existing occurrence`() {
        val occurrenceId = UUID.randomUUID()
        val reminder = MotherObject.reminders().new()
        val occurrence = MotherObject.occurrences().newFrom(reminder, id = occurrenceId)
        val repo = InMemoryOccurrencesRepository(
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
        val repo = InMemoryOccurrencesRepository(
            mutableListOf(),
            mutableListOf(),
            MotherObject.clock
        )
        val useCase = AcknowledgeOccurrenceUseCase(repo)

        assertThrows<OccurrenceNotFoundException> {
            useCase.acknowledge(UUID.randomUUID())
        }
    }
}
