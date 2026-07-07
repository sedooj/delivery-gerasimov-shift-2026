package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import ru.sedooj.delivery_gerasimov_shift_2026.R
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.AccentGreenSoft
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Background
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Canvas
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Foreground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Green_500
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Ink
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.InkSoft
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.PrimaryForeground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Surface as FieldStroke
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.SurfaceCard
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.nunitoFontFamily

@Composable
fun CalculatorRoute(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is CalculatorEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    CalculatorScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (CalculatorIntent) -> Unit
) {
    val packageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(enabled = state.cityPickerTarget != null) {
        onIntent(CalculatorIntent.CityPickerDismissed)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                            "Откуда"
                        } else {
                            "Куда"
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
                            start = 16.dp,
                            end = 16.dp,
                            top = 32.dp,
                            bottom = 98.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            HeroLayout(
                                state = state,
                                onIntent = onIntent
                            )
                        }
                        item { PromoBanner() }
                        item {
                            AnimatedVisibility(visible = state.quote != null) {
                                state.quote?.let { quote ->
                                    QuoteCard(quote = quote)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.isPackageSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { onIntent(CalculatorIntent.PackageSheetDismissed) },
            sheetState = packageSheetState,
            containerColor = SurfaceCard,
            tonalElevation = 0.dp,
            dragHandle = null
        ) {
            PackageTypeBottomSheet(
                state = state,
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun HeroLayout(
    state: CalculatorUiState,
    onIntent: (CalculatorIntent) -> Unit
) {
    BoxWithConstraints {
        val verticalLayout = maxWidth < 720.dp
        if (verticalLayout) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CalculatorCard(state = state, onIntent = onIntent)
                IllustrationCard()
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1.1f)) {
                    CalculatorCard(state = state, onIntent = onIntent)
                }
                Box(modifier = Modifier.weight(0.9f)) {
                    IllustrationCard()
                }
            }
        }
    }
}

@Composable
private fun CalculatorCard(
    state: CalculatorUiState,
    onIntent: (CalculatorIntent) -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Foreground, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Рассчитать доставку",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CityField(
                    title = "Город отправки",
                    value = state.deliveryPoints.selectedPointName(state.selectedSenderPointId),
                    icon = painterResource(R.drawable.green_ellipse),
                    quickCities = state.deliveryPoints.popularCities(state.selectedSenderPointId),
                    onFieldClick = { onIntent(CalculatorIntent.CityPickerOpened(CityPickerTarget.Sender)) },
                    onQuickCityClick = { onIntent(CalculatorIntent.SenderPointChanged(it.id)) }
                )

                CityField(
                    title = "Город назначения",
                    value = state.deliveryPoints.selectedPointName(state.selectedReceiverPointId),
                    icon = painterResource(R.drawable.black_ellipse),
                    quickCities = state.deliveryPoints.popularCities(state.selectedReceiverPointId),
                    onFieldClick = { onIntent(CalculatorIntent.CityPickerOpened(CityPickerTarget.Receiver)) },
                    onQuickCityClick = { onIntent(CalculatorIntent.ReceiverPointChanged(it.id)) }
                )

                PackageField(
                    title = "Размер посылки",
                    value = state.packageFieldText(),
                    isPlaceholder = state.packageFieldText() == "Не выбран",
                    onClick = { onIntent(CalculatorIntent.PackageSheetOpened) }
                )
            }

            Surface(
                color = Foreground,
                shape = RoundedCornerShape(9999.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = { onIntent(CalculatorIntent.CalculateClicked) },
                    enabled = state.canCalculate && !state.isCalculating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    if (state.isCalculating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = SurfaceCard,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Рассчитать",
                                color = PrimaryForeground,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp,
                                    lineHeight = 21.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(R.drawable.arrow_right),
                                contentDescription = null,
                                tint = PrimaryForeground
                            )
                        }
                    }
                }
            }

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = Color(0xFFB53B2D),
                    style = MaterialTheme.typography.bodyMedium
                )
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
    onQuickCityClick: (DeliveryPoint) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        FieldLabel(text = title)
        SelectorFieldContainer(
            value = value,
            prefix = {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(14.dp)
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
    isPlaceholder: Boolean,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        FieldLabel(text = title)
        SelectorFieldContainer(
            value = value,
            onClick = onClick,
            valueColor = if (isPlaceholder) InkSoft else Foreground
        )
    }
}

@Composable
private fun SelectorFieldContainer(
    value: String,
    onClick: () -> Unit,
    valueColor: Color = Foreground,
    prefix: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(9999.dp))
            .border(1.dp, FieldStroke, RoundedCornerShape(9999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefix != null) {
            prefix()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = valueColor,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        )
        Icon(
            painter = painterResource(R.drawable.chevron_down),
            contentDescription = null,
            tint = Foreground
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = Foreground,
        style = MaterialTheme.typography.bodyMedium.copy(
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun PopularCitiesRow(
    cities: List<DeliveryPoint>,
    onCityClick: (DeliveryPoint) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        cities.forEach { city ->
            Text(
                text = city.name,
                modifier = Modifier.clickable { onCityClick(city) },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = FieldStroke,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}

@Composable
private fun CityPickerScreen(
    title: String,
    cities: List<DeliveryPoint>,
    onDismiss: () -> Unit,
    onCitySelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 38.dp, start = 10.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onDismiss),
                    tint = Foreground
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        lineHeight = 28.sp
                    )
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(cities) { city ->
                    CityPickerRow(
                        city = city,
                        onClick = { onCitySelected(city.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CityPickerRow(
    city: DeliveryPoint,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = city.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                color = Foreground
            )
        )
        Icon(
            painter = painterResource(R.drawable.chevron_down),
            contentDescription = null,
            modifier = Modifier.rotate(-90f),
            tint = FieldStroke
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PackageTypeBottomSheet(
    state: CalculatorUiState,
    onIntent: (CalculatorIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Размер посылки",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                lineHeight = 28.sp
            )
        )

        PackageModeSwitcher(
            selectedMode = state.packageInputMode,
            onModeChanged = { onIntent(CalculatorIntent.PackageInputModeChanged(it)) }
        )

        when (state.packageInputMode) {
            PackageInputMode.Approximate -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.packageTypes.forEachIndexed { index, packageType ->
                        PackagePresetCard(
                            packageType = packageType,
                            accentColor = presetAccentColor(index),
                            isSelected = state.selectedPackageTypeId == packageType.id,
                            onClick = {
                                onIntent(CalculatorIntent.PackageTypeChanged(packageType.id))
                            }
                        )
                    }
                }
            }

            PackageInputMode.Exact -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExactDimensionField(
                        label = "Длина",
                        value = state.lengthInput,
                        placeholder = "СМ",
                        onValueChanged = { onIntent(CalculatorIntent.LengthChanged(it)) }
                    )
                    ExactDimensionField(
                        label = "Ширина",
                        value = state.widthInput,
                        placeholder = "СМ",
                        onValueChanged = { onIntent(CalculatorIntent.WidthChanged(it)) }
                    )
                    ExactDimensionField(
                        label = "Высота",
                        value = state.heightInput,
                        placeholder = "СМ",
                        onValueChanged = { onIntent(CalculatorIntent.HeightChanged(it)) }
                    )
                    ExactDimensionField(
                        label = "Вес",
                        value = state.weightInput,
                        placeholder = "КГ",
                        onValueChanged = { onIntent(CalculatorIntent.WeightChanged(it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PackageModeSwitcher(
    selectedMode: PackageInputMode,
    onModeChanged: (PackageInputMode) -> Unit
) {
    Surface(
        color = Color(0xFFF4F4F1),
        shape = RoundedCornerShape(9999.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PackageModeButton(
                text = "Примерные",
                selected = selectedMode == PackageInputMode.Approximate,
                modifier = Modifier.weight(1f),
                onClick = { onModeChanged(PackageInputMode.Approximate) }
            )
            PackageModeButton(
                text = "Точные",
                selected = selectedMode == PackageInputMode.Exact,
                modifier = Modifier.weight(1f),
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
        shape = RoundedCornerShape(9999.dp),
        shadowElevation = if (selected) 2.dp else 0.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Foreground
                )
            )
        }
    }
}

@Composable
private fun PackagePresetCard(
    packageType: DeliveryPackageType,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) Foreground else FieldStroke.copy(alpha = 0.45f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PackagePresetThumb(
            accentColor = accentColor,
            label = packageType.shortLabel()
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = packageType.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                )
            )
            Text(
                text = packageType.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Foreground,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }
        SelectionDot(selected = isSelected)
    }
}

@Composable
private fun PackagePresetThumb(
    accentColor: Color,
    label: String
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(accentColor, accentColor.copy(alpha = 0.55f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Black,
                color = Foreground
            )
        )
    }
}

@Composable
private fun SelectionDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(if (selected) Foreground else Color(0xFFF4F4F1))
            .border(
                width = 1.dp,
                color = if (selected) Foreground else Color(0xFFE7E4DC),
                shape = CircleShape
            )
    )
}

@Composable
private fun ExactDimensionField(
    label: String,
    value: String,
    placeholder: String,
    onValueChanged: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "$label.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Foreground,
                fontSize = 13.sp,
                lineHeight = 18.sp
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
            shape = RoundedCornerShape(9999.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Foreground,
                fontWeight = FontWeight.Medium
            ),
            placeholder = {
                Text(
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
private fun IllustrationCard() {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, Ink, RoundedCornerShape(30.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF79D367),
                                Color(0xFFB9EDA9)
                            )
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(72.dp)
                )
                Text(
                    text = "Быстро,\nпрозрачно,\nнадёжно",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Ink
                    )
                )
            }

            Surface(
                color = Color(0xFF8DDA7B),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Бесплатная доставка",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "для каждого третьего заказа после регистрации",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = InkSoft
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PromoBanner() {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, Ink, RoundedCornerShape(28.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Точный расчёт перед отправкой",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Выберите города, укажите формат посылки и сразу получите стоимость и срок.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = InkSoft
                    )
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentGreenSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    tint = Ink
                )
            }
        }
    }
}

@Composable
private fun QuoteCard(quote: CalculatorQuoteUi) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, Ink, RoundedCornerShape(30.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Ваш заказ",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = quote.deliveryTypeLabel,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = InkSoft
                )
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            QuoteRow(title = "Маршрут", value = quote.routeLabel)
            QuoteRow(title = "Срок", value = quote.etaText)
            QuoteRow(title = "Итого", value = quote.amountText, highlighted = true)
        }
    }
}

@Composable
private fun QuoteRow(
    title: String,
    value: String,
    highlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = InkSoft
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = if (highlighted) {
                MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            } else {
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            },
            textAlign = TextAlign.End
        )
    }
}

private fun List<DeliveryPoint>.selectedPointName(selectedId: String): String {
    return firstOrNull { it.id == selectedId }?.name.orEmpty()
}

private fun List<DeliveryPoint>.popularCities(selectedId: String): List<DeliveryPoint> {
    val preferredOrder = listOf("spb", "nsk", "tmk", "msk")
    return preferredOrder
        .filterNot { it == selectedId }
        .mapNotNull { id -> firstOrNull { it.id == id } }
}

private fun CalculatorUiState.packageFieldText(): String {
    return when {
        packageInputMode == PackageInputMode.Exact && hasExactDimensions -> {
            "${lengthInput}x${widthInput}x${heightInput} см, ${weightInput} кг"
        }

        packageInputMode == PackageInputMode.Approximate && selectedPackageTypeId.isNotBlank() -> {
            packageTypes.selectedPackageTypeName(selectedPackageTypeId)
        }

        else -> "Не выбран"
    }
}

private fun List<DeliveryPackageType>.selectedPackageTypeName(selectedId: String): String {
    return firstOrNull { it.id == selectedId }?.name.orEmpty()
}

private fun DeliveryPackageType.shortLabel(): String {
    return when {
        name.contains("Конверт", ignoreCase = true) -> "A4"
        name.contains("XS", ignoreCase = true) -> "XS"
        name.contains(" S", ignoreCase = true) -> "S"
        name.contains(" M", ignoreCase = true) -> "M"
        name.contains(" L", ignoreCase = true) -> "L"
        else -> name.take(2).uppercase()
    }
}

private fun presetAccentColor(index: Int): Color {
    return when (index % 5) {
        0 -> Color(0xFF8AE3A2)
        1 -> Color(0xFFD8B88A)
        2 -> Color(0xFFCEB28A)
        3 -> Color(0xFFDDBA97)
        else -> Green_500.copy(alpha = 0.7f)
    }
}
