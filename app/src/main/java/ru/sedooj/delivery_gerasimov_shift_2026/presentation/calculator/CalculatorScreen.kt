package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import ru.sedooj.delivery_gerasimov_shift_2026.R
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint
import ru.sedooj.delivery_gerasimov_shift_2026.presentation.deliverymethod.DeliveryUiState
import ru.sedooj.delivery_gerasimov_shift_2026.ui.components.NunitoText
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Background
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.BorderHard
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Canvas
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.DeliveryCardBackground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Foreground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Ink
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Input
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.PrimaryForeground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Secondary
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.SecondaryForeground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Surface
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.SurfaceCard
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Surface as FieldStroke

@Composable
fun CalculatorRoute(
    viewModel: CalculatorViewModel = hiltViewModel(),
    onCalculateClick: () -> Unit = viewModel::calculateDelivery
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val deliveryUiState by viewModel.deliveryUiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is CalculatorEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    CalculatorScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        onCalculateClick = onCalculateClick,
        isDeliveryCalculationLoading = deliveryUiState is DeliveryUiState.Loading
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (CalculatorIntent) -> Unit,
    onCalculateClick: () -> Unit,
    isDeliveryCalculationLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val packageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(enabled = state.cityPickerTarget != null) {
        onIntent(CalculatorIntent.CityPickerDismissed)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Canvas,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Ink
                    )
                }

                state.cityPickerTarget != null -> {
                    CityPickerScreen(
                        title = if (state.cityPickerTarget == CityPickerTarget.Sender) {
                            stringResource(R.string.calculator_city_picker_from_title)
                        } else {
                            stringResource(R.string.calculator_city_picker_to_title)
                        },
                        cities = state.deliveryPoints,
                        onDismiss = { onIntent(CalculatorIntent.CityPickerDismissed) },
                        onCitySelected = { pointId ->
                            if (state.cityPickerTarget == CityPickerTarget.Sender) {
                                onIntent(CalculatorIntent.SenderPointChanged(pointId))
                            } else {
                                onIntent(CalculatorIntent.ReceiverPointChanged(pointId))
                            }
                        }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = CalculatorScreenDimens.screenHorizontalPadding,
                            end = CalculatorScreenDimens.screenHorizontalPadding,
                            top = CalculatorScreenDimens.screenTopPadding,
                            bottom = CalculatorScreenDimens.screenBottomPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            CalculatorScreenDimens.rootContentSpacing
                        )
                    ) {
                        item {
                            HeroLayout(
                                state = state,
                                onIntent = onIntent,
                                onCalculateClick = onCalculateClick,
                                isDeliveryCalculationLoading = isDeliveryCalculationLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            IllustrationCard(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }

    if (state.isPackageSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { onIntent(CalculatorIntent.PackageSheetDismissed) },
            modifier = Modifier.fillMaxHeight(),
            sheetState = packageSheetState,
            containerColor = SurfaceCard,
            tonalElevation = CalculatorScreenDimens.zeroElevation,
            dragHandle = null
        ) {
            PackageTypeBottomSheet(
                state = state,
                onIntent = onIntent,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HeroLayout(
    state: CalculatorUiState,
    onIntent: (CalculatorIntent) -> Unit,
    onCalculateClick: () -> Unit,
    isDeliveryCalculationLoading: Boolean,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val verticalLayout = maxWidth < CalculatorScreenDimens.twoPaneBreakpoint
        if (verticalLayout) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    CalculatorScreenDimens.heroContentSpacing
                )
            ) {
                CalculatorCard(
                    state = state,
                    onIntent = onIntent,
                    onCalculateClick = onCalculateClick,
                    isDeliveryCalculationLoading = isDeliveryCalculationLoading
                )
                ParcelTrackerCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            CalculatorScreenDimens.defaultBorderWidth,
                            Foreground,
                            RoundedCornerShape(CalculatorScreenDimens.primaryCardCornerRadius)
                        ),
                    value = state.trackingNumber,
                    searchButtonEnabled = state.canSearchParcel,
                    isSearching = state.isParcelSearching,
                    onValueChanged = { onIntent(CalculatorIntent.TrackingNumberChanged(it)) },
                    onClick = { onIntent(CalculatorIntent.ParcelSearchClicked) }
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    CalculatorScreenDimens.heroContentSpacing
                )
            ) {
                Box(modifier = Modifier.weight(CalculatorScreenDimens.heroPrimaryWeight)) {
                    CalculatorCard(
                        state = state,
                        onIntent = onIntent,
                        onCalculateClick = onCalculateClick,
                        isDeliveryCalculationLoading = isDeliveryCalculationLoading
                    )
                }
                Box(modifier = Modifier.weight(CalculatorScreenDimens.heroSecondaryWeight)) {
                    ParcelTrackerCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                CalculatorScreenDimens.defaultBorderWidth,
                                Foreground,
                                RoundedCornerShape(CalculatorScreenDimens.primaryCardCornerRadius)
                            ),
                        value = state.trackingNumber,
                        searchButtonEnabled = state.canSearchParcel,
                        isSearching = state.isParcelSearching,
                        onValueChanged = { onIntent(CalculatorIntent.TrackingNumberChanged(it)) },
                        onClick = { onIntent(CalculatorIntent.ParcelSearchClicked) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorCard(
    state: CalculatorUiState,
    onIntent: (CalculatorIntent) -> Unit,
    onCalculateClick: () -> Unit,
    isDeliveryCalculationLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(CalculatorScreenDimens.primaryCardCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .border(
                CalculatorScreenDimens.defaultBorderWidth,
                Foreground,
                RoundedCornerShape(CalculatorScreenDimens.primaryCardCornerRadius)
            )
    ) {
        Column(
            modifier = Modifier.padding(CalculatorScreenDimens.primaryCardPadding),
            verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.primaryCardSectionGap)
        ) {
            NunitoText(
                text = stringResource(R.string.calculator_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.fieldGroupGap)
            ) {
                CityField(
                    title = stringResource(R.string.calculator_sender_city_label),
                    value = state.deliveryPoints.selectedPointName(state.selectedSenderPointId),
                    icon = painterResource(R.drawable.green_ellipse),
                    quickCities = state.deliveryPoints.popularCities(state.selectedSenderPointId),
                    onFieldClick = { onIntent(CalculatorIntent.CityPickerOpened(CityPickerTarget.Sender)) },
                    onQuickCityClick = { onIntent(CalculatorIntent.SenderPointChanged(it.id)) }
                )

                CityField(
                    title = stringResource(R.string.calculator_receiver_city_label),
                    value = state.deliveryPoints.selectedPointName(state.selectedReceiverPointId),
                    icon = painterResource(R.drawable.black_ellipse),
                    quickCities = state.deliveryPoints.popularCities(state.selectedReceiverPointId),
                    onFieldClick = { onIntent(CalculatorIntent.CityPickerOpened(CityPickerTarget.Receiver)) },
                    onQuickCityClick = { onIntent(CalculatorIntent.ReceiverPointChanged(it.id)) }
                )

                PackageField(
                    title = stringResource(R.string.calculator_package_size_label),
                    value = packageFieldText(state),
                    onClick = { onIntent(CalculatorIntent.PackageSheetOpened) }
                )
            }

            Surface(
                color = Foreground,
                shape = RoundedCornerShape(CalculatorScreenDimens.capsuleCornerRadius),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onCalculateClick,
                    enabled = state.canCalculate && !isDeliveryCalculationLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(CalculatorScreenDimens.primaryActionHeight)
                ) {
                    if (isDeliveryCalculationLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(CalculatorScreenDimens.buttonLoaderSize),
                            color = SurfaceCard,
                            strokeWidth = CalculatorScreenDimens.loaderStrokeWidth
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NunitoText(
                                text = stringResource(R.string.calculator_action_calculate),
                                color = PrimaryForeground,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = CalculatorScreenTypography.mediumLetterSpacing,
                                    lineHeight = CalculatorScreenTypography.buttonLineHeight
                                )
                            )
                            Spacer(modifier = Modifier.width(CalculatorScreenDimens.inlineIconGap))
                            Icon(
                                painter = painterResource(R.drawable.arrow_right),
                                contentDescription = null,
                                tint = PrimaryForeground
                            )
                        }
                    }
                }
            }

            state.errorMessageRes?.let { errorRes ->
                NunitoText(
                    text = stringResource(errorRes),
                    color = CalculatorScreenColors.errorText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ParcelTrackerCard(
    modifier: Modifier = Modifier,
    value: String,
    searchButtonEnabled: Boolean,
    isSearching: Boolean,
    onValueChanged: (String) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(CalculatorScreenDimens.primaryCardCornerRadius),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(CalculatorScreenDimens.primaryCardPadding),
            verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.primaryCardSectionGap)
        ) {
            NunitoText(
                text = stringResource(R.string.calculator_tracking_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = CalculatorScreenTypography.mediumLetterSpacing,
                    lineHeight = CalculatorScreenTypography.headlineLineHeight
                )
            )
            OutlinedTextField(
                value = value,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '.' || it == ',' }) {
                        onValueChanged(input.replace(',', '.'))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = CalculatorScreenDimens.trackingFieldMinHeight),
                shape = RoundedCornerShape(CalculatorScreenDimens.capsuleCornerRadius),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = Foreground,
                    fontWeight = FontWeight.Medium
                ),
                placeholder = {
                    NunitoText(
                        text = stringResource(R.string.calculator_tracking_placeholder),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Normal,
                            lineHeight = CalculatorScreenTypography.trackingPlaceholderLineHeight,
                            letterSpacing = CalculatorScreenTypography.mediumLetterSpacing,
                            color = Input
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedIndicatorColor = FieldStroke,
                    unfocusedIndicatorColor = FieldStroke,
                    focusedTextColor = Foreground,
                    unfocusedTextColor = Foreground
                )
            )
            Surface(
                color = Foreground,
                shape = RoundedCornerShape(CalculatorScreenDimens.capsuleCornerRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CalculatorScreenDimens.trackingActionHeight)
            ) {
                TextButton(
                    onClick = onClick,
                    enabled = searchButtonEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(CalculatorScreenDimens.buttonLoaderSize),
                            color = SurfaceCard,
                            strokeWidth = CalculatorScreenDimens.loaderStrokeWidth
                        )
                    } else {
                        NunitoText(
                            text = stringResource(R.string.calculator_tracking_action),
                            color = Input,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = CalculatorScreenTypography.mediumLetterSpacing,
                                lineHeight = CalculatorScreenTypography.defaultLineHeight
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CityField(
    title: String,
    value: String,
    icon: Painter,
    quickCities: List<DeliveryPoint>,
    onFieldClick: () -> Unit,
    onQuickCityClick: (DeliveryPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.fieldTitleGap)
    ) {
        FieldLabel(text = title)
        SelectorFieldContainer(
            value = value,
            prefix = {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(CalculatorScreenDimens.cityMarkerSize)
                )
            },
            onClick = onFieldClick
        )
        PopularCitiesRow(
            cities = quickCities,
            onCityClick = onQuickCityClick
        )
    }
}

@Composable
private fun PackageField(
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.fieldTitleGap)
    ) {
        FieldLabel(text = title)
        SelectorFieldContainer(
            value = value,
            onClick = onClick,
            valueColor = Foreground
        )
    }
}

@Composable
private fun SelectorFieldContainer(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    valueColor: Color = Foreground,
    prefix: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CalculatorScreenDimens.capsuleCornerRadius))
            .border(
                CalculatorScreenDimens.defaultBorderWidth,
                FieldStroke,
                RoundedCornerShape(CalculatorScreenDimens.capsuleCornerRadius)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = CalculatorScreenDimens.selectorHorizontalPadding,
                vertical = CalculatorScreenDimens.selectorVerticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefix != null) {
            prefix()
            Spacer(modifier = Modifier.width(CalculatorScreenDimens.selectorPrefixGap))
        }
        NunitoText(
            text = value,
            modifier = Modifier.weight(CalculatorScreenDimens.fullWeight),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = valueColor,
                fontWeight = FontWeight.Medium,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                lineHeight = CalculatorScreenTypography.defaultLineHeight
            )
        )
        Icon(
            painter = painterResource(R.drawable.chevron_down),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun FieldLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    NunitoText(
        text = text,
        modifier = modifier,
        color = Foreground,
        style = MaterialTheme.typography.bodyMedium.copy(
            letterSpacing = CalculatorScreenTypography.mediumLetterSpacing,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun PopularCitiesRow(
    cities: List<DeliveryPoint>,
    onCityClick: (DeliveryPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.popularCityHorizontalGap),
        verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.popularCityVerticalGap)
    ) {
        cities.forEach { city ->
            NunitoText(
                text = city.name,
                modifier = Modifier.clickable { onCityClick(city) },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Surface,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.Medium,
                    lineHeight = CalculatorScreenTypography.defaultLineHeight,
                    letterSpacing = CalculatorScreenTypography.mediumLetterSpacing
                ),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun CityPickerScreen(
    title: String,
    cities: List<DeliveryPoint>,
    onDismiss: () -> Unit,
    onCitySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = CalculatorScreenDimens.cityPickerTopPadding,
                    start = CalculatorScreenDimens.cityPickerStartPadding,
                    end = CalculatorScreenDimens.screenHorizontalPadding,
                    bottom = CalculatorScreenDimens.cityPickerBottomPadding
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = CalculatorScreenDimens.cityPickerHeaderBottomPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(CalculatorScreenDimens.cityPickerCloseSize)
                        .clickable(onClick = onDismiss),
                    tint = Foreground
                )
                Spacer(modifier = Modifier.width(CalculatorScreenDimens.cityPickerTitleGap))
                NunitoText(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = CalculatorScreenTypography.cityPickerTitleSize,
                        lineHeight = CalculatorScreenTypography.cityPickerTitleLineHeight
                    )
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.cityPickerItemGap)
            ) {
                items(cities) { city ->
                    CityPickerRow(
                        city = city,
                        onClick = { onCitySelected(city.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CityPickerRow(
    city: DeliveryPoint,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = CalculatorScreenDimens.cityPickerRowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NunitoText(
            text = city.name,
            modifier = Modifier.weight(CalculatorScreenDimens.fullWeight),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                color = Foreground
            )
        )
        Icon(
            painter = painterResource(R.drawable.chevron_down),
            contentDescription = null,
            modifier = Modifier.rotate(CalculatorScreenDimens.cityPickerChevronRotation),
            tint = FieldStroke
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PackageTypeBottomSheet(
    state: CalculatorUiState,
    onIntent: (CalculatorIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = CalculatorScreenDimens.sheetHorizontalPadding,
                vertical = CalculatorScreenDimens.sheetVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.sheetSectionGap)
    ) {
        NunitoText(
            text = stringResource(R.string.calculator_package_sheet_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                lineHeight = CalculatorScreenTypography.headlineLineHeight,
                letterSpacing = CalculatorScreenTypography.mediumLetterSpacing,
            ),
            modifier = Modifier.padding(horizontal = CalculatorScreenDimens.heroContentSpacing)
        )

        PackageModeSwitcher(
            selectedMode = state.packageInputMode,
            onModeChanged = { onIntent(CalculatorIntent.PackageInputModeChanged(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        when (state.packageInputMode) {
            PackageInputMode.Approximate -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.packagePresetGap)
                ) {
                    items(
                        items = state.packageTypes,
                        key = { packageType -> packageType.id }
                    ) { packageType ->
                        PackagePresetCard(
                            packageType = packageType,
                            isSelected = state.selectedPackageTypeId == packageType.id,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(CalculatorScreenDimens.packagePresetCardHeight)
                                .clip(RoundedCornerShape(CalculatorScreenDimens.packagePresetCornerRadius))
                                .border(
                                    width = CalculatorScreenDimens.defaultBorderWidth,
                                    color = BorderHard,
                                    shape = RoundedCornerShape(CalculatorScreenDimens.packagePresetCornerRadius)
                                )
                                .clickable(onClick = {
                                    onIntent(
                                        CalculatorIntent.PackageTypeChanged(
                                            packageType.id
                                        )
                                    )
                                })
                                .padding(
                                    horizontal = CalculatorScreenDimens.packagePresetHorizontalPadding,
                                    vertical = CalculatorScreenDimens.packagePresetVerticalPadding
                                )
                        )
                    }
                }
            }

            PackageInputMode.Exact -> {
                Column(verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.exactFieldGap)) {
                    ExactDimensionField(
                        label = stringResource(R.string.calculator_dimension_length),
                        value = state.lengthInput,
                        placeholder = stringResource(R.string.calculator_dimension_centimeters_placeholder),
                        onValueChanged = { onIntent(CalculatorIntent.LengthChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExactDimensionField(
                        label = stringResource(R.string.calculator_dimension_width),
                        value = state.widthInput,
                        placeholder = stringResource(R.string.calculator_dimension_centimeters_placeholder),
                        onValueChanged = { onIntent(CalculatorIntent.WidthChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExactDimensionField(
                        label = stringResource(R.string.calculator_dimension_height),
                        value = state.heightInput,
                        placeholder = stringResource(R.string.calculator_dimension_centimeters_placeholder),
                        onValueChanged = { onIntent(CalculatorIntent.HeightChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExactDimensionField(
                        label = stringResource(R.string.calculator_dimension_weight),
                        value = state.weightInput,
                        placeholder = stringResource(R.string.calculator_dimension_kilograms_placeholder),
                        onValueChanged = { onIntent(CalculatorIntent.WeightChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun PackageModeSwitcher(
    selectedMode: PackageInputMode,
    onModeChanged: (PackageInputMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CalculatorScreenColors.segmentedControlContainer,
        shape = RoundedCornerShape(CalculatorScreenDimens.capsuleCornerRadius),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CalculatorScreenDimens.segmentedControlPadding),
            horizontalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.segmentedControlGap)
        ) {
            PackageModeButton(
                text = stringResource(R.string.calculator_package_mode_approximate),
                selected = selectedMode == PackageInputMode.Approximate,
                modifier = Modifier.weight(CalculatorScreenDimens.fullWeight),
                onClick = { onModeChanged(PackageInputMode.Approximate) }
            )
            PackageModeButton(
                text = stringResource(R.string.calculator_package_mode_exact),
                selected = selectedMode == PackageInputMode.Exact,
                modifier = Modifier.weight(CalculatorScreenDimens.fullWeight),
                onClick = { onModeChanged(PackageInputMode.Exact) }
            )
        }
    }
}

@Composable
private fun PackageModeButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) SurfaceCard else Color.Transparent,
        shape = RoundedCornerShape(CalculatorScreenDimens.capsuleCornerRadius),
        shadowElevation = if (selected) CalculatorScreenDimens.segmentedControlSelectedElevation else CalculatorScreenDimens.zeroElevation,
        modifier = modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(CalculatorScreenDimens.capsuleCornerRadius))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = CalculatorScreenDimens.segmentedControlItemVerticalPadding),
            contentAlignment = Alignment.Center
        ) {
            NunitoText(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Foreground,
                    fontSize = CalculatorScreenTypography.exactFieldLabelLineHeight,
                    letterSpacing = CalculatorScreenTypography.mediumLetterSpacing,
                    lineHeight = CalculatorScreenTypography.switcherLineHeight

                )
            )
        }
    }
}

@Composable
private fun PackagePresetCard(
    packageType: DeliveryPackageType,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.packagePresetContentGap)
    ) {
        PackagePresetThumb(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    start = CalculatorScreenDimens.packagePresetGap,
                    top = CalculatorScreenDimens.packagePresetGap
                )
                .align(Alignment.Top),
            packageType = packageType
        )
        Column(modifier = Modifier.weight(CalculatorScreenDimens.fullWeight), verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.packageGapBetweenTitleAndSubtitle)) {
            NunitoText(
                text = packageType.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = CalculatorScreenTypography.packagePresetTitleSize,
                    lineHeight = CalculatorScreenTypography.packagePresetTitleLineHeight,
                    color = Foreground,
                    letterSpacing = CalculatorScreenTypography.packagePresetTitleLetterSpacing
                )
            )
            NunitoText(
                text = packageType.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Foreground,
                    fontWeight = FontWeight.Medium,
                    fontSize = CalculatorScreenTypography.packagePresetSubtitleSize,
                    lineHeight = CalculatorScreenTypography.packagePresetSubtitleLineHeight,
                    letterSpacing = CalculatorScreenTypography.packagePresetSubtitleLetterSpacing
                )
            )
        }
        SelectionDot(
            selected = isSelected,
            modifier = Modifier.align(Alignment.Top).padding(top = 16.dp).size(CalculatorScreenDimens.selectionDotSize)
        )
    }
}

@Composable
private fun PackagePresetThumb(
    modifier: Modifier = Modifier,
    packageType: DeliveryPackageType
) {
    val imageResId = packageType.getIconResOrNull()
    imageResId?.let { iconResId ->
        Box(
            modifier = modifier,
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(iconResId),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun SelectionDot(
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (selected) SecondaryForeground else Secondary)
            .border(
                width = CalculatorScreenDimens.defaultBorderWidth,
                color = if (selected) SecondaryForeground else Secondary,
                shape = CircleShape
            )
    )
}

@Composable
private fun ExactDimensionField(
    label: String,
    value: String,
    placeholder: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.exactFieldLabelGap)
    ) {
        NunitoText(
            text = stringResource(R.string.calculator_dimension_field_label, label),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Foreground,
                fontSize = CalculatorScreenTypography.exactFieldLabelSize,
                lineHeight = CalculatorScreenTypography.exactFieldLabelLineHeight
            )
        )
        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (input.all { it.isDigit() || it == '.' || it == ',' }) {
                    onValueChanged(input.replace(',', '.'))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CalculatorScreenDimens.capsuleCornerRadius),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Foreground,
                fontWeight = FontWeight.Medium
            ),
            placeholder = {
                NunitoText(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = FieldStroke,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard,
                focusedIndicatorColor = FieldStroke,
                unfocusedIndicatorColor = FieldStroke,
                focusedTextColor = Foreground,
                unfocusedTextColor = Foreground
            )
        )
    }
}

@Composable
private fun IllustrationCard(
    modifier: Modifier = Modifier
) {
    Surface(
        color = DeliveryCardBackground,
        shape = RoundedCornerShape(CalculatorScreenDimens.primaryCardCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = CalculatorScreenDimens.illustrationCardHeight)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Image(
                painter = painterResource(R.drawable.hands),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(
                        width = CalculatorScreenDimens.illustrationWidth,
                        height = CalculatorScreenDimens.illustrationHeight
                    )
                    .offset(
                        x = CalculatorScreenDimens.illustrationOffsetX,
                        y = CalculatorScreenDimens.illustrationOffsetY
                    )
                    .rotate(CalculatorScreenDimens.illustrationRotation)
            )
        }
        Column(
            modifier = Modifier.padding(CalculatorScreenDimens.illustrationCardPadding),
            verticalArrangement = Arrangement.spacedBy(CalculatorScreenDimens.illustrationTextGap, alignment = Alignment.CenterVertically)
        ) {
            NunitoText(
                text = stringResource(R.string.calculator_banner_title),
                color = PrimaryForeground,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = CalculatorScreenTypography.mediumLetterSpacing,
                    lineHeight = CalculatorScreenTypography.headlineLineHeight
                )
            )
            NunitoText(
                text = stringResource(R.string.calculator_banner_subtitle),
                color = PrimaryForeground,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = CalculatorScreenTypography.mediumLetterSpacing,
                    lineHeight = CalculatorScreenTypography.sheetTitleLineHeight
                )
            )
        }
    }
}

@Composable
private fun packageFieldText(state: CalculatorUiState): String {
    return when {
        state.packageInputMode == PackageInputMode.Exact && state.hasExactDimensions -> {
            stringResource(
                R.string.calculator_package_exact_format,
                state.lengthInput,
                state.widthInput,
                state.heightInput,
                state.weightInput
            )
        }

        state.packageInputMode == PackageInputMode.Approximate && state.selectedPackageTypeId.isNotBlank() -> {
            state.packageTypes.selectedPackageTypeName(state.selectedPackageTypeId)
        }

        else -> stringResource(R.string.calculator_package_not_selected)
    }
}

private fun List<DeliveryPoint>.selectedPointName(selectedId: String): String {
    return firstOrNull { it.id == selectedId }?.name.orEmpty()
}

private fun List<DeliveryPoint>.popularCities(selectedId: String): List<DeliveryPoint> {
    return filterNot { it.id == selectedId }
        .take(CalculatorScreenNumbers.maxQuickCityCount)
}

private fun List<DeliveryPackageType>.selectedPackageTypeName(selectedId: String): String {
    return firstOrNull { it.id == selectedId }?.name.orEmpty()
}

@DrawableRes
private fun DeliveryPackageType.getIconResOrNull(): Int? {
    return when (this.id) {
        CalculatorScreenIds.packageEnvelope -> R.drawable.envelope
        CalculatorScreenIds.packageBoxXs -> R.drawable.box_xs
        CalculatorScreenIds.packageBoxS -> R.drawable.box_s
        CalculatorScreenIds.packageBoxM -> R.drawable.box_m
        CalculatorScreenIds.packageBoxL -> R.drawable.box_l
        CalculatorScreenIds.packageBoxXl -> R.drawable.box_xl
        else -> null
    }
}

private object CalculatorScreenIds {
    const val packageEnvelope = "envelope"
    const val packageBoxXs = "box_xs"
    const val packageBoxS = "box-s"
    const val packageBoxM = "box-m"
    const val packageBoxL = "box-l"
    const val packageBag = "bag"
    const val packagePallet = "pallet"
    const val packageBoxXl = "box-xl"
}

private object CalculatorScreenNumbers {
    const val maxQuickCityCount = 4
}

private object CalculatorScreenAlpha {
    const val unselectedBorderAlpha = 0.45f
}

private object CalculatorScreenTypography {
    val mediumLetterSpacing = 0.5.sp
    val defaultLineHeight = 24.sp
    val buttonLineHeight = 21.sp
    val headlineLineHeight = 32.sp
    val trackingPlaceholderLineHeight = 24.sp
    val switcherLineHeight = 26.sp
    val cityPickerTitleSize = 20.sp
    val cityPickerTitleLineHeight = 28.sp
    val sheetTitleLineHeight = 22.sp
    val packagePresetTitleSize = 24.sp
    val packagePresetSubtitleSize = 14.sp
    val packagePresetTitleLineHeight = 32.sp
    val packagePresetSubtitleLineHeight = 22.sp
    val packagePresetTitleLetterSpacing = 0.5.sp
    val packagePresetSubtitleLetterSpacing = 0.5.sp
    val exactFieldLabelSize = 13.sp
    val exactFieldLabelLineHeight = 18.sp
}

private object CalculatorScreenColors {
    val errorText = Color(0xFFB53B2D)
    val segmentedControlContainer = Color(0xFFF4F4F1)
    val selectionDotBackground = Color(0xFFF4F4F1)
    val selectionDotBorder = Color(0xFFE7E4DC)
}

private object CalculatorScreenDimens {
    val screenHorizontalPadding = 16.dp
    val screenTopPadding = 32.dp
    val screenBottomPadding = 98.dp
    val rootContentSpacing = 8.dp
    val heroContentSpacing = 8.dp
    val primaryCardCornerRadius = 24.dp
    val primaryCardPadding = 24.dp
    val primaryCardSectionGap = 24.dp
    val fieldGroupGap = 16.dp
    val fieldTitleGap = 4.dp
    val primaryActionHeight = 58.dp
    val trackingActionHeight = 52.dp
    val trackingFieldMinHeight = 52.dp
    val buttonLoaderSize = 22.dp
    val loaderStrokeWidth = 2.dp
    val inlineIconGap = 8.dp
    val defaultBorderWidth = 1.dp
    val primaryCardWeight = 1f
    val fullWeight = 1f
    val heroPrimaryWeight = 1.1f
    val heroSecondaryWeight = 0.9f
    val twoPaneBreakpoint = 720.dp
    val capsuleCornerRadius = 9999.dp
    val selectorHorizontalPadding = 14.dp
    val selectorVerticalPadding = 12.dp
    val selectorPrefixGap = 8.dp
    val cityMarkerSize = 14.dp
    val popularCityHorizontalGap = 8.dp
    val popularCityVerticalGap = 8.dp
    val cityPickerTopPadding = 38.dp
    val cityPickerStartPadding = 10.dp
    val cityPickerBottomPadding = 24.dp
    val cityPickerHeaderBottomPadding = 20.dp
    val cityPickerCloseSize = 28.dp
    val cityPickerTitleGap = 12.dp
    val cityPickerItemGap = 2.dp
    val cityPickerRowVerticalPadding = 16.dp
    val cityPickerChevronRotation = -90f
    val sheetHorizontalPadding = 16.dp
    val sheetVerticalPadding = 16.dp
    val sheetSectionGap = 16.dp
    const val sheetMaxHeightFraction = 0.86f
    val segmentedControlPadding = 4.dp
    val segmentedControlGap = 4.dp
    val segmentedControlItemVerticalPadding = 12.dp
    val segmentedControlSelectedElevation = 2.dp
    val packagePresetGap = 8.dp
    val packagePresetListMaxHeight = 392.dp
    val packageGapBetweenTitleAndSubtitle = 4.dp
    val packagePresetCornerRadius = 16.dp
    val packagePresetHorizontalPadding = 16.dp
    val packageCardSelectedDotGap = 16.dp
    val packagePresetVerticalPadding = 8.dp
    val packagePresetContentGap = 16.dp
    val packagePresetThumbSize = 36.dp
    val packagePresetThumbCornerRadius = 10.dp
    val packagePresetCardHeight = 90.dp
    val selectionDotSize = 16.dp
    val exactFieldGap = 12.dp
    val exactFieldLabelGap = 6.dp
    val illustrationCardHeight = 90.dp
    val illustrationWidth = 178.52.dp
    val illustrationHeight = 111.8.dp
    val illustrationOffsetX = 42.dp
    val illustrationOffsetY = (-8).dp
    const val illustrationRotation = 42.77f
    val illustrationCardPadding = 16.dp
    val illustrationTextGap = 4.dp
    val zeroElevation = 0.dp
}
