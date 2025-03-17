package uk.ac.tees.mad.iplocator.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.iplocator.R
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.model.dataclass.UserDetails
import uk.ac.tees.mad.iplocator.navigation.SubGraph
import uk.ac.tees.mad.iplocator.viewmodel.ProfileScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileScreenViewModel = koinViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val userDetailsResult by viewModel.userDetails.collectAsState()
    var isDarkMode by remember { mutableStateOf(false) } // Dark mode toggle state
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
        }
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {}
            when (userDetailsResult) {
                is AuthResult.Loading -> {
                    Text("Loading...")
                }
                is AuthResult.Success -> {
                    val userDetails = (userDetailsResult as AuthResult.Success<UserDetails>).data
                    ProfileContent(userDetails)
                }
                is AuthResult.Error -> {
                    val error = (userDetailsResult as AuthResult.Error).exception
                    Text("Error: ${error.message}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dark Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Mode")
                Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = !isDarkMode })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = {
                    //FirebaseAuth.getInstance().signOut()
                    navController.navigate(SubGraph.AuthGraph) // Navigate to login screen
                    {
                        popUpTo(SubGraph.HomeGraph) {
                            inclusive = true
                        }
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }

}

@Composable
fun ProfileContent(userDetails: UserDetails) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
//        Image(
//            painter = painterResource(id = R.drawable.profile), // Replace with actual image loading
//            contentDescription = "Profile Picture",
//            modifier = Modifier
//                .size(120.dp)
//                .clip(CircleShape)
//        )
        Spacer(modifier = Modifier.height(16.dp))

        // User Name (if available)
        if (!userDetails.displayName.isNullOrEmpty()) {
            Text(
                text = userDetails.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // User ID
        ListItem(
            headlineContent = { Text(text = "User ID") },
            supportingContent = { Text(text = userDetails.userId) },
            leadingContent = { Icon(imageVector = Icons.Filled.Info, contentDescription = "User ID") }
        )
        Divider()

        // Email
        ListItem(
            headlineContent = { Text(text = "Email") },
            supportingContent = { Text(text = userDetails.email ?: "Not available") },
            leadingContent = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Email") }
        )
        Divider()

        // Phone Number
        ListItem(
            headlineContent = { Text(text = "Phone Number") },
            supportingContent = { Text(text = userDetails.phoneNumber ?: "Not available") },
            leadingContent = { Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone Number") }
        )
        Divider()

        // Email Verified
        ListItem(
            headlineContent = { Text(text = "Email Verified") },
            supportingContent = { Text(text = userDetails.isEmailVerified.toString()) },
            leadingContent = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Email Verified") }
        )
        Divider()
    }
}