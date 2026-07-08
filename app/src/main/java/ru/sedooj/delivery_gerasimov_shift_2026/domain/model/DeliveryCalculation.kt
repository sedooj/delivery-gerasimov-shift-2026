package ru.sedooj.delivery_gerasimov_shift_2026.domain.model

data class DeliveryCalculation(
    val amountRubles: Int,
    val currency: String,
    val etaDays: Int,
    val routeLabel: String,
    val deliveryTypeLabel: String
)
