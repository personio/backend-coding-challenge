package com.personio.reminders.usecases.occurrences.find

import com.personio.reminders.domain.occurrences.RemindersOccurrencesRepository
import java.time.Clock
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class FindRemindersOccurrencesUseCase(
    private val remindersOccurrencesRepository: RemindersOccurrencesRepository,
    private val clock: Clock
) {
    fun findAll(employeeId: UUID) = remindersOccurrencesRepository.findAt(Instant.now(clock), employeeId)
}
