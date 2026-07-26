package com.artemonre.hireme.theme

import androidx.compose.ui.text.PlatformTextStyle

// Android renders text through its own native path, not Skia's FontRasterizationSettings.
actual val platformTextStyle: PlatformTextStyle? = null
