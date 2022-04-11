package com.personio.reminders.infra.postgres

import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.domain.settings.RemindersSettingsRepository
import java.time.Instant
import java.util.UUID
import org.jooq.DSLContext
import org.jooq.generated.tables.Occurrences.OCCURRENCES
import org.jooq.generated.tables.Reminders.REMINDERS
import org.jooq.generated.tables.records.RemindersRecord
import org.springframework.stereotype.Repository

@Repository
class PostgresRemindersSettingsRepository(
    private val dslContext: DSLContext
) : RemindersSettingsRepository {

    override fun create(reminder: Reminder) {
        dslContext.insertInto(
            REMINDERS
        ).columns(
            REMINDERS.ID,
            REMINDERS.EMPLOYEE_ID,
            REMINDERS.TEXT,
            REMINDERS.TIMESTAMP,
            REMINDERS.IS_RECURRING,
            REMINDERS.RECURRENCE_INTERVAL,
            REMINDERS.RECURRENCE_FREQUENCY
        ).values(
            reminder.id,
            reminder.employeeId,
            reminder.text,
            Instant.parse(reminder.date),
            reminder.isRecurring,
            reminder.recurringInterval,
            reminder.recurringFrequency
        ).execute()
    }

    override fun findAll(employeeId: UUID): Collection<Reminder> {
        val records = dslContext.selectFrom(REMINDERS)
            .where(REMINDERS.EMPLOYEE_ID.eq(employeeId))
            .fetch()

        return records.map { it.toReminder() }
    }

    override fun findBy(id: UUID): Reminder? {
        val record = dslContext.selectFrom(REMINDERS)
            .where(REMINDERS.ID.eq(id))
            .fetchOne() ?: return null

        return record.toReminder()
    }

    override fun delete(id: UUID) {
        dslContext.deleteFrom(OCCURRENCES)
            .where(OCCURRENCES.REMINDER_ID.eq(id))
            .execute()

        dslContext.deleteFrom(REMINDERS)
            .where(REMINDERS.ID.eq(id))
            .execute()
    }

    private fun RemindersRecord.toReminder(): Reminder {
        return Reminder(
            id = this.id,
            employeeId = this.employeeId,
            text = this.text,
            date = this.timestamp.toString(),
            isRecurring = this.isRecurring,
            recurringInterval = this.recurrenceInterval,
            recurringFrequency = this.recurrenceFrequency
        )
    }
}
