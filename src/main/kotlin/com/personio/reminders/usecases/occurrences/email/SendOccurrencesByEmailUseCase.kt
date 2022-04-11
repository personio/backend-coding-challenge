package com.personio.reminders.usecases.occurrences.email

import com.personio.reminders.domain.occurrences.OccurrencesRepository
import com.personio.reminders.infra.mail.MailerService
import com.personio.reminders.infra.mail.Message
import java.time.Clock
import java.time.Instant
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * This class is a use case responsible for notifying employees by email about their reminder's occurrences.
 */
@Service
class SendOccurrencesByEmailUseCase(
    /**
     * The following properties are injected by Spring's Dependency Injection container,
     * during the instantiation of this controller.
     */
    private val occurrences: OccurrencesRepository,
    private val clock: Clock,
    private val mailer: MailerService
) {

    /**
     * This method is invoked every 5 minutes by the Spring Framework.
     * Scheduling is enabled by `@EnableScheduling` annotation in `Application.kt`
     */
    @Scheduled(cron = "0 */5 * * * *")
    fun sendReminders() = occurrences.findAt(Instant.now(clock))
        .filter { !it.isNotificationSent }
        .forEach { occurrence ->
            val message = Message(occurrence.reminder.text, occurrence.reminder.employeeId)
            mailer.send(message)
            occurrences.markAsNotified(occurrence)
        }
}
