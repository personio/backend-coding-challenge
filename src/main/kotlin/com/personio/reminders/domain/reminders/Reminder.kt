package com.personio.reminders.domain.reminders

import com.personio.reminders.usecases.reminders.create.CreateReminderCommand
import java.util.UUID

/**
 * This is a Domain Driven Design Entity for a reminder.
 * This entity is framework-agnostic.
 */
data class Reminder(
    val id: UUID,
    val employeeId: UUID,
    val text: String,
    val date: String,
    val isRecurring: Boolean,
    val recurringInterval: Int?,
    val recurringFrequency: Int?
) {
    companion object {
        fun fromCommand(command: CreateReminderCommand): Reminder {
            return Reminder(
                id = UUID.randomUUID(),
                employeeId = command.employeeId,
                text = command.text,
                date = command.date,
                isRecurring = command.isRecurring,
                recurringInterval = command.recurringInterval,
                recurringFrequency = command.recurringFrequency
            )
        }
    }
}
