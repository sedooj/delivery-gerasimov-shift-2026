package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

sealed interface CalculatorEffect {
    data class ShowMessage(val message: String) : CalculatorEffect
}
