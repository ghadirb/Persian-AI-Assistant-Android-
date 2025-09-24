package com.example.persianaiapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.persianaiapp.ui.chat.ChatScreen
import com.example.persianaiapp.ui.enhanced.EnhancedChatScreen
import com.example.persianaiapp.ui.enhanced.EnhancedSettingsScreen
import com.example.persianaiapp.ui.enhanced.EnhancedWelcomeScreen
import com.example.persianaiapp.ui.offline.OfflineModeScreen
import com.example.persianaiapp.ui.online.OnlineModeScreen
import com.example.persianaiapp.ui.settings.SettingsScreen
import com.example.persianaiapp.ui.startup.KeySetupScreen
import com.example.persianaiapp.ui.welcome.WelcomeScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "welcome"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToOnlineMode = {
                    navController.navigate("online") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToOfflineMode = {
                    navController.navigate("offline") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("chat") {
            EnhancedChatScreen(
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToOffline = {
                    navController.navigate("offline")
                },
                onNavigateToOnline = {
                    navController.navigate("online")
                }
            )
        }
        
        composable("offline") {
            OfflineModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("online") {
            OnlineModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("settings") {
            EnhancedSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("key_setup") {
            KeySetupScreen(
                onNavigateToWelcome = {
                    navController.navigate("welcome") {
                        popUpTo("key_setup") { inclusive = true }
                    }
                }
            )
        }
    }
}