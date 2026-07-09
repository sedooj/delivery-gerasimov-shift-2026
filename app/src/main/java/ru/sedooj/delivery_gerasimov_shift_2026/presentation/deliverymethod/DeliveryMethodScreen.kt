package ru.sedooj.delivery_gerasimov_shift_2026.presentation.deliverymethod

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.sedooj.delivery_gerasimov_shift_2026.R
import ru.sedooj.delivery_gerasimov_shift_2026.ui.components.NunitoText
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Background
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.BorderHard
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.DeliveryCardBackground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Foreground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.InkSoft
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.PrimaryForeground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.SurfaceCard
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.SurfaceMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryMethodScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.delivery_method_back),
                            tint = Foreground
                        )
                    }
                },
                title = {
                    NunitoText(
                        text = stringResource(R.string.delivery_method_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = Foreground,
                    navigationIconContentColor = Foreground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(horizontal = DeliveryMethodDimens.screenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.sectionGap)
        ) {
            DeliveryStepIndicator(modifier = Modifier.fillMaxWidth())
            DeliveryMethodList(modifier = Modifier.fillMaxWidth())
            DeliveryPromoBanner(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun DeliveryStepIndicator(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = DeliveryMethodDimens.stepTopPadding),
        verticalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.stepGap)
    ) {
        NunitoText(
            text = stringResource(R.string.delivery_method_step),
            color = Foreground,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        LinearProgressIndicator(
            progress = { DeliveryMethodNumbers.stepProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(DeliveryMethodDimens.progressHeight)
                .clip(RoundedCornerShape(DeliveryMethodDimens.progressCornerRadius)),
            color = DeliveryCardBackground,
            trackColor = SurfaceMuted,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun DeliveryMethodList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.cardGap)
    ) {
        DeliveryMethodCard(
            title = stringResource(R.string.delivery_method_express_title),
            price = stringResource(R.string.delivery_method_express_price),
            subtitle = stringResource(R.string.delivery_method_express_subtitle),
            icon = Icons.Rounded.Flight,
            onClick = {}
        )
        DeliveryMethodCard(
            title = stringResource(R.string.delivery_method_regular_title),
            price = stringResource(R.string.delivery_method_regular_price),
            subtitle = stringResource(R.string.delivery_method_regular_subtitle),
            icon = Icons.Rounded.Train,
            onClick = {}
        )
    }
}

@Composable
private fun DeliveryMethodCard(
    title: String,
    price: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(DeliveryMethodDimens.cardCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .height(DeliveryMethodDimens.methodCardHeight)
            .border(
                width = DeliveryMethodDimens.cardBorderWidth,
                color = BorderHard,
                shape = RoundedCornerShape(DeliveryMethodDimens.cardCornerRadius)
            )
            .clip(RoundedCornerShape(DeliveryMethodDimens.cardCornerRadius))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = DeliveryMethodDimens.cardHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(DeliveryMethodDimens.methodIconContainerSize)
                    .clip(CircleShape)
                    .background(SurfaceMuted),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Foreground,
                    modifier = Modifier.size(DeliveryMethodDimens.methodIconSize)
                )
            }
            Spacer(modifier = Modifier.width(DeliveryMethodDimens.cardContentGap))
            Column(
                modifier = Modifier.weight(DeliveryMethodNumbers.fullWeight),
                verticalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.cardTextGap)
            ) {
                NunitoText(
                    text = title,
                    color = Foreground,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Black
                    ),
                    maxLines = DeliveryMethodNumbers.titleMaxLines
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.priceGap)
                ) {
                    NunitoText(
                        text = price,
                        color = Foreground,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Black
                        )
                    )
                    NunitoText(
                        text = subtitle,
                        color = InkSoft,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = Foreground,
                modifier = Modifier.size(DeliveryMethodDimens.arrowIconSize)
            )
        }
    }
}

@Composable
private fun DeliveryPromoBanner(
    modifier: Modifier = Modifier
) {
    Surface(
        color = DeliveryCardBackground,
        shape = RoundedCornerShape(DeliveryMethodDimens.bannerCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .height(DeliveryMethodDimens.bannerHeight)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = DeliveryMethodDimens.bannerHorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.bannerTextGap)
            ) {
                NunitoText(
                    text = stringResource(R.string.delivery_method_promo_title),
                    color = PrimaryForeground,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black
                    )
                )
                NunitoText(
                    text = stringResource(R.string.delivery_method_promo_subtitle),
                    color = PrimaryForeground,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Image(
                painter = painterResource(R.drawable.box_l),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(
                        x = DeliveryMethodDimens.bannerBoxLargeOffsetX,
                        y = DeliveryMethodDimens.bannerBoxLargeOffsetY
                    )
                    .size(DeliveryMethodDimens.bannerBoxLargeSize)
            )
            Image(
                painter = painterResource(R.drawable.box_m),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = DeliveryMethodDimens.bannerBoxMediumOffsetX,
                        y = DeliveryMethodDimens.bannerBoxMediumOffsetY
                    )
                    .size(DeliveryMethodDimens.bannerBoxMediumSize)
            )
        }
    }
}

private object DeliveryMethodNumbers {
    const val stepProgress = 1f / 7f
    const val fullWeight = 1f
    const val titleMaxLines = 2
}

private object DeliveryMethodDimens {
    val screenHorizontalPadding = 16.dp
    val sectionGap = 16.dp
    val stepTopPadding = 8.dp
    val stepGap = 8.dp
    val progressHeight = 4.dp
    val progressCornerRadius = 999.dp
    val cardGap = 8.dp
    val cardCornerRadius = 24.dp
    val cardBorderWidth = 1.dp
    val methodCardHeight = 96.dp
    val cardHorizontalPadding = 16.dp
    val cardContentGap = 14.dp
    val cardTextGap = 4.dp
    val methodIconContainerSize = 48.dp
    val methodIconSize = 26.dp
    val arrowIconSize = 28.dp
    val priceGap = 8.dp
    val bannerCornerRadius = 24.dp
    val bannerHeight = 112.dp
    val bannerHorizontalPadding = 18.dp
    val bannerTextGap = 2.dp
    val bannerBoxLargeSize = 90.dp
    val bannerBoxMediumSize = 60.dp
    val bannerBoxLargeOffsetX = 10.dp
    val bannerBoxLargeOffsetY = 12.dp
    val bannerBoxMediumOffsetX = (-54).dp
    val bannerBoxMediumOffsetY = 10.dp
}
