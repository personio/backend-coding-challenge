package com.personio.reminders.domain.occurrences.exceptions

/**
 * This is a domain exception, it is also framework-agnostic.
 *
 * This exception is thrown when a reminder's occurrence is not find in the reminder's occurrence repository.
 */
class OccurrenceNotFoundException(message: String = "occurrence.not-found") :
    RuntimeException(message)
