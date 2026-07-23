package com.artemonre.hireme.portfolio

import androidx.compose.ui.unit.dp

// The screen itself applies no outer padding, so every top-level section margins its own
// content by this amount instead. Kept as a single shared value so components stay visually
// aligned with each other, and so full-bleed content (e.g. ProjectsSection's card carousel)
// can size itself relative to the same inset used everywhere else.
val ScreenPadding = 24.dp
