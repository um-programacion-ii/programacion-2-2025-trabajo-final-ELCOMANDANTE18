package com.tp2025.mobile.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.tp2025.mobile.auth.SessionManager

class EventoService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    // 📡 IPs REALES DE TU PC (Para Celular Físico)
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
            // En producción no inventamos datos, devolvemos vacío para no confundir al usuario
            emptyList()
        }
    }

    // --- 2. BLOQUEAR ASIENTO (REAL) ---
    suspend fun bloquearAsiento(eventoId: Long, fila: Int, col: Int): Boolean {
        return try {
            // Llamada real al endpoint
            // Nota: Asegúrate de tener la data class SolicitudBloqueo creada
            val response = client.post("$proxyUrl/bloquear") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("eventoId" to eventoId, "fila" to fila, "col" to col))
            }

            if (response.status == HttpStatusCode.OK) {
                // Si el server responde OK, el asiento es tuyo
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
                    "fecha" to "2025-12-27T10:00:00Z" // O la fecha actual
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

    // Este método sigue siendo útil si quieres llamar al Proxy directamente sin usar el Repository
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