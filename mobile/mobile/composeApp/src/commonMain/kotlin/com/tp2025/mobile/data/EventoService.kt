package com.tp2025.mobile.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.tp2025.mobile.auth.SessionManager // Importante para llamar a la alarma

class EventoService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    // URLs
    private val backendUrl = "http://192.168.100.14:8080/api"
    private val proxyUrl = "http://192.168.100.14:8081/api/proxy"

    // --- 1. LEER EVENTOS (Backend) ---
    suspend fun obtenerEventos(token: String): List<Evento> {
        return try {
            val response = client.get("$backendUrl/eventos") {
                header("Authorization", "Bearer $token")
            }

            // 👇 DETECCIÓN DE TOKEN VENCIDO
            if (response.status == HttpStatusCode.Unauthorized) {
                println("🚨 Token vencido. Cerrando sesión...")
                SessionManager.onSessionExpired?.invoke() // ¡TOCAMOS LA ALARMA!
                return emptyList()
            }

            response.body()
        } catch (e: Exception) {
            println("❌ Error bajando eventos: ${e.message}")
            emptyList()
        }
    }

    // --- 2. BLOQUEAR ASIENTO (Proxy) ---
    suspend fun bloquearAsiento(eventoId: Long, fila: Int, col: Int): Boolean {
        return try {
            val response = client.post("$proxyUrl/bloquear") {
                contentType(ContentType.Application.Json)
                setBody(SolicitudBloqueo(eventoId, fila, col))
            }

            if (response.status == HttpStatusCode.OK) {
                val respuesta = response.body<RespuestaBloqueo>()
                respuesta.resultado
            } else {
                false
            }
        } catch (e: Exception) {
            println("❌ Error Proxy: ${e.message}")
            false
        }
    }

    // --- 3. REALIZAR VENTA (Backend) ---
    suspend fun realizarVenta(
        token: String,
        eventoId: Long,
        asientos: List<AsientoVenta>
    ): Boolean {
        return try {
            val request = VentaRequest(
                eventoId = eventoId,
                asientos = asientos,
                precioVenta = 0.0
            )

            val response = client.post("$backendUrl/realizar-venta") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            // 👇 DETECCIÓN DE TOKEN VENCIDO
            if (response.status == HttpStatusCode.Unauthorized) {
                println("🚨 Token vencido al comprar. Cerrando sesión...")
                SessionManager.onSessionExpired?.invoke() // ¡ALARMA!
                return false
            }

            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("❌ Error Venta: ${e.message}")
            false
        }
    }

    // --- 4. CONSULTAR OCUPADOS (Proxy) ---
    suspend fun obtenerOcupados(eventoId: Long): List<String> {
        return try {
            val response = client.get("$proxyUrl/ocupados/$eventoId")
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("❌ Error consultando ocupados: ${e.message}")
            emptyList()
        }
    }

    // --- 5. SESIONES (Proxy) ---
    suspend fun guardarVisita(usuario: String, eventoId: Long) {
        try {
            client.post("$proxyUrl/sesion/$usuario/visita/$eventoId")
        } catch (e: Exception) {
            println("⚠️ No se pudo guardar sesión: ${e.message}")
        }
    }

    suspend fun recuperarUltimaVisita(usuario: String): Long? {
        return try {
            val response = client.get("$proxyUrl/sesion/$usuario/ultima_visita")
            if (response.status == HttpStatusCode.OK) {
                val body = response.body<String>()
                val numero = Regex("[0-9]+").find(body)?.value
                numero?.toLongOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}