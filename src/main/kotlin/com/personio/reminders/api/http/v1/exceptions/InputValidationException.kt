package com.personio.reminders.api.http.v1.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(value = HttpStatus.NOT_FOUND)
class InputValidationException(message: String?) : RuntimeException(message) {
}
