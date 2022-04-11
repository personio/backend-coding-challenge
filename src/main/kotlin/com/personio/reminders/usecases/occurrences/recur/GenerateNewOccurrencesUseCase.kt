package com.personio.reminders.usecases.occurrences.recur

import com.personio.reminders.domain.occurrences.RemindersOccurrencesRepository
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class GenerateNewOccurrencesUseCase(
    private val logger: Logger,
    private val remindersOccurrencesRepository: RemindersOccurrencesRepository
) {
    @Scheduled(cron = "*/10 * * * * *")
    fun generateNewOccurrences() {
        val instantsForNextReminderOccurrences = remindersOccurrencesRepository
            .getInstantForNextReminderOccurrences()
        instantsForNextReminderOccurrences.forEach {
            val reminderId = it.key
            val occurrenceInstant = it.value.toString()

            val occurrenceId = remindersOccurrencesRepository.create(reminderId, occurrenceInstant)
            logger.info("Scheduled occurrence $occurrenceId @ $occurrenceInstant for reminder $reminderId}")
        }
    }
}
