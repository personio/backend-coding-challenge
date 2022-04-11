package com.personio.reminders.usecases.settings.find

import com.personio.reminders.domain.settings.RemindersSettingsRepository
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class FindRemindersUseCase(
    private val remindersSettingsRepository: RemindersSettingsRepository
) {
    fun findAll(employeeId: UUID) = remindersSettingsRepository.findAll(employeeId)
}
