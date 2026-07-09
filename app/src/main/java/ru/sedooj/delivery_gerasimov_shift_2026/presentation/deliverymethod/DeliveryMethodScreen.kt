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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sedooj.delivery_gerasimov_shift_2026.R
import ru.sedooj.delivery_gerasimov_shift_2026.domain.model.DeliveryOption
import ru.sedooj.delivery_gerasimov_shift_2026.ui.components.NunitoText
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Background
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.BorderHard
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.DeliveryCardBackground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Foreground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Green_500
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Muted
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.PrimaryForeground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Secondary
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Surface
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.SurfaceCard
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryMethodScreen(
    deliveryUiState: DeliveryUiState,
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
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.delivery_method_back),
                            tint = Foreground
                        )
                    }
                },
                title = {
                    NunitoText(
                        text = stringResource(R.string.delivery_method_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = DeliveryMethodTypography.screenTitleSize,
                            letterSpacing = DeliveryMethodTypography.screenTitleLetterSpacing,
                            lineHeight = DeliveryMethodTypography.screenTitleLineHeight,
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
                .padding(horizontal = DeliveryMethodDimens.screenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.sectionGap)
        ) {
            DeliveryStepIndicator(modifier = Modifier.fillMaxWidth())
            when (deliveryUiState) {
                DeliveryUiState.Idle -> {
                    DeliveryMethodMessage(
                        text = stringResource(R.string.delivery_method_idle),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                DeliveryUiState.Loading -> {
                    DeliveryMethodLoading(modifier = Modifier.fillMaxWidth())
                }

                is DeliveryUiState.Error -> {
                    DeliveryMethodMessage(
                        text = deliveryUiState.message,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is DeliveryUiState.Success -> {
                    if (deliveryUiState.theOptions.isEmpty()) {
                        DeliveryMethodMessage(
                            text = stringResource(R.string.delivery_method_empty_options),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        DeliveryMethodList(
                            options = deliveryUiState.theOptions,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DeliveryPromoBanner(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
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
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Foreground,
                fontSize = DeliveryMethodTypography.deliveryStepIndicatorTitleSize,
                lineHeight = DeliveryMethodTypography.deliveryStepIndicatorTitleLineHeight,
                letterSpacing = DeliveryMethodTypography.deliveryStepIndicatorTitleLetterSpacing,
            )
        )
        LinearProgressIndicator(
            progress = { DeliveryMethodNumbers.stepProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(DeliveryMethodDimens.progressHeight)
                .clip(RoundedCornerShape(DeliveryMethodDimens.progressCornerRadius)),
            color = Green_500,
            trackColor = Muted,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun DeliveryMethodList(
    options: List<DeliveryOption>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.cardGap)
    ) {
        options.forEach { option ->
            DeliveryMethodCard(
                title = option.name,
                price = stringResource(R.string.delivery_method_price_format, option.price),
                subtitle = option.days.toDeliveryDaysText(),
                icon = option.icon,
                onClick = {}
            )
        }
    }
}

@Composable
private fun DeliveryMethodLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(DeliveryMethodDimens.stateContainerHeight),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Foreground,
            strokeWidth = DeliveryMethodDimens.loaderStrokeWidth
        )
    }
}

@Composable
private fun DeliveryMethodMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(DeliveryMethodDimens.stateContainerHeight)
            .clip(RoundedCornerShape(DeliveryMethodDimens.cardCornerRadius))
            .background(SurfaceCard)
            .border(
                width = DeliveryMethodDimens.cardBorderWidth,
                color = BorderHard,
                shape = RoundedCornerShape(DeliveryMethodDimens.cardCornerRadius)
            )
            .padding(DeliveryMethodDimens.stateMessagePadding),
        contentAlignment = Alignment.Center
    ) {
        NunitoText(
            text = text,
            color = Foreground,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun DeliveryMethodCard(
    title: String,
    price: String,
    subtitle: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
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
            .padding(DeliveryMethodDimens.cardPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.cardContentGap)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DeliveryMethodDimens.cardContentGap)
            ) {
                Box(
                    modifier = Modifier
                        .size(DeliveryMethodDimens.methodIconBgSize)
                        .background(
                            color = Secondary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = Foreground,
                        modifier = Modifier.size(DeliveryMethodDimens.methodIconSize)
                    )
                }
                Column(
                    modifier = Modifier.weight(DeliveryMethodNumbers.fullWeight),
                    verticalArrangement = Arrangement.Center
                ) {
                    NunitoText(
                        text = title,
                        color = Foreground,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = Surface,
                            lineHeight = DeliveryMethodTypography.deliveryMethodCardTitleLineHeight,
                            letterSpacing = DeliveryMethodTypography.deliveryMethodCardTitleLetterSpacing,
                            fontSize = DeliveryMethodTypography.deliveryMethodCardTitleSize
                        ),
                        maxLines = DeliveryMethodNumbers.titleMaxLines
                    )
                    NunitoText(
                        text = price,
                        color = Foreground,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = Foreground,
                            lineHeight = DeliveryMethodTypography.deliveryMethodCardPriceLineHeight,
                            letterSpacing = DeliveryMethodTypography.deliveryMethodCardPriceLetterSpacing,
                            fontSize = DeliveryMethodTypography.deliveryMethodCardPriceSize
                        )
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.chevron_right),
                    contentDescription = null,
                )
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(DeliveryMethodDimens.methodIconBgSize + DeliveryMethodDimens.cardContentGap))
                NunitoText(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Surface,
                        lineHeight = DeliveryMethodTypography.deliveryMethodCardSubtitleLineHeight,
                        letterSpacing = DeliveryMethodTypography.deliveryMethodCardSubtitleLetterSpacing,
                        fontSize = DeliveryMethodTypography.deliveryMethodCardSubtitleSize
                    )
                )
            }
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
                        fontWeight = FontWeight.Bold,
                        lineHeight = DeliveryMethodTypography.promoBannerTitleSizeLineHeight,
                        fontSize = DeliveryMethodTypography.promoBannerTitleSize,
                        letterSpacing = DeliveryMethodTypography.promoBannerTitleSizeLetterSpacing,
                        color = PrimaryForeground
                    )
                )
                NunitoText(
                    text = stringResource(R.string.delivery_method_promo_subtitle),
                    color = PrimaryForeground,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = DeliveryMethodTypography.promoBannerSubtitleSizeLineHeight,
                        fontSize = DeliveryMethodTypography.promoBannerSubtitleSize,
                        letterSpacing = DeliveryMethodTypography.promoBannerSubtitleSizeLetterSpacing,
                        color = White
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

private val DeliveryOption.icon: Int
    get() {
        return if (type.contains("express", ignoreCase = true) ||
            type.contains("air", ignoreCase = true)
        ) {
            R.drawable.plane
        } else {
            R.drawable.truck
        }
    }

private fun Int.toDeliveryDaysText(): String {
    val mod100 = this % 100
    val mod10 = this % 10
    val suffix = when {
        mod100 in 11..14 -> "рабочих дней"
        mod10 == 1 -> "рабочий день"
        mod10 in 2..4 -> "рабочих дня"
        else -> "рабочих дней"
    }
    return "$this $suffix"
}

private object DeliveryMethodDimens {
    val screenHorizontalPadding = 16.dp
    val sectionGap = 8.dp
    val stepTopPadding = 8.dp
    val stepGap = 8.dp
    val progressHeight = 4.dp
    val progressCornerRadius = 999.dp
    val loaderStrokeWidth = 2.dp
    val cardGap = 8.dp
    val cardCornerRadius = 24.dp
    val cardBorderWidth = 1.dp
    val methodCardHeight = 116.dp
    val cardPadding = 16.dp
    val cardContentGap = 8.dp
    val cardTextGap = 8.dp
    val methodIconContainerSize = 48.dp
    val methodIconBgSize = 48.dp
    val methodIconSize = 24.dp
    val arrowIconSize = 28.dp
    val priceGap = 8.dp
    val stateContainerHeight = 112.dp
    val stateMessagePadding = 20.dp
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

private object DeliveryMethodTypography {
    val screenTitleSize = 24.sp
    val screenTitleLetterSpacing = 0.5.sp
    val screenTitleLineHeight = 32.sp

    val deliveryStepIndicatorTitleSize = 14.sp
    val deliveryStepIndicatorTitleLineHeight = 22.sp
    val deliveryStepIndicatorTitleLetterSpacing = 0.5.sp

    val deliveryMethodCardTitleSize = 14.sp
    val deliveryMethodCardTitleLineHeight = 22.sp
    val deliveryMethodCardTitleLetterSpacing = 0.5.sp

    val deliveryMethodCardPriceSize = 24.sp
    val deliveryMethodCardPriceLineHeight = 32.sp
    val deliveryMethodCardPriceLetterSpacing = 0.5.sp

    val deliveryMethodCardSubtitleSize = 14.sp
    val deliveryMethodCardSubtitleLineHeight = 22.sp
    val deliveryMethodCardSubtitleLetterSpacing = 0.5.sp

    val promoBannerTitleSize = 24.sp
    val promoBannerTitleSizeLetterSpacing = 0.5.sp
    val promoBannerTitleSizeLineHeight = 32.sp


    val promoBannerSubtitleSize = 14.sp
    val promoBannerSubtitleSizeLetterSpacing = 0.5.sp
    val promoBannerSubtitleSizeLineHeight = 22.sp
}