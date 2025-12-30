package com.tp2025.mobile.data

import kotlinx.serialization.Serializable

@Serializable
data class AsientoVenta(
    val fila: Int,
    val columna: Int,     // Ojo: En AsientosScreen usamos .columna
    var persona: String? = null // Para guardar el nombre después
)