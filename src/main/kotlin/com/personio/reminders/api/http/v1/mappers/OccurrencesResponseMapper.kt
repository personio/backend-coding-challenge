package com.personio.reminders.api.http.v1.mappers

import com.personio.reminders.api.http.v1.responses.OccurrencesResponse
import com.personio.reminders.domain.occurrences.Occurrence

class OccurrencesResponseMapper {
    companion object {
        fun toResponse(occurrence: Occurrence) =
            OccurrencesResponse(
                occurrence.id.toString(),
                occurrence.reminder.id.toString(),
                occurrence.reminder.text,
                occurrence.date
            )
    }
}
