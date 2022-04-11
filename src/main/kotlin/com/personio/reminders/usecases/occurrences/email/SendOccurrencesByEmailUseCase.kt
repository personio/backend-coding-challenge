package com.personio.reminders.usecases.occurrences.email

import com.personio.reminders.domain.occurrences.RemindersOccurrencesRepository
import com.personio.reminders.infra.mail.MailerService
import com.personio.reminders.infra.mail.Message
import java.time.Clock
import java.time.Instant
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SendOccurrencesByEmailUseCase(
    private val occurrences: RemindersOccurrencesRepository,
    private val clock: Clock,
    private val mailer: MailerService
) {
    @Scheduled(cron = "0 */5 * * * *")
    fun sendReminders() = occurrences.findAt(Instant.now(clock))
        .filter { !it.isNotificationSent }
        .forEach { occurrence ->
            val message = Message(occurrence.reminder.text, occurrence.reminder.employeeId)
            mailer.send(message)
            occurrences.markAsNotified(occurrence)
        }
}
