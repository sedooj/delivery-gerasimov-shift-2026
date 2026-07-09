package ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalculateDeliveryResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("reason") val reason: String? = null,
    @SerialName("options") val options: List<DeliveryOptionDto> = emptyList()
)

@Serializable
data class DeliveryOptionDto(
    @SerialName("id") val id: String,
    @SerialName("price") val price: Int,
    @SerialName("days") val days: Int,
    @SerialName("name") val name: String,
    @SerialName("type") val type: String
)
