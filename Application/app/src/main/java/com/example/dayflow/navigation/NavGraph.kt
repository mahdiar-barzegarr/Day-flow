package com.example.dayflow.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dayflow.ui.challenges.ChallengeDetailScreen
import com.example.dayflow.ui.challenges.ChallengesScreen
import com.example.dayflow.ui.home.HomeScreen
import com.example.dayflow.ui.login.LoginScreen
import com.example.dayflow.ui.note.NoteEditorScreen
import com.example.dayflow.ui.note.NotesScreen
import com.example.dayflow.ui.notification.NotificationSettingsScreen
import com.example.dayflow.ui.profile.AboutScreen
import com.example.dayflow.ui.profile.ProfileScreen
import com.example.dayflow.ui.profile.ProfileViewModel
import com.example.dayflow.ui.register.RegisterScreen
import com.example.dayflow.ui.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"

    const val HOME = "home"

    const val CHALLENGE = "challenges"

    const val PROFILE = "profile"

    const val LOGIN = "login"

    const val REGISTER = "register"
    const val GROUP = "groups"

    const val FRIENDS = "friends"

    const val NOTIFICATIONS = "notifications"

    const val NOTE = "note"

    const val ABOUT = "about"

}

fun NavController.navigateToTab(route: String) {
    if (currentDestination?.route == route) return

    navigate(route) {
        popUpTo(Routes.HOME) {
            inclusive = false
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavController = rememberNavController()
) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                onCreateChallenge = { /*TODO*/ },
                onCreateNote = {
                    navController.navigate(Routes.NOTE)
                },
                onNotificationsClick = { /*TODO*/ }
            )
        }

        composable(Routes.CHALLENGE) {
            ChallengesScreen(
                navController = navController,
                onChallengeClick = { id -> navController.navigate("challenge/$id") }
            )
        }
        composable(
            route = "challenge/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            ChallengeDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            val vm: ProfileViewModel = hiltViewModel()
            val state by vm.uiState.collectAsState()

            ProfileScreen(
                navController = navController,
                state = state,
                onSeeAllChallenges = { navController.navigate("challenges") },
                onChallengeClick = { id -> navController.navigate("challenge/$id") },
                onEditProfile = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
                navController = navController
            )
        }


        composable(Routes.NOTIFICATIONS) {
            NotificationSettingsScreen(navController)
        }

        composable(Routes.NOTE) { NotesScreen(navController) }

        composable(
            route = "note_editor?noteId={noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            NoteEditorScreen(onBack = { navController.popBackStack() })
        }


        composable(Routes.ABOUT) { AboutScreen(navController) }
    }
}
