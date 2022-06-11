package com.personio.reminders.infra.postgres.settings

import com.personio.reminders.domain.reminders.Reminder
import com.personio.reminders.domain.reminders.RemindersRepository
import com.personio.reminders.helpers.MotherObject
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * This test interface is used by the "postgres repository implementation" and by the "in-memory repository implementation".
 * It tests the real repository (Postgres) and grants that the "in-memory" repository, used in the unit tests, is also compliant to the real one.
 */
interface RemindersRepositoryContractTest {

    fun subjectWithData(
        existingReminders: Collection<Reminder>
    ): RemindersRepository

    @Test
    fun `create should create a new reminder`() {
        val repo = subjectWithData(emptyList())

        val reminder = MotherObject.reminders().new()
        repo.create(reminder)

        val foundReminder = repo.findBy(reminder.id)
        assertNotNull(foundReminder)
    }

    @Test
    fun `find all should return existing reminders`() {
        val employeeId = UUID.randomUUID()
        val reminder = MotherObject.reminders().new(employeeId = employeeId)
        val repo = subjectWithData(listOf(reminder))

        val existingReminders = repo.findAll(employeeId)

        assertEquals(1, existingReminders.size)
    }

    @Test
    fun `find by should return existing reminder`() {
        val reminder = MotherObject.reminders().new()
        val repo = subjectWithData(listOf(reminder))

        val existingReminder = repo.findBy(reminder.id)

        assertEquals(reminder.text, existingReminder?.text)
    }

    @Test
    fun `delete should remove existing reminder`() {
        val reminder = MotherObject.reminders().new()
        val repo = subjectWithData(listOf(reminder))

        repo.delete(reminder.id)
        val existingReminders = repo.findAll(reminder.employeeId)

        assertEquals(0, existingReminders.size)
    }
}
