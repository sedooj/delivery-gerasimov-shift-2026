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
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryCalculationRequest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.CalculateDeliveryUseCase
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.GetDeliveryPackageTypesUseCase
import ru.sedooj.delivery_gerasimov_shift_2026.domain.usecase.GetDeliveryPointsUseCase
import ru.sedooj.delivery_gerasimov_shift_2026.presentation.deliverymethod.DeliveryUiState

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getDeliveryPointsUseCase: GetDeliveryPointsUseCase,
    private val getDeliveryPackageTypesUseCase: GetDeliveryPackageTypesUseCase,
    private val calculateDeliveryUseCase: CalculateDeliveryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _deliveryUiState = MutableStateFlow<DeliveryUiState>(DeliveryUiState.Idle)
    val deliveryUiState: StateFlow<DeliveryUiState> = _deliveryUiState.asStateFlow()

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
                resetDeliveryState()
                _uiState.update {
                    it.copy(
                        selectedSenderPointId = intent.pointId,
                        cityPickerTarget = null
                    )
                }
            }

            is CalculatorIntent.ReceiverPointChanged -> {
                resetDeliveryState()
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
                resetDeliveryState()
                _uiState.update {
                    it.copy(
                        packageInputMode = intent.mode
                    )
                }
            }

            is CalculatorIntent.PackageTypeChanged -> {
                resetDeliveryState()
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

    fun calculateDelivery() {
        val request = runCatching {
            _uiState.value.toDeliveryCalculationRequest()
        }.getOrElse { throwable ->
            val message = throwable.message.orEmpty()
                .ifBlank { context.getString(R.string.calculator_error_fill_required) }
            _deliveryUiState.value = DeliveryUiState.Error(message)
            viewModelScope.launch {
                _effects.emit(CalculatorEffect.ShowMessage(message))
            }
            return
        }

        viewModelScope.launch {
            _deliveryUiState.value = DeliveryUiState.Loading

            runCatching {
                calculateDeliveryUseCase(request)
            }.onSuccess { options ->
                _deliveryUiState.value = DeliveryUiState.Success(options)
            }.onFailure { throwable ->
                val message = throwable.message.orEmpty()
                    .ifBlank { context.getString(R.string.calculator_error_calculation) }
                _deliveryUiState.value = DeliveryUiState.Error(message)
                _effects.emit(CalculatorEffect.ShowMessage(message))
            }
        }
    }

    fun resetDeliveryCalculation() {
        _deliveryUiState.value = DeliveryUiState.Idle
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
        resetDeliveryState()
        _uiState.update { state ->
            state.update().copy(
                packageInputMode = PackageInputMode.Exact
            )
        }
    }

    private fun CalculatorUiState.toDeliveryCalculationRequest(): DeliveryCalculationRequest {
        if (!canCalculate) {
            error(context.getString(R.string.calculator_error_fill_required))
        }

        val packageSize = resolvePackageSize()
        val senderPoint = deliveryPoints.firstOrNull { it.id == selectedSenderPointId }
        val receiverPoint = deliveryPoints.firstOrNull { it.id == selectedReceiverPointId }

        return DeliveryCalculationRequest(
            length = packageSize.length,
            width = packageSize.width,
            weight = packageSize.weight,
            height = packageSize.height,
            senderLatitude = senderPoint?.latitude ?: DEFAULT_LATITUDE,
            senderLongitude = senderPoint?.longitude ?: DEFAULT_LONGITUDE,
            receiverLatitude = receiverPoint?.latitude ?: DEFAULT_LATITUDE,
            receiverLongitude = receiverPoint?.longitude ?: DEFAULT_LONGITUDE
        )
    }

    private fun CalculatorUiState.resolvePackageSize(): PackageSize {
        return when (packageInputMode) {
            PackageInputMode.Approximate -> {
                packageTypes.firstOrNull { it.id == selectedPackageTypeId }
                    ?.toPackageSize()
                    ?: error(context.getString(R.string.calculator_error_fill_required))
            }

            PackageInputMode.Exact -> {
                PackageSize(
                    length = lengthInput.toDoubleOrNull()
                        ?: error(context.getString(R.string.calculator_error_fill_required)),
                    width = widthInput.toDoubleOrNull()
                        ?: error(context.getString(R.string.calculator_error_fill_required)),
                    height = heightInput.toDoubleOrNull()
                        ?: error(context.getString(R.string.calculator_error_fill_required)),
                    weight = weightInput.toDoubleOrNull()
                        ?: error(context.getString(R.string.calculator_error_fill_required))
                )
            }
        }
    }

    private fun DeliveryPackageType.toPackageSize(): PackageSize {
        return PackageSize(
            length = lengthCm,
            width = widthCm,
            height = heightCm,
            weight = weightKg
        )
    }

    private fun resetDeliveryState() {
        _deliveryUiState.value = DeliveryUiState.Idle
    }

    private data class PackageSize(
        val length: Double,
        val width: Double,
        val height: Double,
        val weight: Double
    )

    private companion object {
        const val PARCEL_SEARCH_PLACEHOLDER_DELAY_MS = 600L
        const val DEFAULT_LATITUDE = 56.8389
        const val DEFAULT_LONGITUDE = 60.6057
    }
}
