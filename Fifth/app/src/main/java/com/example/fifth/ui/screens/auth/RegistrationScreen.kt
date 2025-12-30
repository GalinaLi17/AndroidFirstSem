package com.example.fifth.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fifth.R
import com.example.fifth.ui.viewmodel.AuthViewModel
import com.example.fifth.ui.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: (Long) -> Unit
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as com.example.fifth.FifthApplication)
    )

    val registrationState by viewModel.registrationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val passwordsNotMatchMessage = stringResource(R.string.passwords_not_match)

    LaunchedEffect(registrationState) {
        when (registrationState) {
            is com.example.fifth.ui.viewmodel.RegistrationState.Success -> {
                val successState = registrationState as com.example.fifth.ui.viewmodel.RegistrationState.Success
                onRegistrationSuccess(successState.userId)
            }
            is com.example.fifth.ui.viewmodel.RegistrationState.Error -> {
                val errorState = registrationState as com.example.fifth.ui.viewmodel.RegistrationState.Error
                scope.launch {
                    snackbarHostState.showSnackbar(errorState.message)
                }
                viewModel.resetRegistrationState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.registration_title),
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text(stringResource(R.string.login_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(R.string.confirm_password_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (registrationState is com.example.fifth.ui.viewmodel.RegistrationState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (login.isNotBlank() && name.isNotBlank() &&
                            password.isNotBlank() && confirmPassword.isNotBlank()) {
                            if (password == confirmPassword) {
                                viewModel.register(login, password, name)
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(passwordsNotMatchMessage)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = login.isNotBlank() && name.isNotBlank() &&
                            password.isNotBlank() && confirmPassword.isNotBlank()
                ) {
                    Text(stringResource(R.string.register_confirm_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_button))
            }
        }
    }
}