package com.personio.reminders.infra.postgres.occurrences

import com.personio.reminders.domain.occurrences.ReminderOccurrence
import com.personio.reminders.domain.occurrences.RemindersOccurrencesRepository
import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.helpers.MotherObject
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test

private const val FREQUENCY_DAY = 1

interface RemindersOccurrencesRepositoryContractTest {

    fun subjectWithData(
        existingReminders: Collection<Reminder>,
        existingOccurrences: Collection<ReminderOccurrence>,
        clock: Clock
    ): RemindersOccurrencesRepository

    @Test
    fun `create should create a new occurrence`() {
        val reminder = MotherObject.reminders().new()
        val repo = subjectWithData(
            mutableListOf(
                reminder
            ),
            mutableListOf(), MotherObject.clock
        )

        val occurrenceId = repo.create(reminder.id, reminder.date)

        assertNotNull(repo.findBy(occurrenceId))
    }

    @Test
    fun `find at should return non acknowledged occurrences`() {
        val reminder = MotherObject.reminders().new()
        val occurrence1 = MotherObject.occurrences().newFrom(
            reminder,
            isAcknowledged = true
        )
        val occurrence2 = MotherObject.occurrences().newFrom(
            reminder,
            date = Instant.now(MotherObject.clock).plus(1, ChronoUnit.DAYS)
        )
        val repo = subjectWithData(listOf(reminder), listOf(occurrence1, occurrence2), MotherObject.clock)

        val foundOccurrences = repo.findAt(Instant.now(MotherObject.clock).plus(2, ChronoUnit.DAYS))

        assertEquals(1, foundOccurrences.size)
    }

    @Test
    fun `find at should return non acknowledged occurrences for specific employee`() {
        val reminderForEmployee1 = MotherObject.reminders().new()
        val reminderForEmployee2 = MotherObject.reminders().new()
        val occurrenceForEmployee1 = MotherObject.occurrences().newFrom(reminderForEmployee1)
        val occurrenceForEmployee2 = MotherObject.occurrences().newFrom(reminderForEmployee2)
        val repo = subjectWithData(
            listOf(
                reminderForEmployee1,
                reminderForEmployee2
            ),
            listOf(
                occurrenceForEmployee1,
                occurrenceForEmployee2
            ),
            MotherObject.clock
        )

        val foundOccurrences = repo.findAt(
            Instant.now(MotherObject.clock).plusSeconds(1),
            reminderForEmployee1.employeeId
        )

        assertEquals(reminderForEmployee1.id, foundOccurrences.single().reminder.id)
    }

    @Test
    fun `should detect reminders to recur`() {
        val date = Instant.now(MotherObject.clock)
        val recurringReminder = MotherObject.reminders().new(
            date = date,
            isRecurring = true,
            recurringFrequency = FREQUENCY_DAY, // 1 DAY
            recurringInterval = 1
        )
        val nonRecurringReminder = MotherObject.reminders().new(
            date = date,
            isRecurring = false
        )
        val repo = subjectWithData(
            listOf(recurringReminder, nonRecurringReminder),
            listOf(
                MotherObject.occurrences().newFrom(reminder = recurringReminder, isAcknowledged = true),
                MotherObject.occurrences().newFrom(reminder = nonRecurringReminder, isAcknowledged = true)
            ),
            MotherObject.clock
        )

        val remindersToRecur = repo.getInstantForNextReminderOccurrences()

        val expectedOccurrenceTime = date.plus(1, ChronoUnit.DAYS)

        assertEquals(1, remindersToRecur.size)
        assertEquals(expectedOccurrenceTime, remindersToRecur[recurringReminder.id])
    }

    @Test
    fun `delete should remove existing reminder`() {
        val reminder = MotherObject.reminders().new()
        val occurrence = MotherObject.occurrences().newFrom(reminder)
        val repo = subjectWithData(listOf(reminder), listOf(occurrence), MotherObject.clock)

        repo.delete(occurrence)
        val foundOccurrences = repo.findAt(Instant.now(MotherObject.clock).plusSeconds(1))

        assertEquals(0, foundOccurrences.size)
    }

    @Test
    fun `should mark as acknowledged`() {
        val reminder = MotherObject.reminders().new()
        val occurrence = MotherObject.occurrences().newFrom(reminder, isAcknowledged = false)
        val repo = subjectWithData(listOf(reminder), listOf(occurrence), MotherObject.clock)

        repo.acknowledge(occurrence)
        val foundOccurrence = repo.findBy(occurrence.id)

        assertTrue(foundOccurrence!!.isAcknowledged)
    }

    @Test
    fun `should mark as notified`() {
        val reminder = MotherObject.reminders().new()
        val occurrence = MotherObject.occurrences().newFrom(reminder, isNotificationSent = false)
        val repo = subjectWithData(listOf(reminder), listOf(occurrence), MotherObject.clock)

        repo.markAsNotified(occurrence)
        val foundOccurrence = repo.findBy(occurrence.id)

        assertTrue(foundOccurrence!!.isNotificationSent)
    }
}
