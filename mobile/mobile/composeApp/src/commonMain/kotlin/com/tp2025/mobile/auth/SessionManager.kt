package com.tp2025.mobile.auth

object SessionManager {
    var jwtToken: String? = null
    var currentUser: String? = null

    // 👇 ESTA ES LA NUEVA ALARMA
    // Es una función vacía que llenaremos desde App.kt
    var onSessionExpired: (() -> Unit)? = null

    fun clear() {
        jwtToken = null
        currentUser = null
    }
}