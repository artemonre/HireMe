package com.artemonre.hireme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.artemonre.hireme.navigation.AppNavigation
import com.artemonre.hireme.theme.HireMeTheme
import com.artemonre.hireme.theme.ThemeMode
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    var revealCenter by remember { mutableStateOf(Offset.Zero) }
    var avatarRadiusPx by remember { mutableStateOf(0f) }
    val revealProgress = remember { Animatable(1f) }
    var oldSnapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()

    fun changeTheme(mode: ThemeMode) {
        scope.launch {
            // Capture the current (still old-themed) frame before the state change below
            // causes the content to recompose with the new theme's colors.
            oldSnapshot = graphicsLayer.toImageBitmap()
            themeMode = mode
        }
    }

    HireMeTheme(darkTheme = darkTheme) {
        val backgroundColor = MaterialTheme.colorScheme.background

        // Keyed on the snapshot itself (not on darkTheme) so a mode change that doesn't
        // actually flip light/dark still clears the snapshot instead of leaving it stuck.
        LaunchedEffect(oldSnapshot) {
            if (oldSnapshot != null) {
                revealProgress.snapTo(0f)
                revealProgress.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
                oldSnapshot = null
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor,
        ) {
            Box(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawContent()
                        },
                ) {
                    AppNavigation(
                        themeMode = themeMode,
                        onThemeModeChange = ::changeTheme,
                        onAvatarPositioned = { center, radiusPx ->
                            revealCenter = center
                            avatarRadiusPx = radiusPx
                        },
                    )
                }

                val snapshot = oldSnapshot
                if (snapshot != null) {
                    Canvas(Modifier.fillMaxSize()) {
                        val maxRadius = listOf(
                            Offset(0f, 0f),
                            Offset(size.width, 0f),
                            Offset(0f, size.height),
                            Offset(size.width, size.height),
                        ).maxOf { corner -> (corner - revealCenter).getDistance() }

                        val currentRadius = avatarRadiusPx +
                            revealProgress.value * (maxRadius - avatarRadiusPx)

                        val path = Path().apply {
                            fillType = PathFillType.EvenOdd
                            addRect(Rect(Offset.Zero, size))
                            addOval(Rect(center = revealCenter, radius = currentRadius))
                        }
                        clipPath(path) {
                            drawImage(snapshot, dstSize = IntSize(size.width.toInt(), size.height.toInt()))
                        }
                    }
                }
            }
        }
    }
}
