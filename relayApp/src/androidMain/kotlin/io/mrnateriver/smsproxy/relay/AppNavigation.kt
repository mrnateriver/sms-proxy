package io.mrnateriver.smsproxy.relay

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

fun navigate(route: AppPages, navController: NavController?) {
    val nav = route.navigate
    navController?.nav {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
