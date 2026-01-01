package com.tp2025.mobile.domain.model

import kotlinx.serialization.Serializable

// --- EL EVENTO PRINCIPAL ---
@Serializable
data class Evento(
    val id: Long,
    val titulo: String,
    val resumen: String? = null,
    val descripcion: String? = null,
    val fecha: String? = null,
    val imagen: String? = null,
    val precioEntrada: Double? = null,
    val direccion: String? = null,
    val filaAsientos: Int? = null,
    val columnAsientos: Int? = null
)

// --- CLASES PARA EL PROXY (BLOQUEO) ---
@Serializable
data class SolicitudBloqueo(
    val eventoId: Long,
    val fila: Int,
    val columna: Int
)

@Serializable
data class RespuestaBloqueo(
    val resultado: Boolean,
    val mensaje: String? = null
)

// --- CLASES PARA LA VENTA (BACKEND) ---
@Serializable
data class VentaRequest(
    val eventoId: Long,
    val asientos: List<AsientoVenta>, // <--- Aquí usamos AsientoVenta
    val precioVenta: Double,
    val fecha: String? = null
)
