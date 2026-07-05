package ru.sedooj.delivery_gerasimov_shift_2026.domain.model

data class DeliveryPackageType(
    val id: String,
    val name: String,
    val description: String,
    val maxWeightKg: Int,
    val basePriceRubles: Int
)
