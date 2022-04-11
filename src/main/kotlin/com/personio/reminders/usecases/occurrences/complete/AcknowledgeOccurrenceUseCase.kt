package com.personio.reminders.usecases.occurrences.complete

import com.personio.reminders.domain.occurrences.RemindersOccurrencesRepository
import com.personio.reminders.domain.occurrences.exceptions.ReminderOccurrenceNotFoundException
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class AcknowledgeOccurrenceUseCase(
    private val remindersOccurrencesRepository: RemindersOccurrencesRepository
) {
    fun acknowledge(id: UUID) {
        val occurrence = remindersOccurrencesRepository.findBy(id)
            ?: throw ReminderOccurrenceNotFoundException()
        remindersOccurrencesRepository.acknowledge(occurrence)
    }
}
