package com.tp2025.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tp2025.mobile.data.Evento
import com.tp2025.mobile.data.EventoService

@Composable
fun HomeScreen(
    token: String,
    onEventoClick: (Evento) -> Unit,
    onLogout: () -> Unit
) {
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) } // Estado de carga
    val eventoService = remember { EventoService() }

    // Cargar eventos al iniciar
    LaunchedEffect(Unit) {
        isLoading = true
        eventos = eventoService.obtenerEventos(token)
        isLoading = false
    }

    Scaffold(
        // 👇 ESTO ARREGLA EL CHOQUE CON LA BARRA DE ESTADO
        // Le damos color a la barra superior y padding para que no toque los íconos del sistema
        topBar = {
            TopAppBar(
                title = { Text("Eventos Disponibles", fontWeight = FontWeight.Bold) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                modifier = Modifier.statusBarsPadding(), // <--- ¡LA MAGIA AQUÍ!
                elevation = 8.dp,
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        backgroundColor = Color(0xFFF5F5F5) // Fondo gris claro para resaltar las tarjetas
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            if (isLoading) {
                // Muestra esto mientras carga
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colors.primary
                )
            } else if (eventos.isEmpty()) {
                // Muestra esto si no hay eventos
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.EventBusy,
                        contentDescription = "Sin eventos",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay eventos disponibles", color = Color.Gray)
                }
            } else {
                // Lista de Eventos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp) // Margen interno para la lista
                ) {
                    items(eventos) { evento ->
                        TarjetaEvento(
                            evento = evento,
                            onClick = { onEventoClick(evento) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaEvento(evento: Evento, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp), // Bordes más redondeados
        elevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp) // Más aire interno
        ) {
            // Título y Estado (Opcional)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = evento.titulo,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Resumen
            if (evento.resumen != null) {
                Text(
                    text = evento.resumen,
                    style = MaterialTheme.typography.body2,
                    color = Color.DarkGray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Datos Extra (Ubicación / Fecha)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = evento.direccion ?: "Ubicación a confirmar",
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
            }

            // Fila simulada de Fecha (Si tuvieras fecha en el objeto Evento, úsala aquí)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Próximamente", // O evento.fecha si existiera
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Acción
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Text("VER ENTRADAS", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}