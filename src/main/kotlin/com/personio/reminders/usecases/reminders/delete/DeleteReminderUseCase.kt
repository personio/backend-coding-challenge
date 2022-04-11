package com.personio.reminders.usecases.reminders.delete

import com.personio.reminders.domain.reminders.RemindersRepository
import com.personio.reminders.domain.reminders.exceptions.ReminderNotFoundException
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * This class is a use case responsible for deleting a specific reminder.
 */
@Service
class DeleteReminderUseCase(
    private val remindersSettingsRepository: RemindersRepository
) {

    /**
     * This method is invoked by the controller,
     * the `@Transactional` annotation grants that all db operations inside this method
     * will be executed inside a transaction and that all operations will succeed or
     * all operations will be rolled back.
     */
    @Transactional
    fun delete(reminderId: UUID) {
        remindersSettingsRepository.findBy(reminderId) ?: throw ReminderNotFoundException()
        remindersSettingsRepository.delete(reminderId)
    }
}
