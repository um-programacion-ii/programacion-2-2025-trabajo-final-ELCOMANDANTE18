package com.tp2025.mobile.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.tp2025.mobile.auth.SessionManager
import kotlinx.coroutines.delay

class EventoService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    // URLs (No importan ahora porque estamos simulando, pero las dejamos listas)
    private val backendUrl = "http://192.168.100.14:8080/api"
    private val proxyUrl = "http://192.168.100.14:8081/api/proxy"

    // --- 1. LEER EVENTOS ---
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

            response.body()
        } catch (e: Exception) {
            // MOCK DE EMERGENCIA: Datos falsos locales corregidos (Strings con comillas)
            println("⚠️ Backend no disponible, usando datos falsos locales.")
            listOf(
                Evento(
                    id = 1,
                    titulo = "Final Liga Mendocina",
                    resumen = "Estadio Malvinas",
                    descripcion = "Gran final de basket regional",
                    fecha = "20/10/2025",   // <--- CORREGIDO: Ahora es String
                    imagen = "",            // <--- CORREGIDO: Ahora es String
                    filaAsientos = 10,
                    columnAsientos = 6
                ),
                Evento(
                    id = 2,
                    titulo = "Conferencia Tech",
                    resumen = "Auditorio Ángel Bustelo",
                    descripcion = "Charlas sobre Kotlin y Java",
                    fecha = "15/11/2025",   // <--- CORREGIDO
                    imagen = "",            // <--- CORREGIDO
                    filaAsientos = 8,
                    columnAsientos = 8
                )
            )
        }
    }

    // --- 2. BLOQUEAR ASIENTO (MODO SIMULACIÓN ACTIVADO 🛠️) ---
    suspend fun bloquearAsiento(eventoId: Long, fila: Int, col: Int): Boolean {
        // 👇 1. COMENTAMOS LA LLAMADA REAL PARA QUE NO FALLE
        /*
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
        */

        // 👇 2. SIMULACIÓN DE ÉXITO (Para probar UI local y ver color VIOLETA)
        println("🔧 SIMULACIÓN: Bloqueando Fila $fila - Col $col localmente...")
        delay(300) // Simulamos un pequeño tiempo de espera
        return true // Siempre decimos que SÍ para que se pinte violeta
    }

    // --- 3. CONSULTAR OCUPADOS ---
    suspend fun obtenerOcupados(eventoId: Long): List<String> {
        return try {
            val response = client.get("$proxyUrl/ocupados/$eventoId")
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("⚠️ No se pudo conectar al Proxy, asumiendo 0 ocupados.")
            emptyList()
        }
    }

    // --- 4. REALIZAR VENTA ---
    suspend fun realizarVenta(
        token: String,
        eventoId: Long,
        asientos: List<AsientoVenta>
    ): Boolean {
        println("💰 Simulando venta de ${asientos.size} entradas...")
        delay(1000) // Simulamos proceso de venta
        return true // Decimos que salió bien
    }

    // --- 5. SESIONES ---
    suspend fun guardarVisita(usuario: String, eventoId: Long) {
        // No hacemos nada en simulación para no romper
    }

    suspend fun recuperarUltimaVisita(usuario: String): Long? {
        // Retornamos null para ir siempre al Home en simulación
        return null
    }
}