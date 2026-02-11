package icu.merky.mj.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import icu.merky.mj.feature.chat.ChatRoute
import icu.merky.mj.feature.diary.DiaryRoute
import icu.merky.mj.feature.home.HomeRoute
import icu.merky.mj.feature.relationship.RelationshipRoute
import icu.merky.mj.feature.settings.SettingsRoute

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
            ChatRoute(
                onOpenSettings = {
                    navController.navigate(AppDestination.Settings.route)
                },
                onOpenDiary = {
                    navController.navigate(AppDestination.Diary.route)
                }
            )
        }
        composable(route = AppDestination.Home.route) {
            HomeRoute(
                onOpenDiary = {
                    navController.navigate(AppDestination.Diary.route)
                }
            )
        }
        composable(route = AppDestination.Relationship.route) {
            RelationshipRoute()
        }
        composable(route = AppDestination.Settings.route) {
            SettingsRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = AppDestination.Diary.route) {
            DiaryRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
