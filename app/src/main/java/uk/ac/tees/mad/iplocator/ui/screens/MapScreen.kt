package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MarkerState.Companion.invoke
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.iplocator.model.dataclass.IpDetailsUiState
import uk.ac.tees.mad.iplocator.ui.utils.DetailRow
import uk.ac.tees.mad.iplocator.ui.utils.LoadingErrorScreen
import uk.ac.tees.mad.iplocator.ui.utils.LoadingScreen
import uk.ac.tees.mad.iplocator.ui.utils.RememberShakeSensor
import uk.ac.tees.mad.iplocator.viewmodel.MapScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    latitude: Double?,
    longitude: Double?,
    ip: String?,
    viewmodel: MapScreenViewModel = koinViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    viewmodel.updateIpAndLocation(ip.toString(), latitude, longitude, LocalContext.current)
    viewmodel.getIpLocationDetails(ip.toString())

    val coordinate by viewmodel.coordinates.collectAsStateWithLifecycle()
    val address by viewmodel.address.collectAsStateWithLifecycle()
    val ip by viewmodel.ip.collectAsStateWithLifecycle()


    val isRefreshing by viewmodel.isRefreshing.collectAsStateWithLifecycle()
    val ipDetailsUiState by viewmodel.ipDetailsUiState.collectAsStateWithLifecycle()
    // Add shake detection
    RememberShakeSensor {
        // Trigger refresh when shake is detected
        ip?.let { viewmodel.refreshLocation(it) }
    }


    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Map Screen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->
        if (isRefreshing) {
            LoadingScreen()
        } else {
            when (ipDetailsUiState) {
                is IpDetailsUiState.Loading -> {
                    LoadingScreen()
                }

                is IpDetailsUiState.Error -> {
                    LoadingErrorScreen(errorMessage = (ipDetailsUiState as IpDetailsUiState.Error).message,
                        onRetry = {
                            viewmodel.refreshLocation(ip.toString())
                        })
                }

                is IpDetailsUiState.Success -> {
                    val ipLocation =
                        (ipDetailsUiState as IpDetailsUiState.Success).ipLocationDetails
                    viewmodel.updateIpAndLocation(
                        ipLocation.ip.toString(),
                        ipLocation.latitude,
                        ipLocation.longitude,
                        LocalContext.current
                    )
                    // Camera position state
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(coordinate, 10f) // Zoom level 10
                    }

                    val markerState = remember { MarkerState(position = coordinate) }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Coordinates",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Location Details for IP: $ip",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Card(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    DetailRow(
                                        label = "Latitude", value = "${latitude}"
                                    )
                                    DetailRow(
                                        label = "Longitude", value = "${longitude}"
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp, horizontal = 8.dp)
                                    ) {
                                        Text(text = "Address:", fontWeight = FontWeight.Bold)
                                        Text(text = address.toString())
                                        HorizontalDivider(
                                            modifier = Modifier.padding(4.dp),
                                            thickness = 2.dp
                                        )
                                        // Google Map
                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            GoogleMap(
                                                modifier = Modifier.fillMaxSize(),
                                                cameraPositionState = cameraPositionState,
                                            ) {
                                                // Add a marker at the specified coordinates
                                                Marker(
                                                    state = markerState,
                                                    title = "Location of IP: $ip",
                                                    snippet = "Latitude: $latitude, Longitude: $longitude"
                                                )
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
    }
}
