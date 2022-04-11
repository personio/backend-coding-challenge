package com.personio.reminders.domain.reminders

import java.util.UUID

/**
 * This is a Domain Driven Design Repository interface for the reminder's.
 */
interface RemindersRepository {
    fun create(reminder: Reminder)
    fun findAll(employeeId: UUID): Collection<Reminder>
    fun findBy(id: UUID): Reminder?
    fun delete(id: UUID)
}
