package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.sedooj.delivery_gerasimov_shift_2026.R
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.CalculateDeliveryUseCase
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.GetDeliveryPackageTypesUseCase
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.GetDeliveryPointsUseCase

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getDeliveryPointsUseCase: GetDeliveryPointsUseCase,
    private val getDeliveryPackageTypesUseCase: GetDeliveryPackageTypesUseCase,
    private val calculateDeliveryUseCase: CalculateDeliveryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<CalculatorEffect>()
    val effects: SharedFlow<CalculatorEffect> = _effects.asSharedFlow()

    init {
        loadInitialData()
    }

    fun onIntent(intent: CalculatorIntent) {
        when (intent) {
            is CalculatorIntent.CityPickerOpened -> {
                _uiState.update { it.copy(cityPickerTarget = intent.target) }
            }

            CalculatorIntent.CityPickerDismissed -> {
                _uiState.update { it.copy(cityPickerTarget = null) }
            }

            is CalculatorIntent.SenderPointChanged -> {
                _uiState.update {
                    it.copy(
                        selectedSenderPointId = intent.pointId,
                        cityPickerTarget = null,
                        quote = null
                    )
                }
            }

            is CalculatorIntent.ReceiverPointChanged -> {
                _uiState.update {
                    it.copy(
                        selectedReceiverPointId = intent.pointId,
                        cityPickerTarget = null,
                        quote = null
                    )
                }
            }

            CalculatorIntent.PackageSheetOpened -> {
                _uiState.update { it.copy(isPackageSheetVisible = true) }
            }

            CalculatorIntent.PackageSheetDismissed -> {
                _uiState.update { it.copy(isPackageSheetVisible = false) }
            }

            is CalculatorIntent.PackageInputModeChanged -> {
                _uiState.update {
                    it.copy(
                        packageInputMode = intent.mode,
                        quote = null
                    )
                }
            }

            is CalculatorIntent.PackageTypeChanged -> {
                _uiState.update {
                    it.copy(
                        selectedPackageTypeId = intent.packageTypeId,
                        packageInputMode = PackageInputMode.Approximate,
                        isPackageSheetVisible = false,
                        quote = null
                    )
                }
            }

            is CalculatorIntent.LengthChanged -> updateDimension { copy(lengthInput = intent.value, quote = null) }
            is CalculatorIntent.WidthChanged -> updateDimension { copy(widthInput = intent.value, quote = null) }
            is CalculatorIntent.HeightChanged -> updateDimension { copy(heightInput = intent.value, quote = null) }
            is CalculatorIntent.WeightChanged -> updateDimension { copy(weightInput = intent.value, quote = null) }
            is CalculatorIntent.TrackingNumberChanged -> {
                _uiState.update { it.copy(trackingNumber = intent.value) }
            }

            CalculatorIntent.ParcelSearchClicked -> searchParcel()
            CalculatorIntent.CalculateClicked -> calculate()
            CalculatorIntent.ErrorDismissed -> _uiState.update { it.copy(errorMessageRes = null) }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            runCatching {
                val pointsDeferred = async { getDeliveryPointsUseCase() }
                val packageTypesDeferred = async { getDeliveryPackageTypesUseCase() }
                val points = pointsDeferred.await()
                val packageTypes = packageTypesDeferred.await()
                val defaultSender = points.firstOrNull()?.id.orEmpty()
                val defaultReceiver = points.getOrNull(1)?.id ?: defaultSender

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        deliveryPoints = points,
                        packageTypes = packageTypes,
                        selectedSenderPointId = defaultSender,
                        selectedReceiverPointId = defaultReceiver
                    )
                }
            }.onFailure { throwable ->
                val messageRes = R.string.calculator_error_load
                _uiState.update { it.copy(isLoading = false, errorMessageRes = messageRes) }
                _effects.emit(CalculatorEffect.ShowMessage(context.getString(messageRes)))
            }
        }
    }

    private fun calculate() {
        val currentState = _uiState.value
        if (!currentState.canCalculate) {
            viewModelScope.launch {
                val messageRes = R.string.calculator_error_fill_required
                _uiState.update { it.copy(errorMessageRes = messageRes) }
                _effects.emit(CalculatorEffect.ShowMessage(context.getString(messageRes)))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true, errorMessageRes = null) }

            runCatching {
                val selectedPackageTypeId = resolvePackageTypeId(currentState)
                calculateDeliveryUseCase(
                    request = DeliveryCalculationRequest(
                        senderPointId = currentState.selectedSenderPointId,
                        receiverPointId = currentState.selectedReceiverPointId,
                        packageTypeId = selectedPackageTypeId,
                        lengthCm = currentState.lengthInput.toIntOrNull(),
                        widthCm = currentState.widthInput.toIntOrNull(),
                        heightCm = currentState.heightInput.toIntOrNull(),
                        weightKg = currentState.weightInput.toFloatOrNull()
                    )
                )
            }.onSuccess { calculation ->
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        quote = CalculatorQuoteUi(
                            amountRubles = calculation.amountRubles,
                            etaDays = calculation.etaDays,
                            routeLabel = calculation.routeLabel,
                            deliveryTypeLabel = calculation.deliveryTypeLabel
                        )
                    )
                }
            }.onFailure { throwable ->
                val messageRes = R.string.calculator_error_calculation
                _uiState.update { it.copy(isCalculating = false, errorMessageRes = messageRes) }
                _effects.emit(CalculatorEffect.ShowMessage(context.getString(messageRes)))
            }
        }
    }

    private fun searchParcel() {
        val currentState = _uiState.value
        if (!currentState.canSearchParcel) return

        viewModelScope.launch {
            _uiState.update { it.copy(isParcelSearching = true) }
            delay(PARCEL_SEARCH_PLACEHOLDER_DELAY_MS)
            _uiState.update { it.copy(isParcelSearching = false) }
        }
    }

    private fun updateDimension(update: CalculatorUiState.() -> CalculatorUiState) {
        _uiState.update { state ->
            state.update().copy(
                packageInputMode = PackageInputMode.Exact
            )
        }
    }

    private fun resolvePackageTypeId(state: CalculatorUiState): String {
        if (state.selectedPackageTypeId.isNotBlank()) {
            return state.selectedPackageTypeId
        }

        val weight = state.weightInput.toFloatOrNull() ?: 0f
        return state.packageTypes
            .firstOrNull { weight <= it.maxWeightKg }
            ?.id
            ?: state.packageTypes.lastOrNull()?.id.orEmpty()
    }

    private companion object {
        const val PARCEL_SEARCH_PLACEHOLDER_DELAY_MS = 600L
    }
}
