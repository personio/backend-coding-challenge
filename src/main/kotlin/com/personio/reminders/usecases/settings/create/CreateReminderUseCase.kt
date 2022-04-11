package com.personio.reminders.usecases.settings.create

import com.personio.reminders.domain.occurrences.RemindersOccurrencesRepository
import com.personio.reminders.domain.settings.Reminder
import com.personio.reminders.domain.settings.RemindersSettingsRepository
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateReminderUseCase(
    private val remindersSettingsRepository: RemindersSettingsRepository,
    private val remindersOccurrencesRepository: RemindersOccurrencesRepository
) {
    @Transactional
    fun create(command: CreateReminderCommand): UUID {
        val reminder = Reminder.fromCommand(command)

        remindersSettingsRepository.create(reminder)
        createReminderFirstOccurrence(reminder)

        return reminder.id
    }

    private fun createReminderFirstOccurrence(reminder: Reminder) {
        remindersOccurrencesRepository.create(reminder.id, reminder.date)
    }
}
