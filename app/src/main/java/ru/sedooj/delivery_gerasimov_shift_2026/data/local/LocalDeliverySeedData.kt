package ru.sedooj.delivery_gerasimov_shift_2026.data.local

import kotlin.math.abs
import kotlin.math.roundToInt
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculation
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint

object LocalDeliverySeedData {
    val points = listOf(
        DeliveryPoint(id = "msk", name = "Москва", region = "Москва"),
        DeliveryPoint(id = "spb", name = "Санкт-Петербург", region = "Ленинградская область"),
        DeliveryPoint(id = "nsk", name = "Новосибирск", region = "Новосибирская область"),
        DeliveryPoint(id = "tmk", name = "Томск", region = "Томская область"),
        DeliveryPoint(id = "nvk", name = "Новокузнецк", region = "Кемеровская область"),
        DeliveryPoint(id = "kry", name = "Красноярск", region = "Красноярский край"),
        DeliveryPoint(id = "ekb", name = "Екатеринбург", region = "Свердловская область"),
        DeliveryPoint(id = "khv", name = "Хабаровск", region = "Хабаровский край")
    )

    val packageTypes = listOf(
        DeliveryPackageType(
            id = "envelope",
            name = "Конверт",
            description = "42x36x5 см",
            maxWeightKg = 1,
            basePriceRubles = 290
        ),
        DeliveryPackageType(
            id = "box_xs",
            name = "Короб XS",
            description = "17x12x9 см",
            maxWeightKg = 2,
            basePriceRubles = 360
        ),
        DeliveryPackageType(
            id = "small_box",
            name = "Короб S",
            description = "23x9x10 см",
            maxWeightKg = 5,
            basePriceRubles = 430
        ),
        DeliveryPackageType(
            id = "medium_box",
            name = "Короб M",
            description = "31x22x12 см",
            maxWeightKg = 12,
            basePriceRubles = 610
        ),
        DeliveryPackageType(
            id = "large_box",
            name = "Короб L",
            description = "45x31x20 см",
            maxWeightKg = 20,
            basePriceRubles = 790
        )
    )

    fun calculate(request: DeliveryCalculationRequest): DeliveryCalculation {
        val sender = points.first { it.id == request.senderPointId }
        val receiver = points.first { it.id == request.receiverPointId }
        val packageType = packageTypes.first { it.id == request.packageTypeId }
        val distanceFactor = abs(points.indexOf(sender) - points.indexOf(receiver))
        val dimensions = listOfNotNull(request.lengthCm, request.widthCm, request.heightCm)
        val dimensionsSurcharge = if (dimensions.isEmpty()) 0 else dimensions.sum() * 2
        val weightSurcharge = ((request.weightKg ?: 0f) * 38f).roundToInt()
        val totalAmount = packageType.basePriceRubles +
            (distanceFactor * 190) +
            dimensionsSurcharge +
            weightSurcharge

        return DeliveryCalculation(
            amountRubles = totalAmount,
            currency = "RUB",
            etaDays = 1 + distanceFactor,
            routeLabel = "${sender.name} - ${receiver.name}",
            deliveryTypeLabel = if (distanceFactor <= 1) {
                "Экспресс-доставка"
            } else {
                "Стандартная доставка"
            }
        )
    }
}
