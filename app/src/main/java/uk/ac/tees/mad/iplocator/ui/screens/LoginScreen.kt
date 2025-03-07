package uk.ac.tees.mad.iplocator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.navigation.Dest
import uk.ac.tees.mad.iplocator.navigation.SubGraph
import uk.ac.tees.mad.iplocator.viewmodel.LoginScreenViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginScreenViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val logInResult by viewModel.logInResult.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (isLoginMode) {
            true -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()), contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Authentication Icon",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Login",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Please login in to continue.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            OutlinedTextField(value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isPasswordVisible = !isPasswordVisible
                                    }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = "Toggle Password Visibility"
                                        )
                                    }
                                })

                            Button(
                                enabled = email.isNotBlank() && password.isNotBlank(),
                                onClick = {
                                    // Placeholder for login logic
                                    println("Login button clicked with email: $email, password: $password")

                                    viewModel.logIn(email, password)
                                    isLoginMode = !isLoginMode

                                    //onLoginSuccess()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Login")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Don't have an account?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(onClick = {
                                    errorMessage = null
                                    navController.navigate(Dest.SignUpScreen)
                                }) {
                                    Text("Sign Up")
                                }
                            }
                        }
                    }
                }
            }

            false -> {
                when (val result = logInResult) {
                    is AuthResult.Loading -> {
                        AlertDialog(
                            onDismissRequest = {
                                isLoginMode = !isLoginMode
                            },
                            icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                            title = { Text("Loading") },
                            text = {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                }
                            },
                            confirmButton = { })
                    }

                    is AuthResult.Success -> {
                        // Handle successful sign-up
                        AlertDialog(
                            icon = { Icon(Icons.Default.CloudDone, contentDescription = null) },
                            title = { Text("Log in Successful") },
                            text = {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("You have successfully Logged in.")
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    navController.navigate(SubGraph.HomeGraph) {
                                        popUpTo(SubGraph.AuthGraph) {
                                            inclusive = true
                                        }
                                    }
                                }) {
                                    Text("Go to Home Screen")
                                }
                            },
                            onDismissRequest = {
                                navController.navigate(SubGraph.HomeGraph) {
                                    popUpTo(SubGraph.AuthGraph) {
                                        inclusive = true
                                    }
                                }
                            }
                        )

                    }

                    is AuthResult.Error -> {
                        // Handle sign-up error
                        AlertDialog(
                            icon = { Icon(Icons.Default.Error, contentDescription = null) },
                            title = { Text("Error") },
                            text = {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(result.exception.message.toString())
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    isLoginMode = !isLoginMode
                                }) {
                                    Text("Retry?")
                                }
                            },
                            onDismissRequest = {
                                isLoginMode = !isLoginMode
                            }
                        )
                    }
                }
            }
        }

    }
}