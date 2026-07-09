package ru.sedooj.delivery_gerasimov_shift_2026.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.sedooj.delivery_gerasimov_shift_2026.R
import ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator.CalculatorRoute
import ru.sedooj.delivery_gerasimov_shift_2026.presentation.deliverymethod.DeliveryMethodScreen
import ru.sedooj.delivery_gerasimov_shift_2026.ui.components.NunitoText
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Background
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.DeliveryCardBackground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Foreground
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.InkSoft
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.SurfaceCard

@Composable
fun AppHomeScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val bottomItems = rememberBottomNavigationItems()
    val showBottomBar = currentDestination == null ||
        bottomItems.any { currentDestination.isSelected(it.route) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(
                    items = bottomItems,
                    currentDestination = currentDestination,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
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
                    onCalculateClick = {
                        navController.navigate(AppRoute.DeliveryMethod)
                    }
                )
            }
            composable<AppRoute.History> {
                MainTabPlaceholderScreen(title = "История")
            }
            composable<AppRoute.Profile> {
                MainTabPlaceholderScreen(title = "Профиль")
            }
            composable<AppRoute.DeliveryMethod> {
                DeliveryMethodScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun AppBottomNavigationBar(
    items: List<BottomNavigationItem>,
    currentDestination: NavDestination?,
    onItemClick: (AppRoute) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppNavigationDimens.bottomBarHeight),
        containerColor = SurfaceCard,
        tonalElevation = AppNavigationDimens.bottomBarElevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppNavigationDimens.bottomBarPadding),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                AppBottomNavigationItem(
                    item = item,
                    selected = currentDestination.isSelected(item.route),
                    onClick = { onItemClick(item.route) },
                    modifier = Modifier.weight(AppNavigationDimens.fullWeight)
                )
            }
        }
    }
}

@Composable
private fun AppBottomNavigationItem(
    item: BottomNavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(AppNavigationDimens.bottomItemHeight)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            AppNavigationDimens.bottomItemGap,
            Alignment.CenterVertically
        )
    ) {
        Box(
            modifier = Modifier
                .size(AppNavigationDimens.bottomIconContainerSize)
                .clip(CircleShape)
                .background(if (selected) DeliveryCardBackground else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (selected) Foreground else InkSoft,
                modifier = Modifier.size(AppNavigationDimens.bottomIconSize)
            )
        }
        NunitoText(
            text = item.label,
            color = if (selected) Foreground else InkSoft,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@Composable
private fun MainTabPlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = AppNavigationDimens.placeholderHorizontalPadding),
            contentAlignment = Alignment.TopStart
        ) {
            NunitoText(
                text = title,
                modifier = Modifier.padding(top = AppNavigationDimens.placeholderTopPadding),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black
                )
            )
        }
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
                icon = Icons.Rounded.Calculate
            ),
            BottomNavigationItem(
                route = AppRoute.History,
                label = historyLabel,
                icon = Icons.Rounded.History
            ),
            BottomNavigationItem(
                route = AppRoute.Profile,
                label = profileLabel,
                icon = Icons.Rounded.Person
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
    val icon: ImageVector
)

private object AppNavigationDimens {
    val bottomBarHeight = 88.dp
    val bottomBarPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
    val bottomBarElevation = 0.dp
    val bottomItemHeight = 68.dp
    val bottomItemGap = 2.dp
    val bottomIconContainerSize = 40.dp
    val bottomIconSize = 22.dp
    val placeholderHorizontalPadding = 24.dp
    val placeholderTopPadding = 32.dp
    const val fullWeight = 1f
}
