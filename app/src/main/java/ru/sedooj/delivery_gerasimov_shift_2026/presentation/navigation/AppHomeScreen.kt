package ru.sedooj.delivery_gerasimov_shift_2026.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.sedooj.delivery_gerasimov_shift_2026.R
import ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator.CalculatorRoute
import ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator.CalculatorViewModel
import ru.sedooj.delivery_gerasimov_shift_2026.presentation.deliverymethod.DeliveryMethodScreen
import ru.sedooj.delivery_gerasimov_shift_2026.presentation.deliverymethod.DeliveryUiState
import ru.sedooj.delivery_gerasimov_shift_2026.ui.components.NunitoText
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Background
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.BorderHard
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.DeliveryCardBackground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Foreground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Green_500
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.SurfaceCard

@Composable
fun AppHomeScreen() {
    val navController = rememberNavController()
    val calculatorViewModel: CalculatorViewModel = hiltViewModel()
    val deliveryUiState by calculatorViewModel.deliveryUiState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val bottomItems = rememberBottomNavigationItems()
    val showBottomBar = currentDestination == null ||
        bottomItems.any { currentDestination.isSelected(it.route) }

    LaunchedEffect(deliveryUiState, currentDestination) {
        if (
            deliveryUiState is DeliveryUiState.Success &&
            !currentDestination.isSelected(AppRoute.DeliveryMethod)
        ) {
            navController.navigate(AppRoute.DeliveryMethod) {
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(
                    items = bottomItems,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Calculator,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable<AppRoute.Calculator> {
                CalculatorRoute(
                    viewModel = calculatorViewModel
                )
            }
            composable<AppRoute.History> {
            }
            composable<AppRoute.Profile> {
            }
            composable<AppRoute.DeliveryMethod> {
                DeliveryMethodScreen(
                    deliveryUiState = deliveryUiState,
                    onBackClick = {
                        navController.popBackStack()
                        calculatorViewModel.resetDeliveryCalculation()
                    }
                )
            }
        }
    }
}

@Composable
private fun AppBottomNavigationBar(
    items: List<BottomNavigationItem>,
    currentDestination: NavDestination?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = AppNavigationDimens.bottomBarHorizontalPadding,
                vertical = AppNavigationDimens.bottomBarVerticalPadding
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppNavigationDimens.bottomBarHeight)
                .clip(RoundedCornerShape(AppNavigationDimens.bottomBarCornerRadius))
                .background(SurfaceCard)
                .border(
                    width = AppNavigationDimens.bottomBarBorderWidth,
                    color = BorderHard,
                    shape = RoundedCornerShape(AppNavigationDimens.bottomBarCornerRadius)
                )
                .padding(AppNavigationDimens.bottomBarInnerPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(AppNavigationDimens.bottomItemSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    AppBottomNavigationItem(
                        item = item,
                        selected = currentDestination.isSelected(item.route) ||
                            (currentDestination == null && item.route == AppRoute.Calculator),
                        modifier = Modifier.weight(AppNavigationDimens.fullWeight)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppBottomNavigationItem(
    item: BottomNavigationItem,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val itemModifier = if (selected) {
        modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(AppNavigationDimens.selectedItemCornerRadius))
            .background(Green_500)
    } else {
        modifier
            .fillMaxHeight()
    }

    Column(
        modifier = itemModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            AppNavigationDimens.bottomItemGap,
            Alignment.CenterVertically
        )
    ) {
        Icon(
            painter = painterResource(item.icon),
            contentDescription = item.label,
            tint = if (selected) SurfaceCard else Foreground,
            modifier = Modifier.size(AppNavigationDimens.bottomIconSize)
        )
        NunitoText(
            text = item.label,
            color = if (selected) SurfaceCard else Foreground,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = AppNavigationTypography.pageTitleSize,
                lineHeight = AppNavigationTypography.pageTitleLineHeight,
                letterSpacing = AppNavigationTypography.pageTitleLineLetterSpacing,
            )
        )
    }
}

@Composable
private fun rememberBottomNavigationItems(): List<BottomNavigationItem> {
    val calculatorLabel = stringResource(R.string.navigation_calculator)
    val historyLabel = stringResource(R.string.navigation_history)
    val profileLabel = stringResource(R.string.navigation_profile)

    return remember(calculatorLabel, historyLabel, profileLabel) {
        listOf(
            BottomNavigationItem(
                route = AppRoute.Calculator,
                label = calculatorLabel,
                icon = R.drawable.calculator
            ),
            BottomNavigationItem(
                route = AppRoute.History,
                label = historyLabel,
                icon = R.drawable.history
            ),
            BottomNavigationItem(
                route = AppRoute.Profile,
                label = profileLabel,
                icon = R.drawable.person
            )
        )
    }
}

private fun NavDestination?.isSelected(route: AppRoute): Boolean {
    return this?.hierarchy?.any { destination ->
        when (route) {
            AppRoute.Calculator -> destination.hasRoute<AppRoute.Calculator>()
            AppRoute.History -> destination.hasRoute<AppRoute.History>()
            AppRoute.Profile -> destination.hasRoute<AppRoute.Profile>()
            AppRoute.DeliveryMethod -> destination.hasRoute<AppRoute.DeliveryMethod>()
        }
    } == true
}

private data class BottomNavigationItem(
    val route: AppRoute,
    val label: String,
    val icon: Int
)

private object AppNavigationDimens {
    val bottomBarHeight = 72.dp
    val bottomBarHorizontalPadding = 20.dp
    val bottomBarVerticalPadding = 8.dp
    val bottomBarInnerPadding = 6.dp
    val bottomBarCornerRadius = 40.dp
    val bottomBarBorderWidth = 1.dp
    val selectedItemCornerRadius = 34.dp
    val bottomItemSpacing = 4.dp
    val bottomItemGap = 2.dp
    val bottomIconSize = 28.dp
    val bottomLabelSize = 20.sp
    val bottomLabelLineHeight = 24.sp
    const val fullWeight = 1f
}

private object AppNavigationTypography {
    val pageTitleSize = 12.sp
    val pageTitleLineHeight = 16.sp
    val pageTitleLineLetterSpacing = 0.5.sp
}
