package com.personio.reminders.usecases.occurrences.recur

import com.personio.reminders.domain.occurrences.OccurrencesRepository
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * This class is a use case responsible generating the next occurrences for the reminders.
 */
@Service
class GenerateNewOccurrencesUseCase(
    private val logger: Logger,
    private val occurrencesRepository: OccurrencesRepository
) {
    /**
    * This method is executed every 10 seconds, using Spring Boot's scheduled tasks.
    * Scheduling is enabled by `@EnableScheduling` annotation in `Application.kt`
    **/
    @Scheduled(cron = "*/10 * * * * *")
    fun generateNewOccurrences() {
        val instantsForNextReminderOccurrences = occurrencesRepository
            .getInstantForNextReminderOccurrences()
        instantsForNextReminderOccurrences.forEach {
            val reminderId = it.key
            val occurrenceInstant = it.value.toString()

            val occurrenceId = occurrencesRepository.create(reminderId, occurrenceInstant)
            logger.info("Scheduled occurrence $occurrenceId @ $occurrenceInstant for reminder $reminderId}")
        }
    }
}
