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
import com.tp2025.mobile.data.EventoService

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    data class Asientos(val evento: Evento) : Screen()
}

@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
        var token by remember { mutableStateOf<String?>(null) }

        val eventoService = remember { EventoService() }
        val scope = rememberCoroutineScope()

        // 👇 CONFIGURACIÓN GLOBAL DE LA ALARMA (Issue #19)
        // Esto se ejecuta una vez cuando arranca la App
        DisposableEffect(Unit) {
            SessionManager.onSessionExpired = {
                // Cuando suene la alarma (401), hacemos esto:
                println("⚠️ App: Sesión expirada detectada. Redirigiendo al Login.")
                SessionManager.clear()
                token = null
                currentScreen = Screen.Login
            }
            onDispose { }
        }

        when (val screen = currentScreen) {
            is Screen.Login -> {
                LoginScreen(onLoginSuccess = { nuevoToken, usuario ->
                    token = nuevoToken

                    scope.launch {
                        val ultimoEventoId = eventoService.recuperarUltimaVisita(usuario)
                        if (ultimoEventoId != null) {
                            val eventos = eventoService.obtenerEventos(nuevoToken)
                            val eventoAuntiguo = eventos.find { it.id == ultimoEventoId }
                            if (eventoAuntiguo != null) {
                                currentScreen = Screen.Asientos(eventoAuntiguo)
                            } else {
                                currentScreen = Screen.Home
                            }
                        } else {
                            currentScreen = Screen.Home
                        }
                    }
                })
            }
            is Screen.Home -> {
                if (token != null) {
                    HomeScreen(
                        token = token!!,
                        onEventoClick = { eventoSeleccionado ->
                            currentScreen = Screen.Asientos(eventoSeleccionado)
                        },
                        onLogout = {
                            SessionManager.clear()
                            token = null
                            currentScreen = Screen.Login
                        }
                    )
                } else {
                    currentScreen = Screen.Login
                }
            }
            is Screen.Asientos -> {
                AsientosScreen(
                    evento = screen.evento,
                    onBack = {
                        currentScreen = Screen.Home
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit) {
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
                        SessionManager.currentUser = username
                        statusMessage = "✅ Login Correcto"
                        onLoginSuccess(tokenRecibido, username)
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