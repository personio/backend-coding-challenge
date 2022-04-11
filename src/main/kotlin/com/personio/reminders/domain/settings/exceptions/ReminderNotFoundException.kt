package com.personio.reminders.domain.settings.exceptions

class ReminderNotFoundException(message: String = "reminder.not-found") : RuntimeException(message)
