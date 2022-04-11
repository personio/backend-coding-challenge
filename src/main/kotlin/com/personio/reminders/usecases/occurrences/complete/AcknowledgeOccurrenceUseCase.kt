package com.personio.reminders.usecases.occurrences.complete

import com.personio.reminders.domain.occurrences.OccurrencesRepository
import com.personio.reminders.domain.occurrences.exceptions.OccurrenceNotFoundException
import java.util.UUID
import org.springframework.stereotype.Service

/**
 * This class is a use case responsible for acknowledge reminder's occurrences.
 */
@Service
class AcknowledgeOccurrenceUseCase(
    /**
     * The following properties are injected by Spring's Dependency Injection container,
     * during the instantiation of this controller.
     */
    private val occurrencesRepository: OccurrencesRepository
) {

    /**
     * This method is invoked by the controller and is responsible for the use case implementation.
     */
    fun acknowledge(id: UUID) {
        val occurrence = occurrencesRepository.findBy(id)
            ?: throw OccurrenceNotFoundException()
        occurrencesRepository.acknowledge(occurrence)
    }
}
