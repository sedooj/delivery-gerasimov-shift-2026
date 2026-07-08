package ru.sedooj.delivery_gerasimov_shift_2026.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.sedooj.delivery_gerasimov_shift_2026.data.repository.DeliveryRepositoryImpl
import ru.sedooj.delivery_gerasimov_shift_2026.domain.repository.DeliveryRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDeliveryRepository(
        implementation: DeliveryRepositoryImpl
    ): DeliveryRepository
}
