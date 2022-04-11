package com.personio.reminders.domain.occurrences

import com.personio.reminders.domain.settings.Reminder
import java.util.UUID

data class ReminderOccurrence(
    val id: UUID,
    val reminder: Reminder,
    val isNotificationSent: Boolean = false,
    val date: String,
    val isAcknowledged: Boolean
)
