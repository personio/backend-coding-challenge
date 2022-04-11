package com.personio.reminders.api.http.v1.mappers

import com.personio.reminders.api.http.v1.responses.RemindersResponse
import com.personio.reminders.domain.settings.Reminder

class RemindersResponseMapper {
    companion object {
        fun toResponse(reminder: Reminder) =
            RemindersResponse(
                reminder.id.toString(),
                reminder.text,
                reminder.date,
                reminder.isRecurring,
                reminder.recurringInterval,
                reminder.recurringFrequency
            )
    }
}
