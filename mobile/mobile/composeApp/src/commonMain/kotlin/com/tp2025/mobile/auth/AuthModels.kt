package com.tp2025.mobile.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val rememberMe: Boolean = false
)

@Serializable
data class TokenResponse(
    val id_token: String
)