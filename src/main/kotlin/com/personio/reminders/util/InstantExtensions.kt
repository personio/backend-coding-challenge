package com.personio.reminders.util

import java.time.Clock
import java.time.Instant
import java.time.Period
import java.time.temporal.ChronoUnit

/**
 * This extension adds support to [ChronoUnit] values that are not supported by the default addTo method.
 * You can learn more about Kotlin's extension methods at https://kotlinlang.org/docs/extensions.html
 **/
fun ChronoUnit.addToInstant(instant: Instant, amount: Long, clock: Clock): Instant {
    return when(this) {
        ChronoUnit.DAYS -> Period.ofDays(amount.toInt()).addTo(instant) as Instant
        ChronoUnit.WEEKS -> Period.ofWeeks(amount.toInt()).addTo(instant) as Instant
        ChronoUnit.MONTHS -> instant.atZone(clock.zone).plus(Period.ofMonths(amount.toInt())).toInstant() as Instant
        ChronoUnit.YEARS -> instant.atZone(clock.zone).plus(Period.ofYears(amount.toInt())).toInstant() as Instant
        else -> throw UnsupportedOperationException("Invalid operation for $this")
    }
}
