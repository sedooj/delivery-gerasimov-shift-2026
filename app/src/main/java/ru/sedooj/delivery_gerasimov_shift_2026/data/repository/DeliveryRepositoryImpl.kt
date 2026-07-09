package ru.sedooj.delivery_gerasimov_shift_2026.data.repository

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sedooj.delivery_gerasimov_shift_2026.data.di.IoDispatcher
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.dto.DeliveryOptionDto
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.DeliveryRemoteDataSource
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryOption
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint
import ru.sedooj.delivery_gerasimov_shift_2026.domain.repository.DeliveryRepository

class DeliveryRepositoryImpl @Inject constructor(
    private val remoteDataSource: DeliveryRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DeliveryRepository {

    override suspend fun getDeliveryPoints(): List<DeliveryPoint> = withContext(ioDispatcher) {
        remoteDataSource.getDeliveryPoints()
    }

    override suspend fun getDeliveryPackageTypes(): List<DeliveryPackageType> = withContext(ioDispatcher) {
        remoteDataSource.getDeliveryPackageTypes()
    }

    override suspend fun calculateDelivery(request: DeliveryCalculationRequest): List<DeliveryOption> =
        withContext(ioDispatcher) {
            val response = remoteDataSource.calculateDelivery(request)
            if (!response.success) {
                error(response.reason.orEmpty().ifBlank { "Не удалось рассчитать доставку" })
            }
            response.options.map { option -> option.toDomain() }
        }

    private fun DeliveryOptionDto.toDomain(): DeliveryOption {
        return DeliveryOption(
            id = id,
            price = price,
            days = days,
            name = name,
            type = type
        )
    }
}
