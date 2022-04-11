package com.personio.reminders.domain.occurrences

import java.time.Instant
import java.util.UUID

interface RemindersOccurrencesRepository {
    fun create(reminderId: UUID, date: String): UUID
    fun findAt(instant: Instant): Collection<ReminderOccurrence>
    fun findAt(instant: Instant, employeeId: UUID): Collection<ReminderOccurrence>
    fun findBy(id: UUID): ReminderOccurrence?
    fun getInstantForNextReminderOccurrences(): Map<UUID, Instant>
    fun delete(occurrence: ReminderOccurrence)
    fun markAsNotified(occurrence: ReminderOccurrence)
    fun acknowledge(occurrence: ReminderOccurrence)
}
