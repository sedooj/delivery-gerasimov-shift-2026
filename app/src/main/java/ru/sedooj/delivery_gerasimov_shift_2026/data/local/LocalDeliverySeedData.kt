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
        DeliveryPoint(id = "tmk", name = "Томск", region = "Томская область")
    )

    val packageTypes = listOf(
        DeliveryPackageType(
            id = "envelope",
            name = "Конверт",
            description = "Документы и компактные вложения",
            maxWeightKg = 1,
            basePriceRubles = 290
        ),
        DeliveryPackageType(
            id = "small_box",
            name = "Короб S",
            description = "Лёгкие посылки до 5 кг",
            maxWeightKg = 5,
            basePriceRubles = 430
        ),
        DeliveryPackageType(
            id = "medium_box",
            name = "Короб M",
            description = "Стандартные отправления до 12 кг",
            maxWeightKg = 12,
            basePriceRubles = 610
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
