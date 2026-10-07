package co.edu.adopcionmascota.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Surface
import androidx.navigation.NavHost
import co.edu.adopcionmascota.features.home.HomeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.edu.adopcionmascota.features.login.LoginScreen

@Composable
fun AppNavigation(){
    val navController= rememberNavController()

    Surface(
        modifier=Modifier.fillMaxSize()
    ){
        NavHost(
            navController=navController,
            startDestination=MainRoutes.Home
        ){
             composable<MainRoutes.Home> {
                 HomeScreen(
                     onNavigateToLogin = {
                         navController.navigate(MainRoutes.Login)
                     }
                 )
             }
            composable<MainRoutes.Login> {
                LoginScreen(
                )
            }



        }

    }
}