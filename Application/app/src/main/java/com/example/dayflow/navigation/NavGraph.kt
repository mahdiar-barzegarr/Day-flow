package com.example.dayflow.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dayflow.ui.home.HomeScreen
import com.example.dayflow.ui.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"

    const val HOME = "home"

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavController = rememberNavController()
) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Routes.SPLASH
    ){
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
                onCreateGroup = { /*TODO*/ },
                onNotificationsClick = { /*TODO*/ }
            )
        }
    }
}
