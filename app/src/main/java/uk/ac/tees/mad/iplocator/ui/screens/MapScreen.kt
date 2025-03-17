package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap

@Composable
fun MapScreen(navController: NavHostController, latitude: Double?, longitude: Double?) {
    val location by remember { mutableStateOf(LatLng(latitude ?: 0.0, longitude ?: 0.0)) }
    Scaffold(modifier = Modifier.fillMaxSize()) {innerPadding->
    Column(modifier = Modifier.fillMaxSize().padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Map Screen")
        Text(text = "Latitude: $latitude")
        Text(text = "Longitude: $longitude")
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GoogleMap(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            )
        }
    }
    }
}