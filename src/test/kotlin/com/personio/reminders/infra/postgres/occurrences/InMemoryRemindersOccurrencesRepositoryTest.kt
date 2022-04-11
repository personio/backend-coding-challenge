package com.personio.reminders.infra.postgres.occurrences

import com.personio.reminders.domain.occurrences.ReminderOccurrence
import com.personio.reminders.domain.occurrences.RemindersOccurrencesRepository
import com.personio.reminders.domain.settings.Reminder
import java.time.Clock

internal class InMemoryRemindersOccurrencesRepositoryTest : RemindersOccurrencesRepositoryContractTest {
    override fun subjectWithData(
        existingReminders: Collection<Reminder>,
        existingOccurrences: Collection<ReminderOccurrence>,
        clock: Clock
    ): RemindersOccurrencesRepository {
        return InMemoryRemindersOccurrencesRepository(
            existingReminders.toMutableList(),
            existingOccurrences.toMutableList(),
            clock
        )
    }
}
