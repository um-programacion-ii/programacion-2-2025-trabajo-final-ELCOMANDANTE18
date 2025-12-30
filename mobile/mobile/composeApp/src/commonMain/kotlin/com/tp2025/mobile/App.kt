package com.tp2025.mobile

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.tp2025.mobile.auth.SessionManager
import com.tp2025.mobile.ui.HomeScreen
import com.tp2025.mobile.ui.AsientosScreen
import com.tp2025.mobile.ui.LoginScreen
import com.tp2025.mobile.ui.RegistroScreen
import com.tp2025.mobile.ui.EventoDetalleScreen
import com.tp2025.mobile.ui.DetalleVentaScreen // <--- NUEVA PANTALLA
import com.tp2025.mobile.data.Evento
import com.tp2025.mobile.data.EventoService
import com.tp2025.mobile.data.AsientoVenta // <--- Necesario para pasar la lista

// 1. DEFINIMOS LAS PANTALLAS (Rutas)
sealed class Screen {
    object Login : Screen()
    object Registro : Screen()
    object Home : Screen()
    data class Detalle(val evento: Evento) : Screen()
    data class Asientos(val evento: Evento) : Screen()
    // 👇 NUEVA RUTA: Recibe el evento y los asientos que elegiste
    data class DetalleVenta(val evento: Evento, val asientos: List<AsientoVenta>) : Screen()
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
                        // Lógica de restauración de sesión (opcional)
                        scope.launch {
                            val ultimoEventoId = eventoService.recuperarUltimaVisita(usuario)
                            if (ultimoEventoId != null) {
                                val eventos = eventoService.obtenerEventos(nuevoToken)
                                val eventoAntiguo = eventos.find { it.id == ultimoEventoId }
                                if (eventoAntiguo != null) {
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

            is Screen.Detalle -> {
                EventoDetalleScreen(
                    evento = screen.evento,
                    onBack = { currentScreen = Screen.Home },
                    onComprarClick = {
                        currentScreen = Screen.Asientos(screen.evento)
                    }
                )
            }

            // 👇 PANTALLA DE SELECCIÓN DE ASIENTOS
            is Screen.Asientos -> {
                AsientosScreen(
                    evento = screen.evento,
                    token = token ?: "",
                    onBack = {
                        currentScreen = Screen.Detalle(screen.evento)
                    },
                    onContinuar = { listaAsientos ->
                        // Al dar click en "Continuar", vamos al Detalle de Venta
                        currentScreen = Screen.DetalleVenta(screen.evento, listaAsientos)
                    }
                )
            }

            // 👇 NUEVA PANTALLA: RESUMEN Y PAGO FINAL
            is Screen.DetalleVenta -> {
                DetalleVentaScreen(
                    evento = screen.evento,
                    asientosSeleccionados = screen.asientos,
                    token = token ?: "",
                    onBack = {
                        // Si vuelve atrás, regresa al mapa de asientos
                        currentScreen = Screen.Asientos(screen.evento)
                    },
                    onCompraExitosa = {
                        // ¡Éxito! Volvemos al Home para comprar otra cosa
                        currentScreen = Screen.Home
                    }
                )
            }
        }
    }
}