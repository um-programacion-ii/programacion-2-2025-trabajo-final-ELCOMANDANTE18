package com.tp2025.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tp2025.mobile.auth.SessionManager
import com.tp2025.mobile.data.AsientoVenta
import com.tp2025.mobile.data.Evento
import com.tp2025.mobile.data.EventoService
import kotlinx.coroutines.launch

@Composable
fun AsientosScreen(evento: Evento, onBack: () -> Unit) {
    val filas = evento.filaAsientos ?: 10
    val columnas = evento.columnAsientos ?: 10

    val servicio = remember { EventoService() }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val misAsientos = remember { mutableStateListOf<AsientoVenta>() }

    // Lista de ocupados que vienen del servidor
    var ocupadosRemotos by remember { mutableStateOf<List<String>>(emptyList()) }

    var mostrandoDialogoNombres by remember { mutableStateOf(false) }

    // 1. Cargar ocupados al inicio
    LaunchedEffect(Unit) {
        ocupadosRemotos = servicio.obtenerOcupados(evento.id)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(evento.titulo, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (misAsientos.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    text = { Text("COMPRAR (${misAsientos.size})") },
                    onClick = { mostrandoDialogoNombres = true },
                    backgroundColor = MaterialTheme.colors.primary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Selecciona tu ubicación", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(200.dp).height(6.dp).background(Color.Gray, RoundedCornerShape(2.dp)))
            Text("Pantalla", style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                items(filas) { filaIndex ->
                    LazyRow(modifier = Modifier.padding(bottom = 6.dp)) {
                        items(columnas) { colIndex ->
                            val fila = filaIndex + 1
                            val col = colIndex + 1

                            // 2. Ver si este asiento está en la lista de ocupados
                            val estaOcupado = ocupadosRemotos.contains("$fila-$col")

                            AsientoItem(
                                fila = fila,
                                col = col,
                                eventoId = evento.id,
                                esOcupadoInicial = estaOcupado, // 👈 AQUÍ ARREGLAMOS EL ERROR DE FALTA DE PARÁMETRO
                                servicio = servicio,
                                scope = scope,
                                onBloqueoExitoso = { misAsientos.add(AsientoVenta(fila, col)) },
                                onError = { msg -> scope.launch { scaffoldState.snackbarHostState.showSnackbar(msg) } }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
            ) {
                Text("VOLVER AL HOME")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (mostrandoDialogoNombres) {
            DialogoCargaNombres(
                asientos = misAsientos,
                onDismiss = { mostrandoDialogoNombres = false },
                onConfirmar = { lista ->
                    mostrandoDialogoNombres = false
                    scope.launch {
                        val token = SessionManager.jwtToken ?: ""
                        if (servicio.realizarVenta(token, evento.id, lista)) {
                            scaffoldState.snackbarHostState.showSnackbar("✅ ¡VENTA EXITOSA!")
                            misAsientos.clear()
                            onBack()
                        } else {
                            scaffoldState.snackbarHostState.showSnackbar("❌ Error en la venta.")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun AsientoItem(
    fila: Int, col: Int, eventoId: Long, esOcupadoInicial: Boolean,
    servicio: EventoService, scope: kotlinx.coroutines.CoroutineScope,
    onBloqueoExitoso: () -> Unit, onError: (String) -> Unit
) {
    var estado by remember(esOcupadoInicial) {
        mutableStateOf(if (esOcupadoInicial) 3 else 0)
    }

    val color = when(estado) {
        0 -> Color.LightGray
        1 -> Color(0xFFFFEB3B)
        2 -> Color(0xFF6200EE)
        3 -> Color.Red
        else -> Color.Gray
    }

    Box(
        modifier = Modifier.size(34.dp).background(color, RoundedCornerShape(4.dp)).border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clickable {
                if (estado == 0) {
                    estado = 1
                    scope.launch {
                        if (servicio.bloquearAsiento(eventoId, fila, col)) {
                            estado = 2
                            onBloqueoExitoso()
                        } else {
                            estado = 3
                            onError("Ya está ocupado")
                        }
                    }
                } else if (estado == 3) {
                    onError("Ocupado 🚫")
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (estado == 2) Text("✓", color = Color.White)
        else if (estado == 3) Text("X", color = Color.White)
        else if (estado != 1) Text("$col", style = MaterialTheme.typography.caption)
    }
}

@Composable
fun DialogoCargaNombres(asientos: List<AsientoVenta>, onDismiss: () -> Unit, onConfirmar: (List<AsientoVenta>) -> Unit) {
    val asientosEditables = remember { asientos.map { it.copy() } }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎟️ Completar Entradas") },
        text = {
            Column {
                Text("Ingresa los nombres:")
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(asientosEditables.size) { index ->
                        val asiento = asientosEditables[index]
                        var nombre by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it; asiento.persona = it },
                            label = { Text("F${asiento.fila}-C${asiento.columna}") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirmar(asientosEditables) }) { Text("CONFIRMAR") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}