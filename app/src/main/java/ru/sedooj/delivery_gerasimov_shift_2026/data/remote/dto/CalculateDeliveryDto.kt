package ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalculateDeliveryDto(
    @SerialName("package") val packageDto: PackageDto,
    @SerialName("senderPoint") val senderPoint: PointDto,
    @SerialName("receiverPoint") val receiverPoint: PointDto
)

@Serializable
data class PackageDto(
    @SerialName("length") val length: Double,
    @SerialName("width") val width: Double,
    @SerialName("weight") val weight: Double,
    @SerialName("height") val height: Double
)

@Serializable
data class PointDto(
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double
)
