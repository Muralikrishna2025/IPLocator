package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.iplocator.model.dataclass.IpDetailsUiState
import uk.ac.tees.mad.iplocator.model.dataclass.SearchHistoryItem
import uk.ac.tees.mad.iplocator.navigation.Dest
import uk.ac.tees.mad.iplocator.ui.utils.AdditionalInfo
import uk.ac.tees.mad.iplocator.ui.utils.Coordinates
import uk.ac.tees.mad.iplocator.ui.utils.HeaderCard
import uk.ac.tees.mad.iplocator.ui.utils.ISPDetail
import uk.ac.tees.mad.iplocator.ui.utils.LoadingErrorScreen
import uk.ac.tees.mad.iplocator.ui.utils.LoadingScreen
import uk.ac.tees.mad.iplocator.ui.utils.LocationDetail
import uk.ac.tees.mad.iplocator.viewmodel.SearchScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController, viewModel: SearchScreenViewModel = koinViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isErrorInput by viewModel.isErrorInput.collectAsStateWithLifecycle()
    val ipDetailsUiState by viewModel.ipDetailsUiState.collectAsStateWithLifecycle()
    val inputIp by viewModel.inputIp.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Search Screen") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(!isErrorInput && inputIp.isNotBlank()) {
                val ipLocation by viewModel.ipLocation.collectAsStateWithLifecycle()
                ExtendedFloatingActionButton(onClick = {
                    navController.navigate(
                        Dest.MapScreen(
                            ipLocation?.latitude,
                            ipLocation?.longitude,
                            ipLocation?.ip
                        )
                    )
                }, icon = {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }, text = { Text("Go to Map Screen") })
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            IpSearchBar(
                viewModel = viewModel,
                modifier = Modifier,
                searchHistory = searchHistory,
                onSearch = { ip, isError ->
                    viewModel.updateIsErrorInput(isError)
                    if (!isError) {
                        viewModel.updateInputIp(ip)
                        viewModel.getIpLocationDetails(ip)
                    }
                })
            AnimatedVisibility(visible = isErrorInput) {
                Text(
                    text = "Invalid IP address format (e.g., 192.168.1.1)",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            AnimatedVisibility(!isErrorInput && inputIp.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (ipDetailsUiState) {
                        is IpDetailsUiState.Loading -> {
                            LoadingScreen()
                        }

                        is IpDetailsUiState.Error -> {
                            LoadingErrorScreen(errorMessage = (ipDetailsUiState as IpDetailsUiState.Error).message,
                                onRetry = {
                                    if (inputIp.isNotBlank()) {
                                        viewModel.getIpLocationDetails(inputIp)
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
                                    HeaderCard(
                                        header = "Viewing Results for", ipLocation = ipLocation
                                    )
                                }
                                item { LocationDetail(ipLocation) }
                                item { Coordinates(ipLocation) }
                                item { ISPDetail(ipLocation) }
                                item { AdditionalInfo(ipLocation) }
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpSearchBar(
    viewModel: SearchScreenViewModel,
    modifier: Modifier = Modifier,
    searchHistory: List<SearchHistoryItem>,
    onSearch: (ip: String, isError: Boolean) -> Unit
) {
    val expanded by viewModel.searchBarExpanded.collectAsStateWithLifecycle()
    var ipAddress by rememberSaveable { mutableStateOf("") }
    var isError by rememberSaveable { mutableStateOf(false) }


    fun validateIpAddress(ip: String): Boolean {
        val ipPattern = Regex(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        )
        return ipPattern.matches(ip)
    }

    DockedSearchBar(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.fillMaxWidth(),
                colors = if (isError) SearchBarDefaults.inputFieldColors(MaterialTheme.colorScheme.error) else SearchBarDefaults.inputFieldColors(),
                query = ipAddress,
                onQueryChange = {
                    // Only allow digits and periods
                    val filteredText = it.filter { char -> char.isDigit() || char == '.' }
                    ipAddress = filteredText
                    isError = (!validateIpAddress(filteredText) && filteredText.isNotBlank())
                },
                onSearch = { newQuery ->
                    viewModel.updateSearchBarExpanded(false)
                    onSearch(newQuery, isError)
                },
                expanded = expanded,
                onExpandedChange = {
                    viewModel.updateSearchBarExpanded(it)
                },
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search, contentDescription = null
                    )
                },
                trailingIcon = {
                    if (expanded) {
                        IconButton(onClick = {
                            if (ipAddress.isNotBlank()) {
                                ipAddress = ""
                                //viewModel.searchIP(query)
                            }
                            viewModel.updateSearchBarExpanded(false)
                        }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }

                },
            )
        },
        expanded = expanded,
        onExpandedChange = {
            viewModel.updateSearchBarExpanded(it)
        },
    ) {
        if (searchHistory.isEmpty() || ipAddress.isBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Start Typing to Search")
                Text("Only digits and periods are allowed!")
            }
        } else if (ipAddress.isNotBlank()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(searchHistory) { item ->
                    ListItem(headlineContent = { Text(item.searchedQuery) },
                        colors = ListItemDefaults.colors(
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier.clickable {
                            ipAddress = item.searchedQuery
                            isError = false
                            viewModel.updateSearchBarExpanded(false)
                            onSearch(ipAddress, isError)
                        },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Search, contentDescription = null
                            )
                        })

                }
            }
        }
    }

}