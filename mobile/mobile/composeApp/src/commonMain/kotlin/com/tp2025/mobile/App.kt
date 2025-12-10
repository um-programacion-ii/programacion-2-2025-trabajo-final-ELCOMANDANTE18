package com.tp2025.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        var username by remember { mutableStateOf("admin") }
        var password by remember { mutableStateOf("admin") }
        var statusMessage by remember { mutableStateOf("Esperando login...") }

        // Scope para ejecutar tareas asíncronas (como peticiones HTTP)
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Entradera Móvil 🎟️", style = MaterialTheme.typography.h4)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        statusMessage = "Intentando conectar..."
                        // ACÁ VA A IR LA LÓGICA DE CONEXIÓN
                        statusMessage = "Botón presionado (Falta conectar lógica)"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("INGRESAR")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(statusMessage)
        }
    }
}