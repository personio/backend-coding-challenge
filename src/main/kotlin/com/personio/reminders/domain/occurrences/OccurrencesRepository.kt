package com.personio.reminders.domain.occurrences

import java.time.Instant
import java.util.UUID

/**
 * This is a Domain Driven Design Repository interface for the reminder's occurrences.
 */
interface OccurrencesRepository {
    fun create(reminderId: UUID, date: String): UUID
    fun findAt(instant: Instant): Collection<Occurrence>
    fun findAt(instant: Instant, employeeId: UUID): Collection<Occurrence>
    fun findBy(id: UUID): Occurrence?
    fun getInstantForNextReminderOccurrences(): Map<UUID, Instant>
    fun delete(occurrence: Occurrence)
    fun markAsNotified(occurrence: Occurrence)
    fun acknowledge(occurrence: Occurrence)
}
