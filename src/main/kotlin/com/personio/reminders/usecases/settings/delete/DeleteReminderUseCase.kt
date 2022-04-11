package com.personio.reminders.usecases.settings.delete

import com.personio.reminders.domain.settings.RemindersSettingsRepository
import com.personio.reminders.domain.settings.exceptions.ReminderNotFoundException
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteReminderUseCase(
    private val remindersSettingsRepository: RemindersSettingsRepository
) {
    @Transactional
    fun delete(reminderId: UUID) {
        remindersSettingsRepository.findBy(reminderId) ?: throw ReminderNotFoundException()
        remindersSettingsRepository.delete(reminderId)
    }
}
