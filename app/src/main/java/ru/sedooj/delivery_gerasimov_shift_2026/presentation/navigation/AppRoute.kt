package ru.sedooj.delivery_gerasimov_shift_2026.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Calculator : AppRoute

    @Serializable
    data object History : AppRoute

    @Serializable
    data object Profile : AppRoute

    @Serializable
    data object DeliveryMethod : AppRoute
}
