package com.personio.reminders.usecases.reminders.find

import com.personio.reminders.domain.reminders.RemindersRepository
import java.util.UUID
import org.springframework.stereotype.Service

/**
 * This class is a use case responsible for deleting reminder.
 */
@Service
class DeleteRemindersUseCase(
    private val remindersSettingsRepository: RemindersRepository
) {

    /**
     * This method is invoked by the controller.
     */
    fun deleteReminder(reminderId: UUID) = remindersSettingsRepository.deleteBy(reminderId)
}
