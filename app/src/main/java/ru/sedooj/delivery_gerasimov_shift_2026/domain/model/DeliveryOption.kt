package ru.sedooj.delivery_gerasimov_shift_2026.domain.model

data class DeliveryOption(
    val id: String,
    val price: Int,
    val days: Int,
    val name: String,
    val type: String
)
