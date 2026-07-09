package ru.sedooj.delivery_gerasimov_shift_2026.domain.repository

import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryOption
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint

interface DeliveryRepository {
    suspend fun getDeliveryPoints(): List<DeliveryPoint>
    suspend fun getDeliveryPackageTypes(): List<DeliveryPackageType>
    suspend fun calculateDelivery(request: DeliveryCalculationRequest): List<DeliveryOption>
}
