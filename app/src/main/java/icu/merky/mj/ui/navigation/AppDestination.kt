package icu.merky.mj.ui.navigation

sealed class AppDestination(val route: String) {
    data object Chat : AppDestination("chat")
    data object Home : AppDestination("home")
    data object Relationship : AppDestination("relationship")
    data object Settings : AppDestination("settings")
    data object Diary : AppDestination("diary")
}
