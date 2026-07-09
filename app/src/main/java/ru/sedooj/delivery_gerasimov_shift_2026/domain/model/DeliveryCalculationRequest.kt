package ru.sedooj.delivery_gerasimov_shift_2026.domain.model

data class DeliveryCalculationRequest(
    val length: Double,
    val width: Double,
    val weight: Double,
    val height: Double,
    val senderLatitude: Double,
    val senderLongitude: Double,
    val receiverLatitude: Double,
    val receiverLongitude: Double
)
