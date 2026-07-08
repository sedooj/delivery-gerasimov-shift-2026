package ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase

import javax.inject.Inject
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint
import ru.sedooj.delivery_gerasimov_shift_2026.domain.repository.DeliveryRepository

class GetDeliveryPointsUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(): List<DeliveryPoint> {
        return repository.getDeliveryPoints()
    }
}
