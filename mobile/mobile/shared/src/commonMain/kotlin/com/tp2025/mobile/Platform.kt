package com.tp2025.mobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform