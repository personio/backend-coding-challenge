package com.personio.reminders.domain.settings

import java.util.UUID

interface RemindersSettingsRepository {
    fun create(reminder: Reminder)
    fun findAll(employeeId: UUID): Collection<Reminder>
    fun findBy(id: UUID): Reminder?
    fun delete(id: UUID)
}
