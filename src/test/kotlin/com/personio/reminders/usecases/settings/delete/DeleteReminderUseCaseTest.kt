package com.personio.reminders.usecases.settings.delete

import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.domain.settings.exceptions.ReminderNotFoundException
import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.postgres.settings.InMemoryRemindersSettingsRepository
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeleteReminderUseCaseTest {

    @Test
    fun `should delete existing reminder`() {
        val reminderId = UUID.randomUUID()
        val employeeId = UUID.randomUUID()
        val repo = InMemoryRemindersSettingsRepository(
            mutableListOf(MotherObject.reminders().new(id = reminderId, employeeId = employeeId))
        )
        val useCase = DeleteReminderUseCase(repo)

        useCase.delete(reminderId)

        assertEquals(listOf<Reminder>(), repo.findAll(employeeId))
    }

    @Test
    fun `should throw when trying to delete a non-existing reminder`() {
        val repo = InMemoryRemindersSettingsRepository(mutableListOf())
        val useCase = DeleteReminderUseCase(repo)

        assertThrows<ReminderNotFoundException> {
            useCase.delete(UUID.randomUUID())
        }
    }
}
