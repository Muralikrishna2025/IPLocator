package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.iplocator.model.dataclass.LoadingState
import uk.ac.tees.mad.iplocator.navigation.Dest
import uk.ac.tees.mad.iplocator.navigation.SubGraph
import uk.ac.tees.mad.iplocator.ui.utils.LoadingErrorScreen
import uk.ac.tees.mad.iplocator.ui.utils.LoadingScreen
import uk.ac.tees.mad.iplocator.viewmodel.SplashScreenViewModel

@Composable
fun SplashScreen(
    navController: NavHostController, viewModel: SplashScreenViewModel = koinViewModel()
) {
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = loadingState,
                animationSpec = tween(durationMillis = 1000),
                label = "splashScreen"
            ) { state ->
                when (state) {
                    is LoadingState.Loading -> {
                        AnimatedVisibility(state == LoadingState.Loading) {
                            LoadingScreen()
                        }
                    }

                    is LoadingState.Error -> {
                        LoadingErrorScreen(
                            errorMessage = state.message,
                            onRetry = { viewModel.startLoading() })
                    }

                    is LoadingState.Success -> {
                        val isUserLoggedIn = viewModel.isLoggedIn()
                        LaunchedEffect(key1 = Unit) {
                            if (!isUserLoggedIn) {
                                navController.navigate(SubGraph.AuthGraph) {
                                    popUpTo(Dest.SplashScreen) {
                                        inclusive = true
                                    }
                                }
                            } else {
                                navController.navigate(SubGraph.HomeGraph) {
                                    popUpTo(Dest.SplashScreen) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}