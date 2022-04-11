package com.personio.reminders.usecases.reminders.create

import com.personio.reminders.domain.reminders.Reminder
import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.postgres.occurrences.InMemoryOccurrencesRepository
import com.personio.reminders.infra.postgres.settings.InMemoryRemindersRepository
import java.time.Instant
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit tests for the CreateReminderUseCase class.
 */
internal class CreateReminderUseCaseTest {

    @Test
    fun `should create reminder with first occurrence`() {
        val employeeId = UUID.randomUUID()
        val reminders = mutableListOf<Reminder>()
        val settingsRepo = InMemoryRemindersRepository(reminders)
        val occurrenceRepo = InMemoryOccurrencesRepository(reminders, mutableListOf(), MotherObject.clock)
        val useCase = CreateReminderUseCase(settingsRepo, occurrenceRepo)

        val date = Instant.now(MotherObject.clock)
        val command = CreateReminderCommand(
            employeeId = employeeId,
            text = "",
            date = date.toString(),
            isRecurring = false,
            recurringInterval = null,
            recurringFrequency = null
        )
        useCase.create(command)

        assertEquals(1, settingsRepo.findAll(employeeId).size)
        assertEquals(1, occurrenceRepo.findAt(date.plusSeconds(1)).size)
    }
}
