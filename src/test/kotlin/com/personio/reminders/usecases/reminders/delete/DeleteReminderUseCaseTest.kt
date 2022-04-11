package com.personio.reminders.usecases.reminders.delete

import com.personio.reminders.domain.reminders.Reminder
import com.personio.reminders.domain.reminders.exceptions.ReminderNotFoundException
import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.postgres.settings.InMemoryRemindersRepository
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for the DeleteReminderUseCase class.
 */
internal class DeleteReminderUseCaseTest {

    @Test
    fun `should delete existing reminder`() {
        val reminderId = UUID.randomUUID()
        val employeeId = UUID.randomUUID()
        val repo = InMemoryRemindersRepository(
            mutableListOf(MotherObject.reminders().new(id = reminderId, employeeId = employeeId))
        )
        val useCase = DeleteReminderUseCase(repo)

        useCase.delete(reminderId)

        assertEquals(listOf<Reminder>(), repo.findAll(employeeId))
    }

    @Test
    fun `should throw when trying to delete a non-existing reminder`() {
        val repo = InMemoryRemindersRepository(mutableListOf())
        val useCase = DeleteReminderUseCase(repo)

        assertThrows<ReminderNotFoundException> {
            useCase.delete(UUID.randomUUID())
        }
    }
}
