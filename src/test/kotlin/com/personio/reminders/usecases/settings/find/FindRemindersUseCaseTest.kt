package com.personio.reminders.usecases.settings.find

import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.postgres.settings.InMemoryRemindersSettingsRepository
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FindRemindersUseCaseTest {

    @Test
    fun `findAll should find all existing reminders for an employee`() {
        val employeeId = UUID.randomUUID()
        val otherEmployeeId = UUID.randomUUID()
        val r1 = MotherObject.reminders().new(employeeId = employeeId)
        val r2 = MotherObject.reminders().new(employeeId = employeeId)
        val otherReminder = MotherObject.reminders().new(employeeId = otherEmployeeId)
        val repo = InMemoryRemindersSettingsRepository(
            mutableListOf(r1, r2, otherReminder)
        )
        val useCase = FindRemindersUseCase(repo)

        val foundReminders = useCase.findAll(employeeId)

        assertEquals(listOf(r1, r2), foundReminders)
    }
}
