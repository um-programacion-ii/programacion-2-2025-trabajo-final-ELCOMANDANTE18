package com.tp2025.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp2025.mobile.data.Evento
import com.tp2025.mobile.data.EventoService

// 👇 1. AQUI AGREGAMOS EL PARAMETRO 'onEventoClick'
@Composable
fun HomeScreen(token: String, onEventoClick: (Evento) -> Unit) {
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    val eventoService = remember { EventoService() }

    // Cargar eventos al iniciar
    LaunchedEffect(Unit) {
        eventos = eventoService.obtenerEventos(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos Disponibles 🎟️") },
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
        ) {
            items(eventos) { evento ->
                // 👇 2. PASAMOS LA ACCIÓN AL COMPONENTE TARJETA
                TarjetaEvento(
                    evento = evento,
                    onClick = { onEventoClick(evento) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// 👇 3. AGREGAMOS EL PARAMETRO 'onClick' A LA TARJETA
@Composable
fun TarjetaEvento(evento: Evento, onClick: () -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = evento.titulo,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            if (evento.resumen != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = evento.resumen, style = MaterialTheme.typography.body2)
            }

            if (evento.direccion != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "📍 ${evento.direccion}", style = MaterialTheme.typography.caption, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 👇 4. EL BOTÓN QUE DISPARA LA ACCIÓN
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth() // Botón ancho completo opcional
            ) {
                Text("Ver Entradas")
            }
        }
    }
}