package io.mrnateriver.smsproxy.relay.layout

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.mrnateriver.smsproxy.relay.AppViewModel
import io.mrnateriver.smsproxy.relay.BuildConfig
import io.mrnateriver.smsproxy.relay.pages.home.HOME_PAGE_ROUTE
import io.mrnateriver.smsproxy.relay.pages.home.homePage
import io.mrnateriver.smsproxy.relay.pages.settings.navigateToSettingsPage
import io.mrnateriver.smsproxy.relay.pages.settings.settingsPage
import io.mrnateriver.smsproxy.shared.pages.about.aboutPage
import io.mrnateriver.smsproxy.shared.pages.about.navigateToLicensesPage

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = hiltViewModel<AppViewModel>(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HOME_PAGE_ROUTE,
        exitTransition = { navExitTransitionBuilder() },
        enterTransition = { navEnterTransitionBuilder() },
        popEnterTransition = { navPopEnterTransitionBuilder() },
        popExitTransition = { navPopExitTransitionBuilder() },
    ) {
        homePage(
            viewModel = viewModel,
            onGoToSettingsClick = { navController.navigateToSettingsPage() },
        )

        settingsPage(
            onBackClick = { navController.popBackStack() },
            settingsService = viewModel.settingsService,
        )

        aboutPage(
            onNavigateToLicensesPageClick = { navController.navigateToLicensesPage() },
            versionString = "${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}",
            pageContentWrapper = { AppContentSurface { it() } },
        )
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.navExitTransitionBuilder(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(durationMillis = 300),
    ) + scaleOut(
        targetScale = 0.9f,
        transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f),
        animationSpec = tween(durationMillis = 200),
    )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.navEnterTransitionBuilder(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(durationMillis = 500),
    ) + scaleIn(
        initialScale = 0.9f,
        transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f),
        animationSpec = tween(durationMillis = 800),
    )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.navPopExitTransitionBuilder(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(durationMillis = 300),
    ) + scaleOut(
        targetScale = 0.9f,
        transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f),
        animationSpec = tween(durationMillis = 200),
    )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.navPopEnterTransitionBuilder(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(durationMillis = 500),
    ) + scaleIn(
        initialScale = 0.9f,
        transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f),
        animationSpec = tween(durationMillis = 800),
    )
