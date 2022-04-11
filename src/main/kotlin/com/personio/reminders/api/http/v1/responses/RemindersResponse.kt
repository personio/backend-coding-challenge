package com.personio.reminders.api.http.v1.responses

data class RemindersResponse(
    val id: String,
    val text: String,
    val date: String,
    val isRecurring: Boolean,
    val recurrenceInterval: Int?,
    val recurrenceFrequency: Int?
)
