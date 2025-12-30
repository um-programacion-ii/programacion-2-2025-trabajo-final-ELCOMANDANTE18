package com.tp2025.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.tp2025.mobile.auth.AuthService

@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Unit, // Token, Usuario
    onIrARegistro: () -> Unit // Para navegar al registro
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val authService = remember { AuthService() }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Entradera Móvil 🎫",
                    color = Color(0xFF6200EE),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Bienvenido de nuevo", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // INPUT USUARIO
                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    label = { Text("Usuario") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // INPUT PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // MENSAJE DE ERROR (SI HAY)
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN INGRESAR (LÓGICA CORREGIDA AQUÍ)
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null

                            try {
                                // 1. Obtenemos el resultado (Success o Failure)
                                val resultado = authService.login(usuario, password)

                                // 2. Manejamos el éxito
                                resultado.onSuccess { token ->
                                    onLoginSuccess(token, usuario)
                                }

                                // 3. Manejamos el error
                                resultado.onFailure { error ->
                                    errorMessage = "Error: ${error.message ?: "Credenciales inválidas"}"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error inesperado: ${e.message}"
                            }

                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("INGRESAR")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN IR A REGISTRO
                TextButton(onClick = onIrARegistro) {
                    Text("¿No tienes cuenta? Regístrate aquí")
                }
            }
        }
    }
}