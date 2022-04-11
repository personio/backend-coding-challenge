package com.personio.reminders.api.http.v1.responses

data class OccurrencesResponse(
    val id: String,
    val reminderId: String,
    val text: String,
    val date: String
)
