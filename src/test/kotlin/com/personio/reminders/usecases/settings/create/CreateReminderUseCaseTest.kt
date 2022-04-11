package com.personio.reminders.usecases.settings.create

import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.postgres.occurrences.InMemoryRemindersOccurrencesRepository
import com.personio.reminders.infra.postgres.settings.InMemoryRemindersSettingsRepository
import java.time.Instant
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CreateReminderUseCaseTest {

    @Test
    fun `should create reminder with first occurrence`() {
        val employeeId = UUID.randomUUID()
        val reminders = mutableListOf<Reminder>()
        val settingsRepo = InMemoryRemindersSettingsRepository(reminders)
        val occurrenceRepo = InMemoryRemindersOccurrencesRepository(reminders, mutableListOf(), MotherObject.clock)
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
