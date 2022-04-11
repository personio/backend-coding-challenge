package com.personio.reminders.api.http.v1

import com.personio.reminders.api.http.v1.mappers.RemindersResponseMapper
import com.personio.reminders.api.http.v1.requests.CreateReminderRequest
import com.personio.reminders.api.http.v1.responses.shared.Response
import com.personio.reminders.usecases.reminders.create.CreateReminderCommand
import com.personio.reminders.usecases.reminders.create.CreateReminderUseCase
import com.personio.reminders.usecases.reminders.delete.DeleteReminderUseCase
import com.personio.reminders.usecases.reminders.find.FindRemindersUseCase
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
@RequestMapping("/reminders")
class RemindersEndpoint(
    /**
     * The following properties are injected by Spring's Dependency Injection container,
     * during the instantiation of this controller
     */
    private val createUseCase: CreateReminderUseCase,
    private val deleteUseCase: DeleteReminderUseCase,
    private val findUseCase: FindRemindersUseCase
) {

    /**
     * This method is executed when the employees perform a `POST` request to the `/reminders` endpoint.
     * The request's JSON body is converted into the `request` param.
     * This endpoint returns a `201 CREATED` status code to the client.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateReminderRequest) {
        createUseCase.create(CreateReminderCommand.fromRequest(request))
        return
    }

    /**
     * This method is invoked when the employees perform a `GET` request to the
     * `/reminders?employeeId={employeeId}` endpoint.
     * This endpoint returns a `200 OK` status code to the client along with a JSON containing all the employee's reminders.
     */
    @GetMapping
    fun findAll(@RequestParam(required = true) employeeId: UUID) = Response(
        findUseCase.findAll(employeeId)
            .map(RemindersResponseMapper::toResponse)
    )

    /**
     * This method is invoked when the employees perform a `DELETE` request to the `/reminders/{id}` endpoint.
     * This endpoint returns a `204 NO CONTENT` status code to the client.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) = deleteUseCase.delete(id)
}
