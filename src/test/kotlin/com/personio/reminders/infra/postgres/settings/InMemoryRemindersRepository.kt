package com.personio.reminders.infra.postgres.settings

import com.personio.reminders.domain.reminders.Reminder
import com.personio.reminders.domain.reminders.RemindersRepository
import java.util.UUID

/**
 * In-memory repository implementation used in the unit tests.
 */
class InMemoryRemindersRepository(
    private val reminders: MutableCollection<Reminder>
) : RemindersRepository {

    override fun create(reminder: Reminder) {
        reminders.add(reminder)
    }

    override fun findAll(employeeId: UUID): Collection<Reminder> {
        return reminders.filter { it.employeeId == employeeId }
    }

    override fun findBy(id: UUID): Reminder? {
        return reminders.singleOrNull { it.id == id }
    }

    override fun deleteBy(id: UUID): Int {
        val newList: MutableList<Reminder> = ArrayList()
        this.findAll(id).forEach { newList.add(it) }
        return if (reminders.removeAll(newList.toSet())) 1 else  0
    }
}
