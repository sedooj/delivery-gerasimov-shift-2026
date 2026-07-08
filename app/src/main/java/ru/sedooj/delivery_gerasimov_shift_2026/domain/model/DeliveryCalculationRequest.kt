package ru.sedooj.delivery_gerasimov_shift_2026.domain.model

data class DeliveryCalculationRequest(
    val senderPointId: String,
    val receiverPointId: String,
    val packageTypeId: String,
    val lengthCm: Double?,
    val widthCm: Double?,
    val heightCm: Double?,
    val weightKg: Double?
)
