package com.personio.reminders.usecases.occurrences.find

import com.personio.reminders.domain.occurrences.OccurrencesRepository
import java.time.Clock
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service

/**
 * This class is a use case responsible for retrieving all reminder's occurrences of a specific employee.
 */
@Service
class FindOccurrencesUseCase(
    /**
     * The following properties are injected by Spring's Dependency Injection container,
     * during the instantiation of this controller.
     */
    private val occurrencesRepository: OccurrencesRepository,
    private val clock: Clock
) {
    fun findAll(employeeId: UUID) = occurrencesRepository.findAt(Instant.now(clock), employeeId)
}
