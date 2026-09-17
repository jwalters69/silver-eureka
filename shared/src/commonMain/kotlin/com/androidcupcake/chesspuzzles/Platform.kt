package com.androidcupcake.chesspuzzles

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform