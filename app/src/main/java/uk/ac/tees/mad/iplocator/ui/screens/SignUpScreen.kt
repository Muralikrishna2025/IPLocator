package uk.ac.tees.mad.iplocator.ui.screens

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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.navigation.SubGraph
import uk.ac.tees.mad.iplocator.viewmodel.SignUpScreenViewModel

@Composable
fun SignUpScreen(
    navController: NavHostController, viewModel: SignUpScreenViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(true) }
    val signUpResult by viewModel.signUpResult.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    Scaffold(
        modifier = Modifier.fillMaxSize()

    ) { innerPadding ->
        when (isSignUpMode) {
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
                                text = "Sign Up",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Create an account to get started.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            OutlinedTextField(value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterEmail),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = {
                                    focusRequesterPassword.requestFocus()
                                }),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            OutlinedTextField(value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterPassword),
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                }),
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
                                    viewModel.signUp(email, password)
                                    isSignUpMode = !isSignUpMode
                                    // Placeholder for signup logic
                                    println("Sign Up button clicked with email: $email, password: $password")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Sign Up")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Already have an account?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(onClick = {
                                    navController.navigate(SubGraph.AuthGraph) {
                                        popUpTo(SubGraph.AuthGraph) {
                                            inclusive = true
                                        }
                                    }
                                }) {
                                    Text("Login")
                                }
                            }
                        }
                    }
                }
            }

            false -> {
                when (val result = signUpResult) {
                    is AuthResult.Loading -> {
                        AlertDialog(onDismissRequest = {
                            isSignUpMode = !isSignUpMode
                        },
                            icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                            title = { Text("Loading") },
                            text = {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                }
                            },
                            confirmButton = { })
                    }

                    is AuthResult.Success -> {
                        // Handle successful sign-up
                        AlertDialog(icon = {
                            Icon(
                                Icons.Default.CloudDone, contentDescription = null
                            )
                        }, title = { Text("Sign Up Successful") }, text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("You have successfully signed up.")
                            }
                        }, confirmButton = {
                            TextButton(onClick = {
                                navController.navigate(SubGraph.AuthGraph) {
                                    popUpTo(SubGraph.AuthGraph) {
                                        inclusive = true
                                    }
                                }
                            }) {
                                Text("Go to Login Screen")
                            }
                        }, onDismissRequest = {
                            navController.navigate(SubGraph.AuthGraph) {
                                popUpTo(SubGraph.AuthGraph) {
                                    inclusive = true
                                }
                            }
                        })

                    }

                    is AuthResult.Error -> {
                        // Handle sign-up error
                        AlertDialog(icon = { Icon(Icons.Default.Error, contentDescription = null) },
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
                                    isSignUpMode = !isSignUpMode
                                }) {
                                    Text("Retry?")
                                }
                            },
                            onDismissRequest = {
                                isSignUpMode = !isSignUpMode
                            })
                    }
                }
            }
        }
    }
}
