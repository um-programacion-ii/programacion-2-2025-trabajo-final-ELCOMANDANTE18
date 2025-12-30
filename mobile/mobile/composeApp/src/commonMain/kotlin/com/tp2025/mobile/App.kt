package com.tp2025.mobile

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.tp2025.mobile.auth.SessionManager
import com.tp2025.mobile.ui.HomeScreen
import com.tp2025.mobile.ui.AsientosScreen
import com.tp2025.mobile.ui.LoginScreen
import com.tp2025.mobile.ui.RegistroScreen
import com.tp2025.mobile.ui.EventoDetalleScreen // <--- IMPORTANTE
import com.tp2025.mobile.data.Evento
import com.tp2025.mobile.data.EventoService

sealed class Screen {
    object Login : Screen()
    object Registro : Screen()
    object Home : Screen()
    data class Detalle(val evento: Evento) : Screen() // <--- NUEVA PANTALLA
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
                        // Restaurar sesión si existía
                        scope.launch {
                            val ultimoEventoId = eventoService.recuperarUltimaVisita(usuario)
                            if (ultimoEventoId != null) {
                                val eventos = eventoService.obtenerEventos(nuevoToken)
                                val eventoAntiguo = eventos.find { it.id == ultimoEventoId }
                                if (eventoAntiguo != null) {
                                    // Al restaurar, mejor ir al detalle que directo a asientos
                                    currentScreen = Screen.Detalle(eventoAntiguo)
                                } else {
                                    currentScreen = Screen.Home
                                }
                            } else {
                                currentScreen = Screen.Home
                            }
                        }
                    },
                    onIrARegistro = { currentScreen = Screen.Registro }
                )
            }

            is Screen.Registro -> {
                RegistroScreen(
                    onBack = { currentScreen = Screen.Login },
                    onRegistroSuccess = { currentScreen = Screen.Login }
                )
            }

            is Screen.Home -> {
                if (token != null) {
                    HomeScreen(
                        token = token!!,
                        onEventoClick = { eventoSeleccionado ->
                            // AQUI EL CAMBIO: Vamos al Detalle primero
                            currentScreen = Screen.Detalle(eventoSeleccionado)
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

            // --- PANTALLA DE DETALLE ---
            is Screen.Detalle -> {
                EventoDetalleScreen(
                    evento = screen.evento,
                    onBack = { currentScreen = Screen.Home },
                    onComprarClick = {
                        // Del detalle pasamos a los asientos
                        currentScreen = Screen.Asientos(screen.evento)
                    }
                )
            }

            is Screen.Asientos -> {
                AsientosScreen(
                    evento = screen.evento,
                    onBack = {
                        // Al volver de asientos, regresamos al detalle
                        currentScreen = Screen.Detalle(screen.evento)
                    }
                )
            }
        }
    }
}