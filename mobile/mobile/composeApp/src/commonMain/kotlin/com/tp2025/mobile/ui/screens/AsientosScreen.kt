package com.tp2025.mobile.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.tp2025.mobile.domain.model.AsientoVenta
import com.tp2025.mobile.domain.model.Evento

// 👇 Importamos el ViewModel (Asegúrate de haber creado el archivo del paso anterior)
import com.tp2025.mobile.ui.viewmodel.AsientosViewModel

@Composable
fun AsientosScreen(
    evento: Evento,
    token: String,
    onBack: () -> Unit,
    onContinuar: (List<AsientoVenta>) -> Unit
) {
    // 1. Instanciamos el ViewModel
    // En KMP puro usamos remember { ... } para mantener vivo el VM mientras la pantalla viva.
    val viewModel = remember { AsientosViewModel() }

    // 2. Delegamos la carga de datos al ViewModel
    LaunchedEffect(Unit) {
        viewModel.cargarOcupados(evento.id)
    }

    val filas = evento.filaAsientos ?: 10
    val columnas = evento.columnAsientos ?: 10
    val scaffoldState = rememberScaffoldState()

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
                modifier = Modifier.statusBarsPadding(),
                elevation = 0.dp
            )
        },
        bottomBar = {
            // 3. La UI observa el estado del ViewModel (misAsientos)
            if (viewModel.misAsientos.isNotEmpty()) {
                Surface(elevation = 16.dp, color = Color.White) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total estimado:", style = MaterialTheme.typography.caption)
                                val precio = evento.precioEntrada ?: 0.0
                                // Calculamos total usando la lista del VM
                                val total = precio * viewModel.misAsientos.size
                                Text("$${total}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF4CAF50))
                            }

                            Button(
                                onClick = { onContinuar(viewModel.misAsientos) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("CONTINUAR", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
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
            Text("ESCENARIO", style = MaterialTheme.typography.overline, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Canvas(modifier = Modifier.width(200.dp).height(15.dp)) {
                drawPath(
                    path = Path().apply { moveTo(0f, size.height); quadraticBezierTo(size.width/2, 0f, size.width, size.height) },
                    color = Color.DarkGray,
                    style = Stroke(width = 6f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ReferenciaEstados()
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp)) {
                items(filas) { filaIndex ->
                    LazyRow(modifier = Modifier.padding(bottom = 8.dp), horizontalArrangement = Arrangement.Center) {
                        items(columnas) { colIndex ->
                            val fila = filaIndex + 1
                            val col = colIndex + 1

                            // 4. Preguntamos al VM el estado de cada asiento
                            val esMio = viewModel.misAsientos.any { it.fila == fila && it.columna == col }
                            val esOcupado = viewModel.esOcupadoRemoto(fila, col)

                            AsientoItem(
                                col = col,
                                esMio = esMio,
                                esOcupadoRemoto = esOcupado,
                                onToggle = {
                                    // 5. Enviamos la acción al VM
                                    viewModel.toggleAsiento(fila, col)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- Componentes Visuales Puros (No cambian) ---

@Composable
fun ReferenciaEstados() {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        ItemReferencia(Color.White, "Libre", true); Spacer(Modifier.width(16.dp))
        ItemReferencia(Color(0xFFE53935), "Ocupado"); Spacer(Modifier.width(16.dp))
        ItemReferencia(Color(0xFF6200EE), "Tu Lugar")
    }
}

@Composable
fun ItemReferencia(color: Color, texto: String, borde: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(16.dp).clip(CircleShape).background(color).then(if(borde) Modifier.border(1.dp, Color.Gray, CircleShape) else Modifier))
        Spacer(Modifier.width(6.dp))
        Text(texto, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun AsientoItem(
    col: Int,
    esMio: Boolean,
    esOcupadoRemoto: Boolean,
    onToggle: () -> Unit
) {
    val color = when {
        esMio -> Color(0xFF6200EE)
        esOcupadoRemoto -> Color(0xFFE53935)
        else -> Color.White
    }
    val habilitado = !esOcupadoRemoto

    Box(
        Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .border(1.dp, if(habilitado && !esMio) Color.Gray else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(enabled = habilitado) { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        if(esMio) Text("✓", color=Color.White, fontWeight=FontWeight.Bold)
        else if(esOcupadoRemoto) Text("✕", color=Color.White, fontWeight=FontWeight.Bold)
        else Text("$col", style=MaterialTheme.typography.caption)
    }
}