package com.artemonre.hireme

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform