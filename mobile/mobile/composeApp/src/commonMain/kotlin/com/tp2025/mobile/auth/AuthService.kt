package com.tp2025.mobile.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AuthService {
    // 1. Configuramos el cliente HTTP (el "navegador" de la app)
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Si el backend manda cosas extra, no explota
                prettyPrint = true
            })
        }
    }

    // 2. Definimos la URL.
    // IMPORTANTE: En el emulador Android, "localhost" es "10.0.2.2".
    // Si usas celular físico, aquí iría la IP de tu PC (ej: 192.168.1.XX).
    // private val baseUrl = "http://10.0.2.2:8080/api" // <--- Comenta esta (Android)
    private val baseUrl = "http://192.168.100.14:8080/api"  // <--- Usa esta para Desktop

    // 3. La función de Login
    suspend fun login(usuario: String, contrasenia: String): Result<String> {
        return try {
            val response = client.post("$baseUrl/authenticate") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username = usuario, password = contrasenia))
            }

            if (response.status == HttpStatusCode.OK) {
                val data = response.body<TokenResponse>()
                // ¡Éxito! Devolvemos el Token limpio
                Result.success(data.id_token)
            } else {
                Result.failure(Exception("Error Login: ${response.status}"))
            }
        } catch (e: Exception) {
            println("Error de conexión: ${e.message}")
            Result.failure(e)
        }
    }
}