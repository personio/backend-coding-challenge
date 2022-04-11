package com.personio.reminders.infra.mail

import org.slf4j.Logger
import org.springframework.stereotype.Service

/**
* This is a dummy mailer service that would in real life contain the logic of sending real emails.
* The actual implementation of sending emails is outside the scope of this coding challenge.
**/
@Service
class MailerService(
    private val logger: Logger
) {
    fun send(message: Message) {
        logger.info("Sending a fake message $message")
    }
}
