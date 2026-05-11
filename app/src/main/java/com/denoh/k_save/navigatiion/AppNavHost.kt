package com.denoh.k_save.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.denoh.k_save.screens.AppFlowScreen
import com.denoh.k_save.screens.DashboardScreen
import com.denoh.k_save.screens.HomeScreen
import com.denoh.k_save.screens.SplashScreen
import com.denoh.k_save.screens.TripScreen
import com.denoh.k_save.ui.screens.loginscreen.LoginScreen
import com.denoh.k_save.ui.screens.registerscreen.RegisterScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    // State to track trip details for refund purposes
    var activeTripDestination by remember { mutableStateOf("") }
    var activeTripFare by remember { mutableStateOf(0) }
    var userPhone by remember { mutableStateOf("254") }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTE_SPLASH) {
            SplashScreen(onFinish = {
                navController.navigate(ROUTE_APPFLOW) {
                    popUpTo(ROUTE_SPLASH) { inclusive = true }
                }
            })
        }
        composable(ROUTE_APPFLOW) {
            AppFlowScreen(onEnterApp = {
                navController.navigate(ROUTE_LOGIN) {
                    popUpTo(ROUTE_APPFLOW) { inclusive = true }
                }
            })
        }
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(ROUTE_REGISTER)
                }
            )
        }
        composable(ROUTE_REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(ROUTE_HOME) {
            HomeScreen(
                onLocationSelected = { destination ->
                    activeTripDestination = destination
                    navController.navigate("$ROUTE_DASHBOARD?destination=$destination")
                },
                onServiceSelected = { service ->
                    navController.navigate("$ROUTE_DASHBOARD?category=$service")
                }
            )
        }
        composable(
            route = "$ROUTE_DASHBOARD?category={category}&destination={destination}",
            arguments = listOf(
                navArgument("category") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("destination") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            val destination = backStackEntry.arguments?.getString("destination") ?: ""
            DashboardScreen(
                destination = destination,
                initialCategory = category,
                goHome = { navController.navigate(ROUTE_HOME) },
                onTripConfirmed = { fare, phone ->
                    activeTripFare = fare
                    userPhone = phone
                    navController.navigate(ROUTE_TRIP)
                }
            )
        }
        composable(ROUTE_TRIP) {
            TripScreen(
                destination = activeTripDestination,
                farePaid = activeTripFare,
                phoneNumber = userPhone,
                goHome = { 
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
