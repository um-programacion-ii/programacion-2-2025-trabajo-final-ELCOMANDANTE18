package com.tp2025.mobile

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.tp2025.mobile.auth.AuthService // <--- 1. IMPORTANTE: Importar tu servicio
import com.tp2025.mobile.auth.SessionManager

@Composable
fun App() {
    MaterialTheme {
        var username by remember { mutableStateOf("admin") }
        var password by remember { mutableStateOf("admin") } // JHipster usa 'admin' por defecto
        var statusMessage by remember { mutableStateOf("Esperando login...") }

        // 2. Instanciamos el servicio (el "teléfono" para llamar al backend)
        val authService = remember { AuthService() }

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
                        statusMessage = "🔄 Conectando con JHipster..."

                        // 1. Llamamos al Backend
                        val resultado = authService.login(username, password)

                        resultado.onSuccess { token ->
                            // 2. ¡GUARDAMOS EL TOKEN EN EL BOLSILLO!
                            SessionManager.jwtToken = token // <--- ESTA ES LA LÍNEA NUEVA

                            // 3. Avisamos al usuario
                            statusMessage = "✅ Login Correcto. Token guardado en memoria."
                            println("🔐 Token guardado: ${SessionManager.jwtToken?.take(10)}...")

                            // (Opcional) Aquí navegaríamos a la pantalla de Eventos
                            // navigator.push(EventosScreen())

                        }.onFailure { error ->
                            statusMessage = "❌ Error: ${error.message}"
                        }
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