package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.GetDeliveryPackageTypesUseCase
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.GetDeliveryPointsUseCase

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getDeliveryPointsUseCase: GetDeliveryPointsUseCase,
    private val getDeliveryPackageTypesUseCase: GetDeliveryPackageTypesUseCase
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
                        cityPickerTarget = null
                    )
                }
            }

            is CalculatorIntent.ReceiverPointChanged -> {
                _uiState.update {
                    it.copy(
                        selectedReceiverPointId = intent.pointId,
                        cityPickerTarget = null
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
                        packageInputMode = intent.mode
                    )
                }
            }

            is CalculatorIntent.PackageTypeChanged -> {
                _uiState.update {
                    it.copy(
                        selectedPackageTypeId = intent.packageTypeId,
                        packageInputMode = PackageInputMode.Approximate,
                        isPackageSheetVisible = false
                    )
                }
            }

            is CalculatorIntent.LengthChanged -> updateDimension { copy(lengthInput = intent.value) }
            is CalculatorIntent.WidthChanged -> updateDimension { copy(widthInput = intent.value) }
            is CalculatorIntent.HeightChanged -> updateDimension { copy(heightInput = intent.value) }
            is CalculatorIntent.WeightChanged -> updateDimension { copy(weightInput = intent.value) }
            is CalculatorIntent.TrackingNumberChanged -> {
                _uiState.update { it.copy(trackingNumber = intent.value) }
            }

            CalculatorIntent.ParcelSearchClicked -> searchParcel()
            CalculatorIntent.ErrorDismissed -> _uiState.update { it.copy(errorMessageRes = null) }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            runCatching {
                val points = getDeliveryPointsUseCase()
                val packageTypes = getDeliveryPackageTypesUseCase()
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

    private companion object {
        const val PARCEL_SEARCH_PLACEHOLDER_DELAY_MS = 600L
    }
}
