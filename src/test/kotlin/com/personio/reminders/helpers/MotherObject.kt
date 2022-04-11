package com.personio.reminders.helpers

import com.personio.reminders.domain.occurrences.Occurrence
import com.personio.reminders.domain.reminders.Reminder
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

/**
 * An object mother is a kind of class used in testing to help create example
 * objects that you use for testing.
 *
 * https://martinfowler.com/bliki/ObjectMother.html
 */
object MotherObject {
    val clock: Clock = Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneOffset.UTC)
    fun reminders() = ReminderMotherObject
    fun occurrences() = OccurrenceMotherObject
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

object OccurrenceMotherObject {

    fun newFrom(
        reminder: Reminder,
        id: UUID = UUID.randomUUID(),
        date: Instant? = null,
        isNotificationSent: Boolean = false,
        isAcknowledged: Boolean = false
    ) = Occurrence(
        id = id,
        reminder = reminder,
        date = date?.toString() ?: reminder.date,
        isAcknowledged = isAcknowledged,
        isNotificationSent = isNotificationSent
    )
}
