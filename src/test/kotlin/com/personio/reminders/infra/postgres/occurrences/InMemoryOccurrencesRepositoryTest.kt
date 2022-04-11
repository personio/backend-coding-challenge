package com.personio.reminders.infra.postgres.occurrences

import com.personio.reminders.domain.occurrences.Occurrence
import com.personio.reminders.domain.occurrences.OccurrencesRepository
import com.personio.reminders.domain.reminders.Reminder
import java.time.Clock

/**
 * This class configures the in-memory repository to be used in the tests defined in the RemindersOccurrencesRepositoryContractTest class.
 */
internal class InMemoryOccurrencesRepositoryTest : OccurrencesRepositoryContractTest {
    override fun subjectWithData(
        existingReminders: Collection<Reminder>,
        existingOccurrences: Collection<Occurrence>,
        clock: Clock
    ): OccurrencesRepository {
        return InMemoryOccurrencesRepository(
            existingReminders.toMutableList(),
            existingOccurrences.toMutableList(),
            clock
        )
    }
}
