package uk.ac.tees.mad.iplocator.ui.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.iplocator.navigation.AuthScreen
import uk.ac.tees.mad.iplocator.ui.utils.LoadingScreen

@Composable
fun SplashScreen(navController: NavHostController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LaunchedEffect(key1 = true) {
            delay(3000)
            navController.navigate(AuthScreen)
        }
        Box(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding),
        ) {
            // TODO: Add your custom loading screen content here
            LoadingScreen()
            // TODO: ADD error screen if something goes wrong
//            LoadingErrorScreen(
//                errorMessage = errorMessage,
//                // retry function in viewmodel is called in `onRetry` to retry loading.
//                onRetry = { TODO() }
//            )
        }
    }
}