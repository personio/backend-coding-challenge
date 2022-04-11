package com.personio.reminders.infra.mail

import java.util.UUID

data class Message(
    val text: String,
    val employeeId: UUID
)
