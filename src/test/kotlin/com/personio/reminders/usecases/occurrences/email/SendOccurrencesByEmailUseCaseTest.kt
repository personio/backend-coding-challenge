package com.personio.reminders.usecases.occurrences.email

import com.personio.reminders.helpers.MotherObject
import com.personio.reminders.infra.mail.MailerService
import com.personio.reminders.infra.mail.Message
import com.personio.reminders.infra.postgres.occurrences.InMemoryOccurrencesRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

/**
 * Unit tests for the SendOccurrencesByEmailUseCase class.
 */
class SendOccurrencesByEmailUseCaseTest {

    @Test
    fun `it should send one email per each occurrence`() {
        val reminderOne = MotherObject.reminders().new()
        val occurrenceOne = MotherObject.occurrences().newFrom(reminderOne)
        val reminderTwo = MotherObject.reminders().new(text = "Remind me of creating tests")
        val occurrenceTwo = MotherObject.occurrences().newFrom(reminderTwo)
        val occurrences = InMemoryOccurrencesRepository(
            mutableListOf(reminderOne, reminderTwo),
            mutableListOf(occurrenceOne, occurrenceTwo),
            MotherObject.clock
        )
        val mailer: MailerService = mock()
        val subject = SendOccurrencesByEmailUseCase(
            occurrences,
            MotherObject.clock,
            mailer
        )

        subject.sendReminders()

        verify(mailer, times(2)).send(any())
    }

    @Test
    fun `it should not send mail for an occurrence without a reminder`() {
        val reminderOne = MotherObject.reminders().new()
        val occurrenceOne = MotherObject.occurrences().newFrom(reminderOne)
        val occurrenceTwo = MotherObject.occurrences().newFrom(
            MotherObject.reminders().new(text = "Remind me of creating tests")
        )
        val occurrences = InMemoryOccurrencesRepository(
            mutableListOf(reminderOne),
            mutableListOf(occurrenceOne, occurrenceTwo),
            MotherObject.clock
        )
        val mailer: MailerService = mock()
        val subject = SendOccurrencesByEmailUseCase(
            occurrences,
            MotherObject.clock,
            mailer
        )

        subject.sendReminders()

        verify(mailer, times(1)).send(
            Message(
                reminderOne.text,
                reminderOne.employeeId
            )
        )
    }

    @Test
    fun `it should not send mail for already notified occurrences`() {
        val reminderOne = MotherObject.reminders().new()
        val occurrenceOne = MotherObject.occurrences().newFrom(reminderOne)
        val reminderTwo = MotherObject.reminders().new(text = "Remind me of creating tests")
        val occurrenceTwo = MotherObject.occurrences().newFrom(reminderTwo, isNotificationSent = true)
        val occurrences = InMemoryOccurrencesRepository(
            mutableListOf(reminderOne),
            mutableListOf(occurrenceOne, occurrenceTwo),
            MotherObject.clock
        )
        val mailer: MailerService = mock()
        val subject = SendOccurrencesByEmailUseCase(
            occurrences,
            MotherObject.clock,
            mailer
        )

        subject.sendReminders()

        verify(mailer, times(1)).send(any())
    }

    @Test
    fun `it should mark occurrence as already notified`() {
        val reminderOne = MotherObject.reminders().new()
        val occurrenceOne = MotherObject.occurrences().newFrom(reminderOne)
        val occurrences = InMemoryOccurrencesRepository(
            mutableListOf(reminderOne),
            mutableListOf(occurrenceOne),
            MotherObject.clock
        )
        val mailer: MailerService = mock()
        val subject = SendOccurrencesByEmailUseCase(
            occurrences,
            MotherObject.clock,
            mailer
        )

        subject.sendReminders()

        assertTrue(occurrences.findBy(occurrenceOne.id)!!.isNotificationSent)
    }
}
