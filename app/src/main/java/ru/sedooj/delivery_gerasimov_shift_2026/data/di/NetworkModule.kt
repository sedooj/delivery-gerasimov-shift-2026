package ru.sedooj.delivery_gerasimov_shift_2026.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.sedooj.delivery_gerasimov_shift_2026.BuildConfig
import ru.sedooj.delivery_gerasimov_shift_2026.data.remote.DeliveryApiClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideDeliveryApiClient(): DeliveryApiClient {
        val endpoint = BuildConfig.DELIVERY_API_BASE_URL
        check(endpoint.isNotBlank()) {
            "Delivery API base URL is not configured. Set deliveryApiBaseUrl in local.properties."
        }

        return DeliveryApiClient(baseUrl = endpoint)
    }
}
