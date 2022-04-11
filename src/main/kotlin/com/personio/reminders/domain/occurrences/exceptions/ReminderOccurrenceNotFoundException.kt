package com.personio.reminders.domain.occurrences.exceptions

class ReminderOccurrenceNotFoundException(message: String = "occurrence.not-found") :
    RuntimeException(message)
