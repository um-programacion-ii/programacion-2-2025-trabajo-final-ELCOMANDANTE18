package com.tp2025.mobile

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.tp2025.mobile.auth.SessionManager
import com.tp2025.mobile.ui.HomeScreen
import com.tp2025.mobile.ui.AsientosScreen
import com.tp2025.mobile.ui.LoginScreen // Importamos la nueva pantalla
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

        // 🚨 ALARMA DE SESIÓN (Global)
        DisposableEffect(Unit) {
            SessionManager.onSessionExpired = {
                println("⚠️ App: Sesión expirada. Volviendo al Login.")
                SessionManager.clear()
                token = null
                currentScreen = Screen.Login
            }
            onDispose { }
        }

        // 🚦 ENRUTADOR DE PANTALLAS
        when (val screen = currentScreen) {
            is Screen.Login -> {
                LoginScreen(
                    onLoginSuccess = { nuevoToken, usuario ->
                        token = nuevoToken

                        // 🧠 Lógica Inteligente: ¿Estaba viendo un evento antes?
                        scope.launch {
                            println("🔍 Buscando última visita para: $usuario")
                            val ultimoEventoId = eventoService.recuperarUltimaVisita(usuario)

                            if (ultimoEventoId != null) {
                                // Si tenía una visita, buscamos el evento completo para restaurarlo
                                val eventos = eventoService.obtenerEventos(nuevoToken)
                                val eventoAntiguo = eventos.find { it.id == ultimoEventoId }

                                if (eventoAntiguo != null) {
                                    println("✅ Restaurando sesión en evento: ${eventoAntiguo.titulo}")
                                    currentScreen = Screen.Asientos(eventoAntiguo)
                                } else {
                                    currentScreen = Screen.Home
                                }
                            } else {
                                currentScreen = Screen.Home
                            }
                        }
                    }
                )
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
                        // Al volver, limpiamos la "última visita" si quisieras,
                        // o simplemente vamos al Home
                        currentScreen = Screen.Home
                    }
                )
            }
        }
    }
}