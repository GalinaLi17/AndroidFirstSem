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
fun LoginScreen(
    onNavigateToRegistration: () -> Unit,
    onLoginSuccess: (Long) -> Unit,
    onNavigateToRestoreAccount: (Long) -> Unit
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as com.example.fifth.FifthApplication)
    )

    val authState by viewModel.authState.collectAsState()
    val softDeletedAccount by viewModel.softDeletedAccount.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginTitle = stringResource(R.string.login_title)
    val loginHint = stringResource(R.string.login_hint)
    val passwordHint = stringResource(R.string.password_hint)
    val loginButton = stringResource(R.string.login_button)
    val registerButton = stringResource(R.string.register_button)

    LaunchedEffect(softDeletedAccount) {
        softDeletedAccount?.let { account ->
            onNavigateToRestoreAccount(account.userId)
            viewModel.resetAuthState()
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is com.example.fifth.ui.viewmodel.AuthState.Success -> {
                val successState = authState as com.example.fifth.ui.viewmodel.AuthState.Success
                onLoginSuccess(successState.user.id)
            }
            is com.example.fifth.ui.viewmodel.AuthState.Error -> {
                val errorState = authState as com.example.fifth.ui.viewmodel.AuthState.Error
                if (softDeletedAccount == null) {
                    scope.launch {
                        snackbarHostState.showSnackbar(errorState.message)
                    }
                }
                viewModel.resetAuthState()
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
                text = loginTitle,
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text(loginHint) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(passwordHint) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (authState is com.example.fifth.ui.viewmodel.AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (login.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(login, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = login.isNotBlank() && password.isNotBlank()
                ) {
                    Text(loginButton)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToRegistration,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(registerButton)
            }
        }
    }
}