package com.personio.reminders.infra.postgres.settings

import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.domain.settings.RemindersSettingsRepository

internal class InMemoryRemindersSettingsRepositoryTest : RemindersSettingsRepositoryContractTest {
    override fun subjectWithData(
        existingReminders: Collection<Reminder>
    ): RemindersSettingsRepository {
        return InMemoryRemindersSettingsRepository(
            existingReminders.toMutableList()
        )
    }
}
