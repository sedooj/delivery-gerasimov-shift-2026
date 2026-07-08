package ru.sedooj.delivery_gerasimov_shift_2026.domain.model

data class DeliveryPackageType(
    val id: String,
    val name: String,
    val description: String,
    val lengthCm: Double,
    val widthCm: Double,
    val heightCm: Double,
    val weightKg: Double
)
