package com.personio.reminders.api.http.v1

import com.personio.reminders.api.http.v1.mappers.RemindersResponseMapper
import com.personio.reminders.api.http.v1.requests.CreateReminderRequest
import com.personio.reminders.api.http.v1.responses.shared.Response
import com.personio.reminders.usecases.settings.create.CreateReminderCommand
import com.personio.reminders.usecases.settings.create.CreateReminderUseCase
import com.personio.reminders.usecases.settings.delete.DeleteReminderUseCase
import com.personio.reminders.usecases.settings.find.FindRemindersUseCase
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

@RestController
@RequestMapping("/reminders")
class RemindersSettingsEndpoint(
    private val createUseCase: CreateReminderUseCase,
    private val deleteUseCase: DeleteReminderUseCase,
    private val findUseCase: FindRemindersUseCase
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateReminderRequest) {
        createUseCase.create(CreateReminderCommand.fromRequest(request))
        return
    }

    @GetMapping
    fun findAll(@RequestParam(required = true) employeeId: UUID) = Response(
        findUseCase.findAll(employeeId)
            .map(RemindersResponseMapper::toResponse)
    )

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) = deleteUseCase.delete(id)
}
