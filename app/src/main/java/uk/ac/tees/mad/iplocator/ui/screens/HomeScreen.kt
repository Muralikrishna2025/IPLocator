package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation
import uk.ac.tees.mad.iplocator.navigation.Dest
import uk.ac.tees.mad.iplocator.ui.utils.IpLocatorTopAppBar
import uk.ac.tees.mad.iplocator.ui.utils.LoadingErrorScreen
import uk.ac.tees.mad.iplocator.ui.utils.LoadingScreen
import uk.ac.tees.mad.iplocator.ui.utils.shimmerEffect
import uk.ac.tees.mad.iplocator.viewmodel.HomeScreenViewModel
import uk.ac.tees.mad.iplocator.R
import uk.ac.tees.mad.iplocator.model.dataclass.IpDetailsUiState
import uk.ac.tees.mad.iplocator.ui.utils.AdditionalInfo
import uk.ac.tees.mad.iplocator.ui.utils.Coordinates
import uk.ac.tees.mad.iplocator.ui.utils.HeaderCard
import uk.ac.tees.mad.iplocator.ui.utils.ISPDetail
import uk.ac.tees.mad.iplocator.ui.utils.LocationDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController, viewmodel: HomeScreenViewModel = koinViewModel()
) {
    val ipDetailsUiState by viewmodel.ipDetailsUiState.collectAsStateWithLifecycle()
    val deviceIp by viewmodel.deviceIp.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            IpLocatorTopAppBar(
                title = "IP Locator", scrollBehavior = scrollBehavior, navController = navController
            )
        },
        floatingActionButton = {
            AnimatedVisibility(deviceIp!=null) {
            ExtendedFloatingActionButton(onClick = { navController.navigate(Dest.MapScreen) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = { Text("Go to Map Screen") })}
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (ipDetailsUiState) {
                is IpDetailsUiState.Loading -> {
                    LoadingScreen()
                }

                is IpDetailsUiState.Error -> {
                    LoadingErrorScreen(errorMessage = (ipDetailsUiState as IpDetailsUiState.Error).message,
                        onRetry = {
                            if (deviceIp == null) {
                                viewmodel.getDeviceIP()
                            } else if (deviceIp != null) {
                                viewmodel.getIpLocationDetails(deviceIp!!)
                            }
                        })
                }

                is IpDetailsUiState.Success -> {
                    val ipLocation =
                        (ipDetailsUiState as IpDetailsUiState.Success).ipLocationDetails
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(400.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(
                            span = StaggeredGridItemSpan.FullLine
                        ) {
                            HeaderCard(header = "Your", ipLocation = ipLocation)
                        }
                        item { LocationDetail(ipLocation) }
                        item {Coordinates(ipLocation)}
                        item { ISPDetail(ipLocation) }
                        item{AdditionalInfo(ipLocation)}
//            item { TimezoneDetail(ipLocation) }
//            item { CurrencyDetail(ipLocation) }
                        item(
                            span = StaggeredGridItemSpan.FullLine
                        ) { Spacer(modifier = Modifier.height(80.dp)) }
                    }

                }
            }
        }

    }

}