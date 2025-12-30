package com.tp2025.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tp2025.mobile.data.Evento

@Composable
fun EventoDetalleScreen(
    evento: Evento,
    onBack: () -> Unit,
    onComprarClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                // ✅ CORRECCIÓN AQUÍ: Bajamos la barra para que no choque con la hora/batería
                modifier = Modifier.statusBarsPadding(),
                title = { Text(text = "Detalle del Evento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                backgroundColor = Color(0xFF6200EE),
                contentColor = Color.White,
                elevation = 0.dp
            )
        },
        bottomBar = {
            Surface(
                elevation = 16.dp,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding() // Protege contra la barra de abajo
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onComprarClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
                    ) {
                        Text(
                            text = "VER ASIENTOS DISPONIBLES",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color(0xFFF5F5F5))
        ) {
            // 1. HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color(0xFF6200EE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.fillMaxSize(0.4f)
                )
            }

            // 2. CONTENIDO
            Column(
                modifier = Modifier
                    .offset(y = (-20).dp)
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Etiqueta
                Surface(
                    color = Color(0xFFEEEEEE),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "CONFERENCIA",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.overline,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Título
                Text(
                    text = evento.titulo ?: "Evento sin título",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Precio
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFE8F5E9)) {
                        Icon(Icons.Default.AttachMoney, null, tint = Color(0xFF2E7D32), modifier = Modifier.padding(6.dp).size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Precio Entrada", style = MaterialTheme.typography.caption, color = Color.Gray)
                        Text(
                            text = "$${evento.precioEntrada ?: 0}",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))

                // Ubicación
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.padding(top = 4.dp).size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Ubicación", style = MaterialTheme.typography.caption, color = Color.Gray)
                        Text(
                            text = evento.direccion ?: "A confirmar",
                            style = MaterialTheme.typography.body1,
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Descripción
                Text("Acerca del evento", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = evento.descripcion ?: evento.resumen ?: "Sin detalles.",
                    style = MaterialTheme.typography.body2,
                    lineHeight = 22.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}