package com.tp2025.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.HttpStatusCode
import io.ktor.client.statement.HttpResponse

@Composable
fun RegistroScreen(
    onBack: () -> Unit,
    onRegistroSuccess: () -> Unit
) {
    // ESTADOS DEL FORMULARIO
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ESTADOS UI
    var isLoading by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // ESTADO PARA EL SCROLL
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        // CAJA PRINCIPAL CON SCROLL Y PROTECCIÓN DE TECLADO
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding() // <--- MAGIA 1: Empuja el contenido si sale el teclado
                .verticalScroll(scrollState) // <--- MAGIA 2: Permite hacer scroll si la pantalla es chica
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Intenta centrar, pero si no cabe, el scroll se activa
        ) {

            // LOGO O TÍTULO EXTRA (Opcional, para llenar espacio visual)
            Text(text = "Entradera Móvil", style = MaterialTheme.typography.h5, color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Completa tus datos", style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(16.dp))

            // INPUT: NOMBRE (LOGIN)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Usuario (Login)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            // INPUT: EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            // INPUT: PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text(
                "Mínimo 4 caracteres (Mayúscula + minúscula + número)",
                style = MaterialTheme.typography.caption,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp).align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            mensajeError = null
                            try {
                                val exito = registrarUsuario(nombre, email, password)
                                if (exito) {
                                    onRegistroSuccess()
                                } else {
                                    mensajeError = "Error: Usuario ya existe o contraseña débil."
                                }
                            } catch (e: Exception) {
                                mensajeError = "Error de red: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = nombre.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
                ) {
                    Text("REGISTRARSE")
                }
            }

            if (mensajeError != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = mensajeError!!, color = Color.Red)
            }

            // ESPACIO EXTRA AL FINAL PARA QUE EL SCROLL NO QUEDE APRETADO
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// LOGICA DE RED (Mantenemos la que corregimos para JHipster)
suspend fun registrarUsuario(nombre: String, email: String, pass: String): Boolean {
    val client = HttpClient()
    return try {
        val response: HttpResponse = client.post("http://192.168.100.14:8080/api/register") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "login": "$nombre",
                    "email": "$email",
                    "password": "$pass",
                    "langKey": "es"
                }
            """.trimIndent())
        }
        response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK
    } catch (e: Exception) {
        println("Error registro: ${e.message}")
        false
    } finally {
        client.close()
    }
}