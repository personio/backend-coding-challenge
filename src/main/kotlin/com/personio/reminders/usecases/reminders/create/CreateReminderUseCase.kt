package com.personio.reminders.usecases.reminders.create

import com.personio.reminders.domain.occurrences.OccurrencesRepository
import com.personio.reminders.domain.reminders.Reminder
import com.personio.reminders.domain.reminders.RemindersRepository
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * This class is a use case responsible for creating a new reminder for an employee.
 */
@Service
class CreateReminderUseCase(
    private val remindersRepository: RemindersRepository,
    private val occurrencesRepository: OccurrencesRepository
) {

    /**
     * This method is invoked by the controller,
     * the `@Transactional` annotation grants that all db operations inside this method
     * will be executed inside a transaction and that all operations will succeed or
     * all operations will be rolled back.
     */
    @Transactional
    fun create(command: CreateReminderCommand): UUID {
        val reminder = Reminder.fromCommand(command)

        remindersRepository.create(reminder)
        createReminderFirstOccurrence(reminder)

        return reminder.id
    }

    private fun createReminderFirstOccurrence(reminder: Reminder) {
        occurrencesRepository.create(reminder.id, reminder.date)
    }
}
