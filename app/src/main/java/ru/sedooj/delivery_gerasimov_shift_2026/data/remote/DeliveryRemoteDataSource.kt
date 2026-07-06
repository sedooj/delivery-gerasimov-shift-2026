package ru.sedooj.delivery_gerasimov_shift_2026.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import javax.inject.Inject
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculation
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint
import ru.sedooj.delivery_gerasimov_shift_2026.graphql.CalculateDeliveryMutation
import ru.sedooj.delivery_gerasimov_shift_2026.graphql.GetDeliveryPackageTypesQuery
import ru.sedooj.delivery_gerasimov_shift_2026.graphql.GetDeliveryPointsQuery
import ru.sedooj.delivery_gerasimov_shift_2026.graphql.type.CalculateDeliveryInput

class DeliveryRemoteDataSource @Inject constructor(
    private val apolloClient: ApolloClient?
) {
    suspend fun getDeliveryPoints(): List<DeliveryPoint>? {
        val client = apolloClient ?: return null
        val response = client.query(GetDeliveryPointsQuery()).execute()
        val data = response.data ?: return null
        return data.deliveryPoints.map { point ->
            DeliveryPoint(
                id = point.id,
                name = point.name,
                region = point.region
            )
        }
    }

    suspend fun getDeliveryPackageTypes(): List<DeliveryPackageType>? {
        val client = apolloClient ?: return null
        val response = client.query(GetDeliveryPackageTypesQuery()).execute()
        val data = response.data ?: return null
        return data.deliveryPackageTypes.map { packageType ->
            DeliveryPackageType(
                id = packageType.id,
                name = packageType.name,
                description = packageType.description,
                maxWeightKg = packageType.maxWeightKg,
                basePriceRubles = packageType.basePriceRubles
            )
        }
    }

    suspend fun calculateDelivery(request: DeliveryCalculationRequest): DeliveryCalculation? {
        val client = apolloClient ?: return null
        val response = client.mutation(
            CalculateDeliveryMutation(
                input = CalculateDeliveryInput(
                    senderPointId = request.senderPointId,
                    receiverPointId = request.receiverPointId,
                    packageTypeId = request.packageTypeId,
                    lengthCm = request.lengthCm.toOptional(),
                    widthCm = request.widthCm.toOptional(),
                    heightCm = request.heightCm.toOptional(),
                    weightKg = request.weightKg?.toDouble().toOptional()
                )
            )
        ).execute()
        val data = response.data?.calculateDelivery ?: return null
        return DeliveryCalculation(
            amountRubles = data.amountRubles,
            currency = data.currency,
            etaDays = data.etaDays,
            routeLabel = data.routeLabel,
            deliveryTypeLabel = data.deliveryTypeLabel
        )
    }
}

private fun Int?.toOptional(): Optional<Int?> {
    return if (this == null) Optional.Absent else Optional.Present(this)
}

private fun Double?.toOptional(): Optional<Double?> {
    return if (this == null) Optional.Absent else Optional.Present(this)
}
