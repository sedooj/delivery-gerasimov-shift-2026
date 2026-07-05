package ru.sedooj.delivery_gerasimov_shift_2026.data.repository

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sedooj.delivery_gerasimov_shift_2026.data.di.IoDispatcher
import ru.sedooj.delivery_gerasimov_shift_2026.data.local.LocalDeliverySeedData
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.DeliveryRemoteDataSource
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculation
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint
import ru.sedooj.delivery_gerasimov_shift_2026.domain.repository.DeliveryRepository

class DeliveryRepositoryImpl @Inject constructor(
    private val remoteDataSource: DeliveryRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DeliveryRepository {

    override suspend fun getDeliveryPoints(): List<DeliveryPoint> = withContext(ioDispatcher) {
        remoteDataSource.getDeliveryPoints() ?: LocalDeliverySeedData.points
    }

    override suspend fun getDeliveryPackageTypes(): List<DeliveryPackageType> = withContext(ioDispatcher) {
        remoteDataSource.getDeliveryPackageTypes() ?: LocalDeliverySeedData.packageTypes
    }

    override suspend fun calculateDelivery(request: DeliveryCalculationRequest): DeliveryCalculation =
        withContext(ioDispatcher) {
            remoteDataSource.calculateDelivery(request) ?: LocalDeliverySeedData.calculate(request)
        }
}
