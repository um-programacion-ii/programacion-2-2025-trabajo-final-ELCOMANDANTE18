package com.tp2025.mobile.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    // CONFIGURACIÓN PARA TU MOTO G8
    // Apunta a tu computadora en la red Wi-Fi
    const val BASE_URL = "http://192.168.194.250:8080/api"
}