package com.personio.reminders.api.http.v1

import com.personio.reminders.api.http.v1.mappers.OccurrencesResponseMapper
import com.personio.reminders.api.http.v1.responses.shared.Response
import com.personio.reminders.usecases.occurrences.complete.AcknowledgeOccurrenceUseCase
import com.personio.reminders.usecases.occurrences.find.FindOccurrencesUseCase
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * This is a controller (interface adapter) used by the web interface.
 * Each controller is responsible for one functionality only,
 * following this way the Single Responsibility Principle.
 *
 * The REST controllers consume and produce JSON by default.
 */
@RestController
@RequestMapping("/occurrences")
class OccurrencesEndpoint(
    /**
     * The following properties are injected by Spring's Dependency Injection container,
     * during the instantiation of this controller
     */
    private val findUseCase: FindOccurrencesUseCase,
    private val acknowledgeUseCase: AcknowledgeOccurrenceUseCase
) {
    /**
     * This method is executed when the employees perform a `GET` request to the `/occurrences?employeeId={employeeId}` endpoint.
     * This endpoint returns a `200 OK` status code to the client along with a JSON containing all the employee reminder's occurrences.
     */
    @GetMapping
    fun findAll(@RequestParam(required = true) employeeId: UUID) =
        Response(
            findUseCase.findAll(employeeId = employeeId)
                .map(OccurrencesResponseMapper::toResponse)
        )

    /**
     * This method is executed when the employees perform a `PUT` request to the `/occurrences/{id}` endpoint.
     * This endpoint returns a `204 NO CONTENT` status code to the client.
     */
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun acknowledge(@PathVariable id: UUID) {
        acknowledgeUseCase.acknowledge(id)
    }
}
