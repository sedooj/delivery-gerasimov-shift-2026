package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

import androidx.annotation.StringRes
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

data class CalculatorUiState(
    val isLoading: Boolean = true,
    val deliveryPoints: List<DeliveryPoint> = emptyList(),
    val packageTypes: List<DeliveryPackageType> = emptyList(),
    val selectedSenderPointId: String = "",
    val selectedReceiverPointId: String = "",
    val cityPickerTarget: CityPickerTarget? = null,
    val isPackageSheetVisible: Boolean = false,
    val selectedPackageTypeId: String = "",
    val packageInputMode: PackageInputMode = PackageInputMode.Approximate,
    val trackingNumber: String = "",
    val isParcelSearching: Boolean = false,
    val lengthInput: String = "",
    val widthInput: String = "",
    val heightInput: String = "",
    val weightInput: String = "",
    @param:StringRes val errorMessageRes: Int? = null
) {
    val hasExactDimensions: Boolean
        get() = lengthInput.isNotBlank() &&
            widthInput.isNotBlank() &&
            heightInput.isNotBlank() &&
            weightInput.isNotBlank()

    val canSearchParcel: Boolean
        get() = trackingNumber.isNotBlank() && !isParcelSearching

    val canCalculate: Boolean
        get() = selectedSenderPointId.isNotBlank() &&
            selectedReceiverPointId.isNotBlank() &&
            when (packageInputMode) {
                PackageInputMode.Approximate -> selectedPackageTypeId.isNotBlank()
                PackageInputMode.Exact -> hasExactDimensions
            }
}
