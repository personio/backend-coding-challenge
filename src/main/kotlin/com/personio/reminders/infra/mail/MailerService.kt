package com.personio.reminders.infra.mail

import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class MailerService(
    private val logger: Logger
) {
    fun send(message: Message) {
        logger.info("Sending a fake message $message")
    }
}
