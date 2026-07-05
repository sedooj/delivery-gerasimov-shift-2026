package ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase

import javax.inject.Inject
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.repository.DeliveryRepository

class GetDeliveryPackageTypesUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(): List<DeliveryPackageType> {
        return repository.getDeliveryPackageTypes()
    }
}
