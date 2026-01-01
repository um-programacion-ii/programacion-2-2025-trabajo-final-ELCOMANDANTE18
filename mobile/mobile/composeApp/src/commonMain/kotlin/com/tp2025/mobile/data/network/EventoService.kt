package com.tp2025.mobile.data.network

import com.tp2025.mobile.data.local.SessionManager
import com.tp2025.mobile.domain.model.AsientoVenta
import com.tp2025.mobile.domain.model.Evento
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class EventoService {

    private val client = HttpClient {
        // CORRECCIÓN: Se usa 'install' directamente, sin 'HttpClientConfig.'
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    // 📡 IPs REALES DE TU PC (Asegúrate que tu celular esté en la misma red Wi-Fi)
    private val backendUrl = "http://192.168.100.14:8080/api"
    private val proxyUrl = "http://192.168.100.14:8081/api/proxy"

    // --- 1. LEER EVENTOS (REAL) ---
    suspend fun obtenerEventos(token: String): List<Evento> {
        return try {
            val response = client.get("$backendUrl/eventos") {
                header("Authorization", "Bearer $token")
            }

            if (response.status == HttpStatusCode.Unauthorized) {
                println("🚨 Token vencido. Cerrando sesión...")
                SessionManager.onSessionExpired?.invoke()
                return emptyList()
            }

            // Retorna los datos reales de la base de datos
            response.body()
        } catch (e: Exception) {
            println("❌ Error crítico al conectar con Backend ($backendUrl): ${e.message}")
            // En producción devolvemos vacío para no crashear
            emptyList()
        }
    }

    // --- 2. BLOQUEAR ASIENTO (REAL) ---
    suspend fun bloquearAsiento(eventoId: Long, fila: Int, col: Int): Boolean {
        return try {
            val response = client.post("$proxyUrl/bloquear") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("eventoId" to eventoId, "fila" to fila, "col" to col))
            }

            if (response.status == HttpStatusCode.OK) {
                true
            } else {
                println("⚠️ El servidor rechazó el bloqueo: ${response.status}")
                false
            }
        } catch (e: Exception) {
            println("❌ Error de red al bloquear: ${e.message}")
            false
        }
    }

    // --- 3. REALIZAR VENTA (REAL) ---
    suspend fun realizarVenta(
        token: String,
        eventoId: Long,
        asientos: List<AsientoVenta>
    ): Boolean {
        return try {
            println("💰 Enviando venta al Backend...")
            val response = client.post("$backendUrl/ventas") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                // Enviamos el objeto de venta completo
                setBody(mapOf(
                    "eventoId" to eventoId,
                    "asientos" to asientos,
                    // Usamos una fecha fija válida ISO-8601 para evitar problemas de parsing en JHipster por ahora
                    "fecha" to "2025-12-30T10:00:00Z"
                ))
            }

            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                println("✅ Venta registrada correctamente en el Backend.")
                true
            } else {
                println("❌ Error en la venta. Status: ${response.status}")
                false
            }
        } catch (e: Exception) {
            println("❌ Error de conexión al vender: ${e.message}")
            false
        }
    }

    // --- 4. OTROS ---

    suspend fun obtenerOcupados(eventoId: Long): List<String> {
        return try {
            val response = client.get("$proxyUrl/ocupados/$eventoId")
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("⚠️ Error obteniendo ocupados: ${e.message}")
            emptyList()
        }
    }

    suspend fun guardarVisita(usuario: String, eventoId: Long) {
        // Implementación opcional de analíticas
    }

    suspend fun recuperarUltimaVisita(usuario: String): Long? {
        return null
    }
}