package com.tp2025.mobile.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ProxyRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    // 👇 CORREGIDO: Usamos la IP de tu PC (dark-hole)
    private val baseUrl = "http://192.168.100.14:8081/api/proxy"

    suspend fun obtenerAsientosOcupados(eventoId: Long): List<String> {
        return try {
            println("📱 MOBILE: Consultando Proxy en $baseUrl para evento $eventoId")
            // timeout opcional para que no se quede colgado si la IP está mal
            val respuesta: List<String> = client.get("$baseUrl/ocupados/$eventoId").body()
            println("✅ MOBILE: Recibidos ${respuesta.size} ocupados")
            respuesta
        } catch (e: Exception) {
            println("❌ MOBILE ERROR: No se pudo conectar al Proxy ($baseUrl): ${e.message}")
            emptyList()
        }
    }
}