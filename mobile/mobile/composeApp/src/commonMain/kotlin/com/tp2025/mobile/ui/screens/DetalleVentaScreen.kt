package com.tp2025.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tp2025.mobile.domain.model.AsientoVenta
import com.tp2025.mobile.domain.model.Evento
// 👇 Importamos el ViewModel
import com.tp2025.mobile.ui.viewmodel.DetalleVentaViewModel

@Composable
fun DetalleVentaScreen(
    evento: Evento,
    asientosSeleccionados: List<AsientoVenta>,
    token: String,
    onBack: () -> Unit,
    onCompraExitosa: () -> Unit
) {
    // 1. Instanciamos el ViewModel pasando los datos iniciales
    val viewModel = remember { DetalleVentaViewModel(asientosSeleccionados) }

    // Scrolleo para responsive
    val scrollState = rememberScrollState()

    val totalPagar = (evento.precioEntrada ?: 0.0) * viewModel.listaTickets.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Compra") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                },
                backgroundColor = Color(0xFF6200EE),
                contentColor = Color.White,
                modifier = Modifier.statusBarsPadding(),
                elevation = 4.dp
            )
        },
        bottomBar = {
            Surface(
                elevation = 16.dp,
                color = Color.White,
                modifier = Modifier.navigationBarsPadding().imePadding()
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
                            // 2. Delegamos la acción al ViewModel
                            viewModel.confirmarCompra(token, evento.id)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE)),
                        // 3. Observamos estado 'isBuying' del VM
                        enabled = !viewModel.isBuying
                    ) {
                        if (viewModel.isBuying) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("CONFIRMAR PAGO", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(scrollState) // Scroll para teclado
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

            // Usamos itemsIndexed manual porque estamos dentro de un Column con scroll
            viewModel.listaTickets.forEachIndexed { index, ticket ->
                Card(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color.White,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                        OutlinedTextField(
                            value = ticket.persona ?: "",
                            onValueChange = { nuevoNombre ->
                                // 4. Actualizamos el VM
                                viewModel.actualizarNombre(index, nuevoNombre)
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

            // Espacio extra al final
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // 5. Diálogo observado desde el ViewModel
    if (viewModel.showSuccessDialog) {
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