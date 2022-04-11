package com.personio.reminders.infra.postgres.occurrences

import com.personio.reminders.domain.occurrences.Occurrence
import com.personio.reminders.domain.occurrences.OccurrencesRepository
import com.personio.reminders.domain.reminders.Reminder
import com.personio.reminders.helpers.MotherObject
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

private const val FREQUENCY_DAY = 1

/**
 * This test interface is used by the "postgres repository implementation" and by the "in-memory repository implementation".
 * It tests the real repository (Postgres) and grants that the "in-memory" repository, used in the unit tests, is also compliant to the real one.
 */
interface OccurrencesRepositoryContractTest {

    fun subjectWithData(
        existingReminders: Collection<Reminder>,
        existingOccurrences: Collection<Occurrence>,
        clock: Clock
    ): OccurrencesRepository

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
    fun `find at should return occurrences for a specific employee`() {
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

        // Truncate to microseconds because the PostgreSQL TIMESTAMP type doesn't support nanosecond precision
        val expectedOccurrenceTime = date.plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MICROS)

        assertEquals(1, remindersToRecur.size)
        assertEquals(
            expectedOccurrenceTime,
            remindersToRecur[recurringReminder.id]!!.truncatedTo(ChronoUnit.MICROS)
        )
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
