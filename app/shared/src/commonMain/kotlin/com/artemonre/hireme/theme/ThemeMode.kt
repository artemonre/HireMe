package com.artemonre.hireme.theme

enum class ThemeMode { SYSTEM, LIGHT, DARK }

fun ThemeMode.isDark(systemDark: Boolean): Boolean = when (this) {
    ThemeMode.SYSTEM -> systemDark
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}
