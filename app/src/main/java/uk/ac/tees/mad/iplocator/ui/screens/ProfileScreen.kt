package uk.ac.tees.mad.iplocator.ui.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.model.dataclass.UserDetails
import uk.ac.tees.mad.iplocator.navigation.SubGraph
import uk.ac.tees.mad.iplocator.ui.utils.LoadingScreen
import uk.ac.tees.mad.iplocator.viewmodel.ProfileScreenViewModel
import kotlin.apply

val LocalIsDarkMode = staticCompositionLocalOf { mutableStateOf(true) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController, viewModel: ProfileScreenViewModel = koinViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }
    val isDarkMode = LocalIsDarkMode.current
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Profile") },
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
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                AppSettings(isDarkMode = isDarkMode, sharedPreferences= sharedPreferences)
            }
            item {
                PersonalDetails(viewModel = viewModel)
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(8.dp), thickness = 2.dp
                )
                LogOutButton(navController, viewModel)
            }
        }
    }
}

@Composable
fun AppSettings(isDarkMode: MutableState<Boolean>,
                sharedPreferences: SharedPreferences) {
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
                imageVector = Icons.Default.Settings,
                contentDescription = "App Settings",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "App Settings",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Card(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Dark Mode Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.DarkMode, contentDescription = "Dark Mode")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Dark Mode")
                        Switch(checked = isDarkMode.value, onCheckedChange = {
                            isDarkMode.value = it
                            sharedPreferences.edit().putBoolean("dark_mode", it).apply()
                        })
                    }
                }
            }
        }

    }
}


@Composable
fun PersonalDetails(viewModel: ProfileScreenViewModel) {
    val userDetailsResult by viewModel.userDetails.collectAsState()
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
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Details",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Profile Details",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Card(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (userDetailsResult) {
                    is AuthResult.Loading -> {
                        LoadingScreen()
                    }

                    is AuthResult.Success -> {
                        val userDetails =
                            (userDetailsResult as AuthResult.Success<UserDetails>).data
                        ProfileContent(userDetails)
                    }

                    is AuthResult.Error -> {
                        val error = (userDetailsResult as AuthResult.Error).exception
                        Text(
                            text = "Error: ${error.message}",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

        }
    }

}


@Composable
fun ProfileContent(userDetails: UserDetails) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User ID
        DetailColumn(
            icon = Icons.Default.AccountCircle,
            iconDesc = "User ID",
            label = "User ID",
            value = userDetails.userId
        )
        HorizontalDivider(
            modifier = Modifier.padding(4.dp), thickness = 2.dp
        )
        // Email
        DetailColumn(
            icon = Icons.Filled.Email,
            iconDesc = "Email",
            label = "Email",
            value = userDetails.email ?: "Not available"
        )
        HorizontalDivider(
            modifier = Modifier.padding(4.dp), thickness = 2.dp
        )
        // Name
        DetailColumn(
            icon = Icons.Filled.Person,
            iconDesc = "Name",
            label = "Name",
            value = if (userDetails.displayName.isNullOrEmpty()) "Not available" else userDetails.displayName
        )
        HorizontalDivider(
            modifier = Modifier.padding(4.dp), thickness = 2.dp
        )
        // Phone Number
        DetailColumn(
            icon = Icons.Filled.Phone,
            iconDesc = "Phone Number",
            label = "Phone Number",
            value = if (userDetails.phoneNumber.isNullOrEmpty()) "Not available" else userDetails.phoneNumber
        )

    }
}

@Composable
fun LogOutButton(navController: NavHostController, viewModel: ProfileScreenViewModel) {
    // Logout Button
    Button(
        onClick = {
            viewModel.LogOut()
            navController.navigate(SubGraph.AuthGraph) // Navigate to login screen
            {
                popUpTo(SubGraph.HomeGraph) {
                    inclusive = true
                }
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("Logout")
    }
}


@Composable
fun DetailColumn(icon: ImageVector, iconDesc: String, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(imageVector = icon, contentDescription = iconDesc)
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "$label:", fontWeight = FontWeight.Bold)
            Text(text = value)
        }
    }
}