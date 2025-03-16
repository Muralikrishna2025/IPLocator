package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun MapScreen(navController: NavHostController, latitude: Double?, longitude: Double?) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,verticalArrangement = Arrangement.Center) {
        Text(text = "Map Screen")
        Text(text = "Latitude: $latitude")
        Text(text = "Longitude: $longitude")
    }
}