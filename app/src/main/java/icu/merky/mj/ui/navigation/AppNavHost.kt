package icu.merky.mj.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import icu.merky.mj.feature.chat.ChatRoute
import icu.merky.mj.feature.home.HomeRoute
import icu.merky.mj.feature.relationship.RelationshipRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Chat.route,
        modifier = modifier
    ) {
        composable(route = AppDestination.Chat.route) {
            ChatRoute()
        }
        composable(route = AppDestination.Home.route) {
            HomeRoute()
        }
        composable(route = AppDestination.Relationship.route) {
            RelationshipRoute()
        }
    }
}
