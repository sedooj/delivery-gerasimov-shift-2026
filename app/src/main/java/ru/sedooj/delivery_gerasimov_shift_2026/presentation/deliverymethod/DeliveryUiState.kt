package ru.sedooj.delivery_gerasimov_shift_2026.presentation.deliverymethod

import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryOption

sealed interface DeliveryUiState {
    object Idle : DeliveryUiState
    object Loading : DeliveryUiState
    data class Success(val theOptions: List<DeliveryOption>) : DeliveryUiState
    data class Error(val message: String) : DeliveryUiState
}
