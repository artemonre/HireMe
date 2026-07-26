package com.artemonre.hireme.theme

import androidx.compose.ui.text.PlatformTextStyle

// Skia-based (Desktop, Web) targets can request finer control over font rasterization than
// Android/iOS expose here; those platforms just get null (their own native text rendering).
expect val platformTextStyle: PlatformTextStyle?
