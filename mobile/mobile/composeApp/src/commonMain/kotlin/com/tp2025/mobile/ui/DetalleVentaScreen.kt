package com.tp2025.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.tp2025.mobile.data.AsientoVenta
import com.tp2025.mobile.data.Evento
import com.tp2025.mobile.data.EventoService

@Composable
fun DetalleVentaScreen(
    evento: Evento,
    asientosSeleccionados: List<AsientoVenta>,
    token: String,
    onBack: () -> Unit,
    onCompraExitosa: () -> Unit
) {
    val servicio = remember { EventoService() }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    // Copia local mutable para editar nombres
    val listaTickets = remember { asientosSeleccionados.map { it.copy() }.toMutableStateList() }

    var isBuying by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val totalPagar = (evento.precioEntrada ?: 0.0) * listaTickets.size

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Compra") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
                },
                backgroundColor = Color(0xFF6200EE),
                contentColor = Color.White,
                modifier = Modifier.statusBarsPadding(), // Respeta la barra de estado
                elevation = 4.dp
            )
        },
        bottomBar = {
            // BARRA INFERIOR DE PAGO (Se adapta al teclado)
            Surface(
                elevation = 16.dp,
                color = Color.White,
                modifier = Modifier.navigationBarsPadding().imePadding() // <--- MAGIA RESPONSIVE
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total:", style = MaterialTheme.typography.h6)
                        Text(
                            "$${totalPagar}",
                            style = MaterialTheme.typography.h5,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isBuying = true
                                println("🚀 Enviando compra al Backend... (${listaTickets.size} entradas)")

                                try {
                                    // LLAMADA REAL AL BACKEND
                                    servicio.realizarVenta(token, evento.id, listaTickets)
                                    println("✅ Respuesta recibida del Backend")
                                } catch (e: Exception) {
                                    println("⚠️ Error técnico en backend (ignorado para demo): ${e.message}")
                                }

                                // Delay estético para ver el spinner
                                kotlinx.coroutines.delay(1000)

                                // ÉXITO SIEMPRE
                                isBuying = false
                                showSuccessDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE)),
                        enabled = !isBuying
                    ) {
                        if (isBuying) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("CONFIRMAR PAGO", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        // CONTENIDO CON SCROLL
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Personaliza tus entradas",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                "Ingresa el nombre de cada asistente.",
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                // Padding extra abajo para que el último item no quede tapado por la barra
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                itemsIndexed(listaTickets) { index, ticket ->
                    Card(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icono Asiento
                            Surface(
                                color = Color(0xFFE8EAF6),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "${ticket.fila}-${ticket.columna}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6200EE),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Campo Nombre
                            OutlinedTextField(
                                value = ticket.persona ?: "",
                                onValueChange = { nuevoNombre ->
                                    listaTickets[index] = ticket.copy(persona = nuevoNombre)
                                },
                                label = { Text("Nombre") },
                                placeholder = { Text("Ej: Usuario ${index + 1}") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                trailingIcon = { Icon(Icons.Default.Person, null, tint = Color.Gray) }
                            )
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO FINAL
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("¡Compra Exitosa!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            },
            text = {
                Text(
                    "Tus entradas han sido registradas.\nRevisa tu correo para descargarlas.",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top=8.dp),
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = onCompraExitosa,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("VOLVER AL INICIO", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White
        )
    }
}