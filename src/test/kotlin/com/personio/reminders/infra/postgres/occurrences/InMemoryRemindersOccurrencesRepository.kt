package com.personio.reminders.infra.postgres.occurrences

import com.personio.reminders.domain.occurrences.ReminderOccurrence
import com.personio.reminders.domain.occurrences.RemindersOccurrencesRepository
import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.util.addToInstant
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class InMemoryRemindersOccurrencesRepository(
    val reminders: MutableCollection<Reminder>,
    val occurrences: MutableCollection<ReminderOccurrence>,
    val clock: Clock
) : RemindersOccurrencesRepository {
    override fun create(reminderId: UUID, date: String): UUID {
        val reminder = reminders.single { it.id == reminderId }
        val occurrence = ReminderOccurrence(
            id = UUID.randomUUID(),
            reminder = reminder,
            date = date,
            isNotificationSent = false,
            isAcknowledged = false
        )
        occurrences.add(occurrence)
        return occurrence.id
    }

    override fun findAt(instant: Instant): Collection<ReminderOccurrence> {
        return occurrences.filter {
            !it.isAcknowledged &&
                Instant.parse(it.date).isBefore(instant)
        }
    }

    override fun findAt(instant: Instant, employeeId: UUID): Collection<ReminderOccurrence> {
        val reminderIds = reminders.filter {
            it.employeeId == employeeId
        }.map {
            it.id
        }

        return occurrences.filter {
            !it.isAcknowledged &&
                Instant.parse(it.date).isBefore(instant) &&
                reminderIds.contains(it.reminder.id)
        }
    }

    override fun findBy(id: UUID): ReminderOccurrence? {
        return occurrences.singleOrNull { it.id == id }
    }

    override fun getInstantForNextReminderOccurrences(): Map<UUID, Instant> {
        val recurringReminders = reminders.filter {
            it.isRecurring &&
                it.recurringFrequency != null &&
                it.recurringInterval != null
        }

        return recurringReminders.map {
            val lastOccurrence = occurrences.sortedBy { it.date }.lastOrNull()
            val nextOccurrenceInstant = if (lastOccurrence == null) {
                Instant.parse(it.date)
            } else {
                val unit = convertFrequencyToChronoUnit(it.recurringFrequency!!)
                val lastOccurrenceTimestamp = Instant.parse(lastOccurrence.date)
                unit.addToInstant(lastOccurrenceTimestamp, it.recurringInterval!!.toLong(), clock)
            }

            it.id to nextOccurrenceInstant
        }.toMap()
    }

    override fun delete(occurrence: ReminderOccurrence) {
        occurrences.removeIf { it.id == occurrence.id }
    }

    override fun markAsNotified(occurrence: ReminderOccurrence) {
        val occurrenceExists = occurrences.any { it.id == occurrence.id }
        if (!occurrenceExists) return

        val updatedOccurrence = ReminderOccurrence(
            id = occurrence.id,
            reminder = occurrence.reminder,
            date = occurrence.date,
            isNotificationSent = true,
            isAcknowledged = occurrence.isAcknowledged
        )
        occurrences.removeIf { it.id == occurrence.id }
        occurrences.add(updatedOccurrence)
    }

    override fun acknowledge(occurrence: ReminderOccurrence) {
        val occurrenceExists = occurrences.any { it.id == occurrence.id }
        if (!occurrenceExists) return

        val updatedOccurrence = ReminderOccurrence(
            id = occurrence.id,
            reminder = occurrence.reminder,
            date = occurrence.date,
            isNotificationSent = occurrence.isNotificationSent,
            isAcknowledged = true
        )
        occurrences.removeIf { it.id == occurrence.id }
        occurrences.add(updatedOccurrence)
    }

    // Note: Intentionally leaving duplicated code
    private fun convertFrequencyToChronoUnit(frequency: Int): ChronoUnit {
        return when (frequency) {
            1 -> ChronoUnit.DAYS
            2 -> ChronoUnit.WEEKS
            3 -> ChronoUnit.MONTHS
            4 -> ChronoUnit.YEARS
            else -> throw IllegalArgumentException("Invalid frequency provided")
        }
    }
}
