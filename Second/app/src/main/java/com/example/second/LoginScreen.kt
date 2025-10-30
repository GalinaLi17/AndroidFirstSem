package com.example.second
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Вход", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { new ->
                        email = new
                        if (emailError != null) emailError = null
                    },
                    label = { Text("Email") },
                    singleLine = true,
                    isError = emailError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) {
                    Text(
                        text = emailError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { new ->
                        password = new
                        if (passwordError != null) passwordError = null
                    },
                    label = { Text("Пароль") },
                    singleLine = true,
                    isError = passwordError != null,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            val desc = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                            Icon(imageVector = icon, contentDescription = desc)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (passwordError != null) {
                    Text(
                        text = passwordError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            Button(
                onClick = {
                    focusManager.clearFocus()
                    var ok = true

                    if (email.isBlank()) {
                        emailError = "Пожалуйста, введите email"
                        ok = false
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Неправильный формат email"
                        ok = false
                    }

                    if (password.isBlank()) {
                        passwordError = "Пожалуйста, введите пароль"
                        ok = false
                    } else if (password.length < 8) {
                        passwordError = "Минимальная длина пароля — 8 символов"
                        ok = false
                    }

                    if (ok) {
                        navController.navigate("notesList/$email")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.BottomCenter)
                    .imePadding()
                    .padding(bottom = 16.dp),
            ) {
                Text("Войти")
            }
        }
    }
}
