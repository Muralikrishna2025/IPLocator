package uk.ac.tees.mad.iplocator.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import uk.ac.tees.mad.iplocator.model.dataclass.IpDetailsUiState
import uk.ac.tees.mad.iplocator.navigation.Dest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpLocatorTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController,
    ipDetailsUiState: IpDetailsUiState
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(Dest.ProfileScreen)
            }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Icon",
                )
            }
        },
        actions = {
            AnimatedVisibility(ipDetailsUiState is IpDetailsUiState.Success && ipDetailsUiState.ipLocationDetails.latitude != null && ipDetailsUiState.ipLocationDetails.longitude != null) {
                IconButton(onClick = {
                    navController.navigate(Dest.SearchScreen)
                }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        scrollBehavior = scrollBehavior
    )
}