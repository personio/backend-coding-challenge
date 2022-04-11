package com.personio.reminders.usecases.settings.create

import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.helpers.MotherObject
import java.time.Instant
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CreateReminderCommandTest {

    @Test
    fun `should generate reminder from command`() {
        val command = CreateReminderCommand(
            employeeId = UUID.randomUUID(),
            text = "",
            date = Instant.now(MotherObject.clock).toString(),
            isRecurring = false,
            recurringInterval = null,
            recurringFrequency = null
        )

        val reminder = Reminder.fromCommand(command)

        assertEquals(command.employeeId, reminder.employeeId)
        assertEquals(command.text, reminder.text)
        assertEquals(command.date, reminder.date)
        assertEquals(command.isRecurring, reminder.isRecurring)
        assertEquals(command.recurringInterval, reminder.recurringInterval)
        assertEquals(command.recurringFrequency, reminder.recurringFrequency)
    }
}
