package ru.sedooj.delivery_gerasimov_shift_2026.domain.model

data class DeliveryCalculationRequest(
    val senderPointId: String,
    val receiverPointId: String,
    val packageTypeId: String,
    val lengthCm: Int?,
    val widthCm: Int?,
    val heightCm: Int?,
    val weightKg: Float?
)
