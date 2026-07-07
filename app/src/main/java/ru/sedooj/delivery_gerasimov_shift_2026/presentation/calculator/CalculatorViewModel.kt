package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.CalculateDeliveryUseCase
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.GetDeliveryPackageTypesUseCase
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.GetDeliveryPointsUseCase

@HiltViewModel
class CalculatorViewModel @Inject constructor(
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
            CalculatorIntent.CalculateClicked -> calculate()
            CalculatorIntent.ErrorDismissed -> _uiState.update { it.copy(errorMessage = null) }
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
                val message = throwable.message ?: "Не удалось загрузить данные для расчёта"
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                _effects.emit(CalculatorEffect.ShowMessage(message))
            }
        }
    }

    private fun calculate() {
        val currentState = _uiState.value
        if (!currentState.canCalculate) {
            viewModelScope.launch {
                val message = "Заполните обязательные поля перед расчётом"
                _uiState.update { it.copy(errorMessage = message) }
                _effects.emit(CalculatorEffect.ShowMessage(message))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true, errorMessage = null) }

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
                            amountText = "${calculation.amountRubles} \u20BD",
                            etaText = "${calculation.etaDays} дн.",
                            routeLabel = calculation.routeLabel,
                            deliveryTypeLabel = calculation.deliveryTypeLabel
                        )
                    )
                }
            }.onFailure { throwable ->
                val message = throwable.message ?: "Не удалось рассчитать доставку"
                _uiState.update { it.copy(isCalculating = false, errorMessage = message) }
                _effects.emit(CalculatorEffect.ShowMessage(message))
            }
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
}
