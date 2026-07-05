package ru.sedooj.delivery_gerasimov_shift_2026.data.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.memory.MemoryCacheFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.sedooj.delivery_gerasimov_shift_2026.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient? {
        val endpoint = BuildConfig.GRAPHQL_SERVER_URL
        if (endpoint.isBlank()) {
            return null
        }

        return ApolloClient.Builder()
            .serverUrl(endpoint)
            .normalizedCache(MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024))
            .build()
    }
}
