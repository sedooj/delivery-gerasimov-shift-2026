package ru.sedooj.delivery_gerasimov_shift_2026.data.remote

import javax.inject.Inject
import org.json.JSONObject
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.CalculateDeliveryDto
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.CalculateDeliveryResponse
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.DeliveryOptionDto
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.PointDto

class HttpDeliveryApiService @Inject constructor(
    private val apiClient: DeliveryApiClient
) : DeliveryApiService {

    override fun calculateDelivery(request: CalculateDeliveryDto): CalculateDeliveryResponse {
        val response = apiClient.post(
            path = DELIVERY_CALC_PATH,
            body = request.toJson()
        )

        return CalculateDeliveryResponse(
            success = response.optBoolean(KEY_SUCCESS, false),
            reason = response.optString(KEY_REASON).ifBlank { null },
            options = response.optJSONArray(KEY_OPTIONS)?.let { options ->
                List(options.length()) { index ->
                    options.getJSONObject(index).toDeliveryOptionDto()
                }
            }.orEmpty()
        )
    }

    private fun CalculateDeliveryDto.toJson(): JSONObject {
        return JSONObject()
            .put(
                KEY_PACKAGE,
                JSONObject()
                    .put(KEY_LENGTH, packageDto.length)
                    .put(KEY_WIDTH, packageDto.width)
                    .put(KEY_WEIGHT, packageDto.weight)
                    .put(KEY_HEIGHT, packageDto.height)
            )
            .put(KEY_SENDER_POINT, senderPoint.toJson())
            .put(KEY_RECEIVER_POINT, receiverPoint.toJson())
    }

    private fun PointDto.toJson(): JSONObject {
        return JSONObject()
            .put(KEY_LATITUDE, latitude)
            .put(KEY_LONGITUDE, longitude)
    }

    private fun JSONObject.toDeliveryOptionDto(): DeliveryOptionDto {
        return DeliveryOptionDto(
            id = getString(KEY_ID),
            price = getInt(KEY_PRICE),
            days = getInt(KEY_DAYS),
            name = getString(KEY_NAME),
            type = getString(KEY_TYPE)
        )
    }

    private companion object {
        const val DELIVERY_CALC_PATH = "/api/delivery/calc"
        const val KEY_SUCCESS = "success"
        const val KEY_REASON = "reason"
        const val KEY_PACKAGE = "package"
        const val KEY_SENDER_POINT = "senderPoint"
        const val KEY_RECEIVER_POINT = "receiverPoint"
        const val KEY_OPTIONS = "options"
        const val KEY_LENGTH = "length"
        const val KEY_WIDTH = "width"
        const val KEY_WEIGHT = "weight"
        const val KEY_HEIGHT = "height"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_ID = "id"
        const val KEY_PRICE = "price"
        const val KEY_DAYS = "days"
        const val KEY_NAME = "name"
        const val KEY_TYPE = "type"
    }
}
