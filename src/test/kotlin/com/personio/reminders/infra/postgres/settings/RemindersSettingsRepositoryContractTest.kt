package com.personio.reminders.infra.postgres.settings

import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.domain.settings.RemindersSettingsRepository
import com.personio.reminders.helpers.MotherObject
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.Test

interface RemindersSettingsRepositoryContractTest {

    fun subjectWithData(
        existingReminders: Collection<Reminder>
    ): RemindersSettingsRepository

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
