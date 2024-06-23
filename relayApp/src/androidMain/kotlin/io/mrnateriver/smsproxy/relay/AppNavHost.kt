package io.mrnateriver.smsproxy.relay

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.mrnateriver.smsproxy.relay.about.aboutPage
import io.mrnateriver.smsproxy.relay.home.HomePageRoute
import io.mrnateriver.smsproxy.relay.home.homePage
import io.mrnateriver.smsproxy.relay.settings.settingsPage

@Composable
fun AppNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomePageRoute,
        exitTransition = { navExitTransitionBuilder() },
        enterTransition = { navEnterTransitionBuilder() },
        popEnterTransition = { navPopEnterTransitionBuilder() },
        popExitTransition = { navPopExitTransitionBuilder() },
    ) {
        homePage()
        aboutPage(navController)
        settingsPage()
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.navExitTransitionBuilder(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(durationMillis = 300),
    ) + scaleOut(
        targetScale = 0.9f,
        transformOrigin = TransformOrigin(0.5f, 1f),
        animationSpec = tween(durationMillis = 200),
    )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.navEnterTransitionBuilder(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(durationMillis = 500),
    ) + scaleIn(
        initialScale = 0.9f,
        transformOrigin = TransformOrigin(0.5f, 1f),
        animationSpec = tween(durationMillis = 800),
    )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.navPopExitTransitionBuilder(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(durationMillis = 300),
    ) + scaleOut(
        targetScale = 0.9f,
        transformOrigin = TransformOrigin(0.5f, 1f),
        animationSpec = tween(durationMillis = 200),
    )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.navPopEnterTransitionBuilder(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(durationMillis = 500),
    ) + scaleIn(
        initialScale = 0.9f,
        transformOrigin = TransformOrigin(0.5f, 1f),
        animationSpec = tween(durationMillis = 800),
    )
