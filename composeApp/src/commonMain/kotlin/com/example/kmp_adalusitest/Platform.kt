package com.example.kmp_adalusitest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform