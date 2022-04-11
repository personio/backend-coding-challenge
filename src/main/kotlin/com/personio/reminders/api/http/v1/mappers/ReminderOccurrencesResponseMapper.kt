package com.personio.reminders.api.http.v1.mappers

import com.personio.reminders.api.http.v1.responses.ReminderOccurrencesResponse
import com.personio.reminders.domain.occurrences.ReminderOccurrence

class ReminderOccurrencesResponseMapper {
    companion object {
        fun toResponse(occurrence: ReminderOccurrence) =
            ReminderOccurrencesResponse(
                occurrence.id.toString(),
                occurrence.reminder.id.toString(),
                occurrence.reminder.text,
                occurrence.date
            )
    }
}
