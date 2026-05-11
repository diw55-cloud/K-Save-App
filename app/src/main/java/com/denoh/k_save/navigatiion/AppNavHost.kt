package com.denoh.k_save.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.denoh.k_save.network.SessionManager
import com.denoh.k_save.screens.AppFlowScreen
import com.denoh.k_save.screens.DashboardScreen
import com.denoh.k_save.screens.DiagnosticScreen
import com.denoh.k_save.screens.HomeScreen
import com.denoh.k_save.screens.SavingsScreen
import com.denoh.k_save.screens.SettingsScreen
import com.denoh.k_save.screens.SplashScreen
import com.denoh.k_save.screens.TripScreen
import com.denoh.k_save.ui.screens.loginscreen.LoginScreen
import com.denoh.k_save.ui.screens.registerscreen.RegisterScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    // Determine start destination based on login state
    val startDest = if (sessionManager.isLoggedIn()) ROUTE_HOME else ROUTE_SPLASH

    var activeTripDestination by remember { mutableStateOf("") }
    var activeTripFare by remember { mutableStateOf(0) }
    var userPhone by remember { mutableStateOf("254") }

    NavHost(
        navController = navController,
        startDestination = startDest
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
                },
                onSettingsClick = {
                    navController.navigate(ROUTE_SETTINGS)
                },
                onDiagnosticClick = {
                    navController.navigate(ROUTE_DIAGNOSTIC)
                }
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(ROUTE_DIAGNOSTIC) {
            DiagnosticScreen(onBack = { navController.popBackStack() })
        }
        composable(ROUTE_SAVINGS) {
            SavingsScreen(onBack = { navController.popBackStack() })
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
                goHome = { 
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_HOME) { inclusive = true }
                    }
                },
                goSavings = {
                    navController.navigate(ROUTE_SAVINGS)
                },
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
