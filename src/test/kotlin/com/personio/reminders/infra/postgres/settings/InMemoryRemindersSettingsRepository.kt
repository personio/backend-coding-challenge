package com.personio.reminders.infra.postgres.settings

import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.domain.settings.RemindersSettingsRepository
import java.util.UUID

class InMemoryRemindersSettingsRepository(
    private val reminders: MutableCollection<Reminder>
) : RemindersSettingsRepository {

    override fun create(reminder: Reminder) {
        reminders.add(reminder)
    }

    override fun findAll(employeeId: UUID): Collection<Reminder> {
        return reminders.filter { it.employeeId == employeeId }
    }

    override fun findBy(id: UUID): Reminder? {
        return reminders.singleOrNull { it.id == id }
    }

    override fun delete(id: UUID) {
        reminders.removeIf { it.id == id }
    }
}
