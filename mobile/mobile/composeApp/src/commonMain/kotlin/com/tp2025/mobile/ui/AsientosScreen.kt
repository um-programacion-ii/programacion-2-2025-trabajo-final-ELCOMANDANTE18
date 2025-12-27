package com.tp2025.mobile.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Estado local de mis selecciones
    val misAsientos = remember { mutableStateListOf<AsientoVenta>() }
    // Estado de ocupados que vienen del servidor
    var ocupadosRemotos by remember { mutableStateOf<List<String>>(emptyList()) }
    var mostrandoDialogoNombres by remember { mutableStateOf(false) }

    // 1. CARGA INICIAL
    LaunchedEffect(Unit) {
        // Traemos los ocupados reales del Proxy
        ocupadosRemotos = servicio.obtenerOcupados(evento.id)

        val usuario = SessionManager.currentUser ?: "anonimo"
        println("📱 App: Guardando sesión para: $usuario en evento ${evento.id}")
        servicio.guardarVisita(usuario, evento.id)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(evento.titulo, maxLines = 1, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                modifier = Modifier.statusBarsPadding(), // Evita choque arriba
                elevation = 8.dp
            )
        },
        floatingActionButton = {
            if (misAsientos.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    text = { Text("COMPRAR (${misAsientos.size})", fontWeight = FontWeight.Bold) },
                    onClick = { mostrandoDialogoNombres = true },
                    backgroundColor = MaterialTheme.colors.secondary,
                    contentColor = Color.White,
                    icon = { Text("🛒") },
                    modifier = Modifier.navigationBarsPadding() // Evita choque abajo del FAB
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- PANTALLA DE CINE VISUAL ---
            Text("PANTALLA", style = MaterialTheme.typography.overline, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Canvas(modifier = Modifier.width(200.dp).height(15.dp)) {
                val path = Path().apply {
                    moveTo(0f, size.height)
                    quadraticBezierTo(size.width / 2, 0f, size.width, size.height)
                }
                drawPath(
                    path = path,
                    color = Color.DarkGray,
                    style = Stroke(width = 6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- REFERENCIA DE COLORES ---
            ReferenciaEstados()

            Spacer(modifier = Modifier.height(16.dp))

            // --- GRILLA DE ASIENTOS ---
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(filas) { filaIndex ->
                    LazyRow(
                        modifier = Modifier.padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        items(columnas) { colIndex ->
                            val fila = filaIndex + 1
                            val col = colIndex + 1

                            // Lógica de Estado
                            val esMio = misAsientos.any { it.fila == fila && it.columna == col }
                            val esOcupadoRemoto = ocupadosRemotos.contains("$fila-$col")

                            AsientoItem(
                                fila = fila,
                                col = col,
                                eventoId = evento.id,
                                esMio = esMio,
                                esOcupadoRemoto = esOcupadoRemoto,
                                servicio = servicio,
                                scope = scope,
                                onBloqueoExitoso = {
                                    misAsientos.add(AsientoVenta(fila, col))
                                },
                                onError = { msg ->
                                    scope.launch { scaffoldState.snackbarHostState.showSnackbar(msg) }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }

            // --- BOTÓN VOLVER INFERIOR ---
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("VOLVER AL HOME")
            }

            // --- ESPACIADORES PARA BARRA DE NAVEGACIÓN ---
            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }

        // Diálogo fuera del Column principal
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

// 👇 COMPONENTE DE REFERENCIA
@Composable
fun ReferenciaEstados() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemReferencia(color = Color.White, texto = "Libre", tieneBorde = true)
        Spacer(modifier = Modifier.width(16.dp))
        ItemReferencia(color = Color(0xFFE53935), texto = "Ocupado")
        Spacer(modifier = Modifier.width(16.dp))
        ItemReferencia(color = Color(0xFF6200EE), texto = "Tu Lugar")
    }
}

@Composable
fun ItemReferencia(color: Color, texto: String, tieneBorde: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
                .then(
                    if (tieneBorde) Modifier.border(1.dp, Color.Gray, CircleShape) else Modifier
                )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(texto, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun AsientoItem(
    fila: Int, col: Int, eventoId: Long,
    esMio: Boolean,
    esOcupadoRemoto: Boolean,
    servicio: EventoService, scope: kotlinx.coroutines.CoroutineScope,
    onBloqueoExitoso: () -> Unit, onError: (String) -> Unit
) {
    var cargando by remember { mutableStateOf(false) }

    val colorFondo = when {
        cargando -> Color(0xFFFFEE58) // Amarillo
        esMio -> Color(0xFF6200EE)    // Violeta
        esOcupadoRemoto -> Color(0xFFE53935) // Rojo
        else -> Color.White           // Blanco
    }

    val habilitado = !esMio && !esOcupadoRemoto && !cargando
    val colorBorde = if (habilitado) Color.Gray else Color.Transparent
    val colorTexto = if (habilitado) Color.Black else Color.White

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colorFondo)
            .border(1.dp, colorBorde, RoundedCornerShape(8.dp))
            // 👇 AQUÍ ESTÁ LA LÓGICA CON LOGS QUE NECESITAMOS 👇
            .clickable(enabled = habilitado) {
                println("👆 DEBUG: Click en asiento Fila $fila - Col $col")

                cargando = true
                scope.launch {
                    println("⏳ DEBUG: Llamando a bloquearAsiento...")

                    val exito = servicio.bloquearAsiento(eventoId, fila, col)

                    println("🏁 DEBUG: Resultado bloqueo: $exito")

                    cargando = false
                    if (exito) {
                        onBloqueoExitoso()
                    } else {
                        onError("Alguien te ganó el lugar 😞")
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (esMio) Text("✓", color = colorTexto, fontWeight = FontWeight.Bold)
        else if (esOcupadoRemoto) Text("✕", color = colorTexto, fontWeight = FontWeight.Bold)
        else if (cargando) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.Black)
        else Text("$col", style = MaterialTheme.typography.caption, color = colorTexto)
    }
}

@Composable
fun DialogoCargaNombres(asientos: List<AsientoVenta>, onDismiss: () -> Unit, onConfirmar: (List<AsientoVenta>) -> Unit) {
    val asientosEditables = remember { asientos.map { it.copy() } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎟️ Personalizar Entradas") },
        text = {
            Column {
                Text("Ingresa el nombre para cada entrada:", style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(asientosEditables.size) { index ->
                        val asiento = asientosEditables[index]
                        var nombre by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it; asiento.persona = it },
                            label = { Text("Asiento F${asiento.fila}-C${asiento.columna}") },
                            placeholder = { Text("Nombre del asistente") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirmar(asientosEditables) }) { Text("CONFIRMAR COMPRA") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        shape = RoundedCornerShape(16.dp)
    )
}