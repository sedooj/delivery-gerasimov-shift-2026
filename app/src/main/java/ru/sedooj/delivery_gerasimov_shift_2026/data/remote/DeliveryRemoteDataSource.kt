package ru.sedooj.delivery_gerasimov_shift_2026.data.remote

import javax.inject.Inject
import kotlin.math.roundToInt
import org.json.JSONArray
import org.json.JSONObject
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.CalculateDeliveryDto
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.CalculateDeliveryResponse
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.PackageDto
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.PointDto
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint

class DeliveryRemoteDataSource @Inject constructor(
    private val apiClient: DeliveryApiClient,
    private val apiService: DeliveryApiService
) {
    fun getDeliveryPoints(): List<DeliveryPoint> {
        val response = apiClient.get(DELIVERY_POINTS_PATH)
        response.requireSuccess()
        return response.getJSONArray(KEY_POINTS).mapObjects { point ->
            DeliveryPoint(
                id = point.getString(KEY_ID),
                name = point.getString(KEY_NAME),
                latitude = point.getDouble(KEY_LATITUDE),
                longitude = point.getDouble(KEY_LONGITUDE)
            )
        }
    }

    fun getDeliveryPackageTypes(): List<DeliveryPackageType> {
        val response = apiClient.get(DELIVERY_PACKAGE_TYPES_PATH)
        response.requireSuccess()
        return response.getJSONArray(KEY_PACKAGES).mapObjects { packageType ->
            val length = packageType.getDouble(KEY_LENGTH)
            val width = packageType.getDouble(KEY_WIDTH)
            val height = packageType.getDouble(KEY_HEIGHT)
            val weight = packageType.getDouble(KEY_WEIGHT)
            DeliveryPackageType(
                id = packageType.getString(KEY_ID),
                name = packageType.getString(KEY_NAME),
                description = "${length.formatDimension()}x${width.formatDimension()}x${height.formatDimension()} см",
                lengthCm = length,
                widthCm = width,
                heightCm = height,
                weightKg = weight
            )
        }
    }

    fun calculateDelivery(request: DeliveryCalculationRequest): CalculateDeliveryResponse {
        return apiService.calculateDelivery(
            CalculateDeliveryDto(
                packageDto = PackageDto(
                    length = request.length,
                    width = request.width,
                    weight = request.weight,
                    height = request.height
                ),
                senderPoint = PointDto(
                    latitude = request.senderLatitude,
                    longitude = request.senderLongitude
                ),
                receiverPoint = PointDto(
                    latitude = request.receiverLatitude,
                    longitude = request.receiverLongitude
                )
            )
        )
    }

    private fun JSONObject.requireSuccess() {
        if (optBoolean(KEY_SUCCESS, false)) return
        error(optString(KEY_REASON).ifBlank { "Delivery API request failed." })
    }

    private fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T): List<T> {
        return List(length()) { index -> transform(getJSONObject(index)) }
    }

    private fun Double.formatDimension(): String {
        return if (this % WHOLE_NUMBER_DIVISOR == 0.0) {
            roundToInt().toString()
        } else {
            toString()
        }
    }

    private companion object {
        const val DELIVERY_POINTS_PATH = "/api/delivery/points"
        const val DELIVERY_PACKAGE_TYPES_PATH = "/api/delivery/package/types"
        const val KEY_SUCCESS = "success"
        const val KEY_REASON = "reason"
        const val KEY_POINTS = "points"
        const val KEY_PACKAGES = "packages"
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_LENGTH = "length"
        const val KEY_WIDTH = "width"
        const val KEY_HEIGHT = "height"
        const val KEY_WEIGHT = "weight"
        const val WHOLE_NUMBER_DIVISOR = 1.0
    }
}
