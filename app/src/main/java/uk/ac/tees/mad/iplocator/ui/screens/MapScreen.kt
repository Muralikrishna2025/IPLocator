package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocationData
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
    viewModel: MapScreenViewModel = koinViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val locationData by viewModel.ipLocationData.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val ipDetailsUiState by viewModel.ipDetailsUiState.collectAsStateWithLifecycle()
    val offlineMode by viewModel.offlineMode.collectAsStateWithLifecycle()

    // Initial data fetch
    LaunchedEffect(ip) {
        if (ip != null) {
            viewModel.updateIpAndLocation(ip, latitude, longitude, context)
            viewModel.getIpLocationDetails(ip)
        }
    }

    // Shake detection
    RememberShakeSensor {
        locationData.ip?.let { viewModel.refreshLocation(it) }
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Map Screen") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }, actions = {
                AnimatedVisibility(offlineMode == true) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.CloudOff,
                            "Offline",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                AnimatedVisibility(offlineMode == false) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Outlined.CloudDone, "Online", tint = Color.Green)
                    }
                }
            }, scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isRefreshing) {
                LoadingScreen()
            } else {
                when (ipDetailsUiState) {
                    is IpDetailsUiState.Loading -> LoadingScreen()
                    is IpDetailsUiState.Error -> LoadingErrorScreen(errorMessage = (ipDetailsUiState as IpDetailsUiState.Error).message,
                        onRetry = { locationData.ip?.let { viewModel.refreshLocation(it) } })

                    is IpDetailsUiState.Success -> {
                        val ipLocation =
                            (ipDetailsUiState as IpDetailsUiState.Success).ipLocationDetails
                        viewModel.updateIpAndLocation(
                            ipLocation.ip.toString(),
                            ipLocation.latitude,
                            ipLocation.longitude,
                            context
                        )
                        LocationDetailsContent(ipLocationData = locationData)
                    }
                }
            }
        }
    }
}

@Composable
fun LocationDetailsContent(ipLocationData: IpLocationData) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ipLocationData.coordinates, 10f)
    }
    val markerState = remember { MarkerState(position = ipLocationData.coordinates) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = "Location Details for IP: ${ipLocationData.ip}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Card(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                DetailRow(label = "Latitude", value = "${ipLocationData.latitude}")
                DetailRow(label = "Longitude", value = "${ipLocationData.longitude}")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Text(text = "Address:", fontWeight = FontWeight.Bold)
                    Text(text = ipLocationData.address.toString())
                    HorizontalDivider(modifier = Modifier.padding(4.dp), thickness = 2.dp)
                    Box(modifier = Modifier.fillMaxSize()) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                        ) {
                            Marker(
                                state = markerState,
                                title = "Location of IP: ${ipLocationData.ip}",
                                snippet = "Latitude: ${ipLocationData.latitude}, Longitude: ${ipLocationData.longitude}"
                            )
                        }
                    }
                }
            }
        }
    }
}