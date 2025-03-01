package uk.ac.tees.mad.iplocator.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.ac.tees.mad.iplocator.ui.screens.AuthScreen
import uk.ac.tees.mad.iplocator.ui.screens.HomeScreen
import uk.ac.tees.mad.iplocator.ui.screens.MapScreen
import uk.ac.tees.mad.iplocator.ui.screens.ProfileScreen
import uk.ac.tees.mad.iplocator.ui.screens.SearchScreen
import uk.ac.tees.mad.iplocator.ui.screens.SplashScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController,
        startDestination = SplashScreen){
        composable<SplashScreen>{
            SplashScreen(navController = navController)
        }
        composable<AuthScreen>{
            AuthScreen(navController = navController)
        }
        composable<HomeScreen> {
            HomeScreen(navController = navController)
        }
        composable<SearchScreen> {
            SearchScreen(navController = navController)
        }
        composable<MapScreen> {
            MapScreen(navController = navController)
        }
        composable<ProfileScreen> {
            ProfileScreen(navController = navController)
        }
    }
}