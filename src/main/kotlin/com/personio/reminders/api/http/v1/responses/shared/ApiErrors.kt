package com.personio.reminders.api.http.v1.responses.shared

data class ApiErrors(val errors: List<ApiError>)

data class ApiError(val id: String, val status: String, val title: String, val detail: String?)
