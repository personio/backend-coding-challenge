package com.personio.reminders.infra.postgres.settings

import com.personio.reminders.domain.reminders.Reminder
import com.personio.reminders.domain.reminders.RemindersRepository

/**
 * This class configures the in-memory repository to be used in the tests defined in the RemindersSettingsRepositoryContractTest class.
 */
internal class InMemoryRemindersRepositoryTest : RemindersRepositoryContractTest {
    override fun subjectWithData(
        existingReminders: Collection<Reminder>
    ): RemindersRepository {
        return InMemoryRemindersRepository(
            existingReminders.toMutableList()
        )
    }
}
