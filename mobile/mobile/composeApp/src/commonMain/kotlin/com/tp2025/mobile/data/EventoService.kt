package com.tp2025.mobile.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

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

    // --- LEER EVENTOS ---
    suspend fun obtenerEventos(token: String): List<Evento> {
        return try {
            val response = client.get("$backendUrl/eventos") {
                header("Authorization", "Bearer $token")
            }
            response.body()
        } catch (e: Exception) {
            println("❌ Error bajando eventos: ${e.message}")
            emptyList()
        }
    }

    // --- BLOQUEAR ASIENTO (PROXY) ---
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

    // --- REALIZAR VENTA (BACKEND) ---
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

            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("❌ Error Venta: ${e.message}")
            false
        }
    }

    // 👇 ESTA ES LA FUNCIÓN QUE TE FALTABA Y DABA ERROR
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
}