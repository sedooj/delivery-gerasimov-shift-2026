package ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPackageType
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryPoint
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.AccentGreen
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.AccentGreenSoft
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Canvas
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Deliverygerasimovshift2026Theme
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Ink
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.InkSoft
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.SurfaceCard

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

@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (CalculatorIntent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Canvas,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF9F8F2),
                            Canvas,
                            Color(0xFFE8F3DB)
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Ink
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 16.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item { HeaderBar() }
                    item { ScreenHeading() }
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

@Composable
private fun HeaderBar() {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(26.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, Ink, RoundedCornerShape(26.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(AccentGreen, AccentGreenSoft)))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "DELIVERY",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = null,
                    tint = Ink
                )
            }
            Surface(
                color = Ink,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Выйти",
                        color = SurfaceCard,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = null,
                        tint = SurfaceCard,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenHeading() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Рассчитать доставку",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Подберите маршрут, тип посылки и получите стоимость в один тап.",
            style = MaterialTheme.typography.bodyLarge,
            color = InkSoft
        )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CalculatorCard(
    state: CalculatorUiState,
    onIntent: (CalculatorIntent) -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, Ink, RoundedCornerShape(30.dp))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Рассчитать доставку",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            DeliveryDropdownField(
                title = "Город отправки",
                selectedValue = state.deliveryPoints.selectedPointName(state.selectedSenderPointId),
                options = state.deliveryPoints,
                onOptionPicked = { onIntent(CalculatorIntent.SenderPointChanged(it)) }
            )

            DeliveryDropdownField(
                title = "Город назначения",
                selectedValue = state.deliveryPoints.selectedPointName(state.selectedReceiverPointId),
                options = state.deliveryPoints,
                onOptionPicked = { onIntent(CalculatorIntent.ReceiverPointChanged(it)) }
            )

            PackageTypeDropdownField(
                title = "Размер посылки",
                selectedValue = state.packageTypes.selectedPackageTypeName(state.selectedPackageTypeId),
                options = state.packageTypes,
                onOptionPicked = { onIntent(CalculatorIntent.PackageTypeChanged(it)) }
            )

            Text(
                text = "Параметры",
                style = MaterialTheme.typography.labelLarge,
                color = InkSoft
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = !state.exactDimensionsEnabled,
                    onClick = { onIntent(CalculatorIntent.ExactDimensionsToggled(false)) },
                    label = { Text("Примерные") }
                )
                FilterChip(
                    selected = state.exactDimensionsEnabled,
                    onClick = { onIntent(CalculatorIntent.ExactDimensionsToggled(true)) },
                    label = { Text("Точные") }
                )
            }

            AnimatedVisibility(visible = state.exactDimensionsEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    NumberField(
                        label = "Длина, см",
                        value = state.lengthInput,
                        onValueChanged = { onIntent(CalculatorIntent.LengthChanged(it)) }
                    )
                    NumberField(
                        label = "Ширина, см",
                        value = state.widthInput,
                        onValueChanged = { onIntent(CalculatorIntent.WidthChanged(it)) }
                    )
                    NumberField(
                        label = "Высота, см",
                        value = state.heightInput,
                        onValueChanged = { onIntent(CalculatorIntent.HeightChanged(it)) }
                    )
                }
            }

            NumberField(
                label = "Вес, кг",
                value = state.weightInput,
                onValueChanged = { onIntent(CalculatorIntent.WeightChanged(it)) }
            )

            Surface(
                color = Ink,
                shape = RoundedCornerShape(22.dp),
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
                                color = SurfaceCard,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = SurfaceCard
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
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Ink
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "для каждого третьего заказа после регистрации",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSoft
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Выберите города, укажите формат посылки и сразу получите стоимость и срок.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = quote.deliveryTypeLabel,
                style = MaterialTheme.typography.bodyLarge,
                color = InkSoft
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
            style = MaterialTheme.typography.bodyLarge,
            color = InkSoft,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = if (highlighted) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = if (highlighted) FontWeight.Black else FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryDropdownField(
    title: String,
    selectedValue: String,
    options: List<DeliveryPoint>,
    onOptionPicked: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelLarge, color = InkSoft)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Ink,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = Ink,
                    unfocusedTextColor = Ink
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { point ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("${point.name}, ${point.region}") },
                        onClick = {
                            onOptionPicked(point.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PackageTypeDropdownField(
    title: String,
    selectedValue: String,
    options: List<DeliveryPackageType>,
    onOptionPicked: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelLarge, color = InkSoft)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Ink,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = Ink,
                    unfocusedTextColor = Ink
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { packageType ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = {
                            Column {
                                Text(packageType.name)
                                Text(
                                    text = packageType.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkSoft
                                )
                            }
                        },
                        onClick = {
                            onOptionPicked(packageType.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberField(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = InkSoft)
        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (input.all { it.isDigit() || it == '.' || it == ',' }) {
                    onValueChanged(input.replace(',', '.'))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard,
                focusedIndicatorColor = Ink,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = Ink,
                unfocusedTextColor = Ink
            )
        )
    }
}

private fun List<DeliveryPoint>.selectedPointName(selectedId: String): String {
    return firstOrNull { it.id == selectedId }?.name.orEmpty()
}

private fun List<DeliveryPackageType>.selectedPackageTypeName(selectedId: String): String {
    return firstOrNull { it.id == selectedId }?.name.orEmpty()
}

@Preview(showBackground = true)
@Composable
private fun CalculatorScreenPreview() {
    Deliverygerasimovshift2026Theme {
        CalculatorScreen(
            state = CalculatorUiState(
                isLoading = false,
                deliveryPoints = samplePoints,
                packageTypes = samplePackageTypes,
                selectedSenderPointId = "msk",
                selectedReceiverPointId = "spb",
                selectedPackageTypeId = "envelope",
                quote = CalculatorQuoteUi(
                    amountText = "780 ₽",
                    etaText = "2 дн.",
                    routeLabel = "Москва - Санкт-Петербург",
                    deliveryTypeLabel = "Экспресс-доставка"
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onIntent = {}
        )
    }
}

private val samplePoints = listOf(
    DeliveryPoint("msk", "Москва", "Москва"),
    DeliveryPoint("spb", "Санкт-Петербург", "Ленинградская область")
)

private val samplePackageTypes = listOf(
    DeliveryPackageType("envelope", "Конверт", "Документы", 1, 290)
)
