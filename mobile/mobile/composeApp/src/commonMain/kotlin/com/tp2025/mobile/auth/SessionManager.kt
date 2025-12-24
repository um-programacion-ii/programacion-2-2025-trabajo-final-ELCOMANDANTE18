package com.tp2025.mobile.auth

object SessionManager {
    var jwtToken: String? = null

    // 👇 AGREGÁ ESTA VARIABLE NUEVA
    var currentUser: String? = null

    fun clear() {
        jwtToken = null
        currentUser = null
    }
}