package ru.sedooj.delivery_gerasimov_shift_2026.data.remote

import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.CalculateDeliveryDto
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.CalculateDeliveryResponse

interface DeliveryApiService {
    fun calculateDelivery(request: CalculateDeliveryDto): CalculateDeliveryResponse
}
