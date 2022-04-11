package com.personio.reminders.domain.reminders.exceptions

/**
 * This is a domain exception, it is also framework-agnostic.
 *
 * This exception is thrown when a reminder is not find in the reminders repository.
 */
class ReminderNotFoundException(message: String = "reminder.not-found") : RuntimeException(message)
