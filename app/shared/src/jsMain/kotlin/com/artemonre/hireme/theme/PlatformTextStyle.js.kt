package com.artemonre.hireme.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.FontHinting
import androidx.compose.ui.text.FontRasterizationSettings
import androidx.compose.ui.text.FontSmoothing
import androidx.compose.ui.text.PlatformParagraphStyle
import androidx.compose.ui.text.PlatformTextStyle

// Compose Multiplatform's own Windows default falls back to plain grayscale AntiAlias instead of
// ClearType-style SubpixelAntiAlias (see the upstream FontRasterizationSettings.PlatformDefault —
// SubpixelAntiAlias is deliberately disabled there pending a proper way to read the OS's real
// ClearType setting). Request subpixel smoothing explicitly to match how most other Windows apps
// render text.
@OptIn(ExperimentalTextApi::class)
actual val platformTextStyle: PlatformTextStyle? = PlatformTextStyle(
    spanStyle = null,
    paragraphStyle = PlatformParagraphStyle(
        FontRasterizationSettings(
            smoothing = FontSmoothing.SubpixelAntiAlias,
            hinting = FontHinting.Normal,
            subpixelPositioning = true,
            autoHintingForced = false,
        ),
    ),
)
