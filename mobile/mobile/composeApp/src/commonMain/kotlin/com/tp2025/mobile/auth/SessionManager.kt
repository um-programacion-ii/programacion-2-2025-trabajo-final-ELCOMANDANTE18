package com.tp2025.mobile.auth

/**
 * Esta clase es el "bolsillo" de la App.
 * Guarda el token en memoria para usarlo en otras llamadas.
 */
object SessionManager {
    var jwtToken: String? = null

    // Función para saber si estamos logueados
    fun isLoggedIn(): Boolean {
        return !jwtToken.isNullOrBlank()
    }

    // Función para limpiar sesión (Logout)
    fun clear() {
        jwtToken = null
    }
}