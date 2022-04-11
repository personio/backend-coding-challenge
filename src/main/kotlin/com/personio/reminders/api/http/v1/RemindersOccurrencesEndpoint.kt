package com.personio.reminders.api.http.v1

import com.personio.reminders.api.http.v1.mappers.ReminderOccurrencesResponseMapper
import com.personio.reminders.api.http.v1.responses.shared.Response
import com.personio.reminders.usecases.occurrences.complete.AcknowledgeOccurrenceUseCase
import com.personio.reminders.usecases.occurrences.find.FindRemindersOccurrencesUseCase
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/occurrences")
class RemindersOccurrencesEndpoint(
    private val findUseCase: FindRemindersOccurrencesUseCase,
    private val acknowledgeUseCase: AcknowledgeOccurrenceUseCase
) {
    @GetMapping
    fun findAll(@RequestParam(required = true) employeeId: UUID) =
        Response(
            findUseCase.findAll(employeeId = employeeId)
                .map(ReminderOccurrencesResponseMapper::toResponse)
        )

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun acknowledge(@PathVariable id: UUID) {
        acknowledgeUseCase.acknowledge(id)
    }
}
