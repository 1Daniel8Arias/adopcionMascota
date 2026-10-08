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
import androidx.navigation.toRoute
import co.edu.adopcionmascota.features.adoption.detail.PublicationDetailScreen
import co.edu.adopcionmascota.features.adoption.list.PublicationScreen
import co.edu.adopcionmascota.features.login.LoginScreen
import co.edu.adopcionmascota.features.register.RegisterScreen

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
                         navController.navigate(MainRoutes.PublicationList)
                     },
                     onNavigateToRegister = {
                         navController.navigate(MainRoutes.Register)
                     }
                 )
             }
            composable<MainRoutes.Login> {
                LoginScreen(
                    onNavigationPublicLists = {
                        navController.navigate(MainRoutes.PublicationList){
                            popUpTo(MainRoutes.Home){
                                inclusive=true
                            }
                        }
                    }

                )
            }

            composable<MainRoutes.Register>{
                RegisterScreen(
                    onNavigateToBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable<MainRoutes.PublicationList>{
                PublicationScreen(
                    OnNavigateToPublication = { publicationId ->
                        navController.navigate(
                            MainRoutes.PublicationDetail(publicationId)
                        )
                    },

                    onPublicationClick = { publication ->
                        navController.navigate(
                            MainRoutes.PublicationDetail(publication.id)
                        )
                    }

                )

            }

            composable<MainRoutes.PublicationDetail>{
                val args= it.toRoute<MainRoutes.PublicationDetail>()
                PublicationDetailScreen(
                    pubblicationId = args.publicationId
                )

            }



        }

    }
}