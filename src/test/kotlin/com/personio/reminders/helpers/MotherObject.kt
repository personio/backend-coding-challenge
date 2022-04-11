package com.personio.reminders.helpers

import com.personio.reminders.domain.occurrences.ReminderOccurrence
import com.personio.reminders.domain.settings.Reminder
import java.time.Clock
import java.time.Instant
import java.util.UUID

object MotherObject {
    val clock = Clock.systemUTC()
    fun reminders() = ReminderMotherObject
    fun occurrences() = ReminderOccurrenceMotherObject
}

object ReminderMotherObject {

    fun new(
        id: UUID = UUID.randomUUID(),
        employeeId: UUID = UUID.randomUUID(),
        text: String = "Send report to Max Mustermann",
        date: Instant = Instant.now(MotherObject.clock),
        isRecurring: Boolean = false,
        recurringInterval: Int? = null,
        recurringFrequency: Int? = null
    ) = Reminder(
        id = id,
        employeeId = employeeId,
        text = text,
        date = date.toString(),
        isRecurring = isRecurring,
        recurringInterval = recurringInterval,
        recurringFrequency = recurringFrequency
    )
}

object ReminderOccurrenceMotherObject {

    fun newFrom(
        reminder: Reminder,
        id: UUID = UUID.randomUUID(),
        date: Instant? = null,
        isNotificationSent: Boolean = false,
        isAcknowledged: Boolean = false
    ) = ReminderOccurrence(
        id = id,
        reminder = reminder,
        date = date?.toString() ?: reminder.date,
        isAcknowledged = isAcknowledged,
        isNotificationSent = isNotificationSent
    )
}
