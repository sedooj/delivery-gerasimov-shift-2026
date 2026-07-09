package ru.sedooj.delivery_gerasimov_shift_2026.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = SurfaceCard,
    secondary = Green_500,
    onSecondary = Ink,
    tertiary = AccentGreenSoft,
    background = Canvas,
    onBackground = Ink,
    surface = SurfaceCard,
    onSurface = Ink,
    surfaceVariant = Muted,
    outline = DividerWarm
)

@Composable
fun Deliverygerasimovshift2026Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
