package ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase

import javax.inject.Inject
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryOption
import ru.sedooj.delivery_gerasimov_shift_2026.domain.repository.DeliveryRepository

class CalculateDeliveryUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(request: DeliveryCalculationRequest): List<DeliveryOption> {
        return repository.calculateDelivery(request)
    }
}
