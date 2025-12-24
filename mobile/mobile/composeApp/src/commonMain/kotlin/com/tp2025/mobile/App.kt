package com.tp2025.mobile

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.tp2025.mobile.auth.AuthService
import com.tp2025.mobile.auth.SessionManager
import com.tp2025.mobile.ui.HomeScreen
import com.tp2025.mobile.ui.AsientosScreen
import com.tp2025.mobile.data.Evento

// 1. Definimos los "destinos" posibles de nuestra App
sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    data class Asientos(val evento: Evento) : Screen() // Este destino lleva datos
}

@Composable
fun App() {
    MaterialTheme {
        // Estado de la navegación: Empezamos en Login
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

        // Estado del Token
        var token by remember { mutableStateOf<String?>(null) }

        // 2. El "Router" que decide qué mostrar
        when (val screen = currentScreen) {
            is Screen.Login -> {
                LoginScreen(onLoginSuccess = { nuevoToken ->
                    token = nuevoToken
                    currentScreen = Screen.Home // Al loguear, vamos al Home
                })
            }
            is Screen.Home -> {
                if (token != null) {
                    HomeScreen(
                        token = token!!,
                        onEventoClick = { eventoSeleccionado ->
                            // Navegamos a la pantalla de asientos pasando el evento
                            currentScreen = Screen.Asientos(eventoSeleccionado)
                        }
                    )
                } else {
                    // Si se perdió el token, volvemos al login
                    currentScreen = Screen.Login
                }
            }
            is Screen.Asientos -> {
                AsientosScreen(
                    evento = screen.evento,
                    onBack = {
                        // Al volver (flecha o compra exitosa), regresamos al Home
                        currentScreen = Screen.Home
                    }
                )
            }
        }
    }
}

/**
 * Pantalla de Login (Tal cual la tenías)
 */
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin") }
    var statusMessage by remember { mutableStateOf("Esperando login...") }
    var isLoggingIn by remember { mutableStateOf(false) }

    val authService = remember { AuthService() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Entradera Móvil 🎟️", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isLoggingIn) return@Button
                isLoggingIn = true

                scope.launch {
                    statusMessage = "🔄 Conectando..."
                    val resultado = authService.login(username, password)

                    resultado.onSuccess { tokenRecibido ->
                        SessionManager.jwtToken = tokenRecibido
                        statusMessage = "✅ Login Correcto"
                        onLoginSuccess(tokenRecibido)
                    }.onFailure { error ->
                        statusMessage = "❌ Error: ${error.message}"
                        isLoggingIn = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoggingIn
        ) {
            if (isLoggingIn) {
                CircularProgressIndicator(color = MaterialTheme.colors.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("INGRESAR")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(statusMessage)
    }
}