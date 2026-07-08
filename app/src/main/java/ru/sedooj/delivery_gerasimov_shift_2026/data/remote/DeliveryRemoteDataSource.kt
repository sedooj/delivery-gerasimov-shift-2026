package ru.sedooj.delivery_gerasimov_shift_2026.data.remote

import javax.inject.Inject
import kotlin.math.roundToInt
import org.json.JSONArray
import org.json.JSONObject
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculation
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint

class DeliveryRemoteDataSource @Inject constructor(
    private val apiClient: DeliveryApiClient
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

    fun calculateDelivery(request: DeliveryCalculationRequest): DeliveryCalculation {
        val points = getDeliveryPoints()
        val packageTypes = getDeliveryPackageTypes()
        val senderPoint = points.firstOrNull { it.id == request.senderPointId }
            ?: error("Sender delivery point not found: ${request.senderPointId}")
        val receiverPoint = points.firstOrNull { it.id == request.receiverPointId }
            ?: error("Receiver delivery point not found: ${request.receiverPointId}")
        val packagePayload = request.resolvePackagePayload(packageTypes)

        val response = apiClient.post(
            path = DELIVERY_CALC_PATH,
            body = JSONObject()
                .put(KEY_PACKAGE, packagePayload)
                .put(KEY_SENDER_POINT, senderPoint.toCalculatePointJson())
                .put(KEY_RECEIVER_POINT, receiverPoint.toCalculatePointJson())
        )
        response.requireSuccess()

        val option = response.getJSONArray(KEY_OPTIONS)
            .takeUnless { it.length() == 0 }
            ?.getJSONObject(FIRST_INDEX)
            ?: error("Delivery API returned no delivery options.")

        return DeliveryCalculation(
            amountRubles = (option.getDouble(KEY_PRICE) / KOPECKS_IN_RUBLE).roundToInt(),
            currency = CURRENCY_RUB,
            etaDays = option.getDouble(KEY_DAYS).roundToInt(),
            routeLabel = "${senderPoint.name} - ${receiverPoint.name}",
            deliveryTypeLabel = option.getString(KEY_NAME)
        )
    }

    private fun DeliveryCalculationRequest.resolvePackagePayload(
        packageTypes: List<DeliveryPackageType>
    ): JSONObject {
        val selectedPackage = packageTypes.firstOrNull { it.id == packageTypeId }
        return JSONObject()
            .put(KEY_LENGTH, lengthCm ?: selectedPackage?.lengthCm ?: error("Package length is required."))
            .put(KEY_WIDTH, widthCm ?: selectedPackage?.widthCm ?: error("Package width is required."))
            .put(KEY_HEIGHT, heightCm ?: selectedPackage?.heightCm ?: error("Package height is required."))
            .put(KEY_WEIGHT, weightKg ?: selectedPackage?.weightKg ?: error("Package weight is required."))
    }

    private fun DeliveryPoint.toCalculatePointJson(): JSONObject {
        return JSONObject()
            .put(KEY_LATITUDE, latitude)
            .put(KEY_LONGITUDE, longitude)
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
        const val DELIVERY_CALC_PATH = "/api/delivery/calc"
        const val KEY_SUCCESS = "success"
        const val KEY_REASON = "reason"
        const val KEY_POINTS = "points"
        const val KEY_PACKAGES = "packages"
        const val KEY_PACKAGE = "package"
        const val KEY_SENDER_POINT = "senderPoint"
        const val KEY_RECEIVER_POINT = "receiverPoint"
        const val KEY_OPTIONS = "options"
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_LENGTH = "length"
        const val KEY_WIDTH = "width"
        const val KEY_HEIGHT = "height"
        const val KEY_WEIGHT = "weight"
        const val KEY_PRICE = "price"
        const val KEY_DAYS = "days"
        const val FIRST_INDEX = 0
        const val KOPECKS_IN_RUBLE = 100.0
        const val WHOLE_NUMBER_DIVISOR = 1.0
        const val CURRENCY_RUB = "RUB"
    }
}
