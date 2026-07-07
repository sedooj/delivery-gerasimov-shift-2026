package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint

enum class CityPickerTarget {
    Sender,
    Receiver
}

enum class PackageInputMode {
    Approximate,
    Exact
}

data class CalculatorQuoteUi(
    val amountText: String,
    val etaText: String,
    val routeLabel: String,
    val deliveryTypeLabel: String
)

data class CalculatorUiState(
    val isLoading: Boolean = true,
    val isCalculating: Boolean = false,
    val deliveryPoints: List<DeliveryPoint> = emptyList(),
    val packageTypes: List<DeliveryPackageType> = emptyList(),
    val selectedSenderPointId: String = "",
    val selectedReceiverPointId: String = "",
    val cityPickerTarget: CityPickerTarget? = null,
    val isPackageSheetVisible: Boolean = false,
    val selectedPackageTypeId: String = "",
    val packageInputMode: PackageInputMode = PackageInputMode.Approximate,
    val lengthInput: String = "",
    val widthInput: String = "",
    val heightInput: String = "",
    val weightInput: String = "",
    val quote: CalculatorQuoteUi? = null,
    val errorMessage: String? = null
) {
    val hasExactDimensions: Boolean
        get() = lengthInput.isNotBlank() &&
            widthInput.isNotBlank() &&
            heightInput.isNotBlank() &&
            weightInput.isNotBlank()

    val canCalculate: Boolean
        get() = selectedSenderPointId.isNotBlank() &&
            selectedReceiverPointId.isNotBlank() &&
            when (packageInputMode) {
                PackageInputMode.Approximate -> selectedPackageTypeId.isNotBlank()
                PackageInputMode.Exact -> hasExactDimensions
            }
}
