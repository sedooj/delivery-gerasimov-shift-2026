package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

sealed interface CalculatorIntent {
    data class SenderPointChanged(val pointId: String) : CalculatorIntent
    data class ReceiverPointChanged(val pointId: String) : CalculatorIntent
    data class PackageTypeChanged(val packageTypeId: String) : CalculatorIntent
    data class ExactDimensionsToggled(val enabled: Boolean) : CalculatorIntent
    data class LengthChanged(val value: String) : CalculatorIntent
    data class WidthChanged(val value: String) : CalculatorIntent
    data class HeightChanged(val value: String) : CalculatorIntent
    data class WeightChanged(val value: String) : CalculatorIntent
    data object CalculateClicked : CalculatorIntent
    data object ErrorDismissed : CalculatorIntent
}
