package uk.ac.tees.mad.iplocator.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import uk.ac.tees.mad.iplocator.ui.screens.HomeScreen
import uk.ac.tees.mad.iplocator.ui.screens.LoginScreen
import uk.ac.tees.mad.iplocator.ui.screens.MapScreen
import uk.ac.tees.mad.iplocator.ui.screens.ProfileScreen
import uk.ac.tees.mad.iplocator.ui.screens.SearchScreen
import uk.ac.tees.mad.iplocator.ui.screens.SignUpScreen
import uk.ac.tees.mad.iplocator.ui.screens.SplashScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController, startDestination = Dest.SplashScreen
    ) {
        composable<Dest.SplashScreen> {
            SplashScreen(navController = navController)
        }
        navigation<SubGraph.AuthGraph>(startDestination = Dest.LoginScreen) {
            composable<Dest.LoginScreen> {
                LoginScreen(navController = navController)
            }
            composable<Dest.SignUpScreen> {
                SignUpScreen(navController = navController)
            }
        }
        navigation<SubGraph.HomeGraph>(startDestination = Dest.HomeScreen) {
            composable<Dest.HomeScreen> {
                HomeScreen(navController = navController)
            }
            composable<Dest.SearchScreen> {
                SearchScreen(navController = navController)
            }
            composable<Dest.MapScreen> {
                val args = it.toRoute<Dest.MapScreen>()
                MapScreen(
                    navController = navController,
                    latitude = args.latitude,
                    longitude = args.longitude,
                    ip = args.ip
                )
            }
            composable<Dest.ProfileScreen> {
                ProfileScreen(navController = navController)
            }
        }
    }
}