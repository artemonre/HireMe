package com.artemonre.hireme.portfolio

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.artemonre.hireme.components.BoardgameModalExtrusionEdges
import com.artemonre.hireme.components.BoardgameModalSurface

private val PortfolioModalShape = RoundedCornerShape(16.dp)
private const val PortfolioModalMaxHeightFraction = 0.8f

@Composable
actual fun PortfolioModal(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        // Capped below the full window height so the dialog reads as a floating card rather than
        // a full-screen takeover. Content taller than that relies on the sheet's own scrolling
        // (e.g. AllSkillsSheet's verticalScroll) — it gets clipped to the shape otherwise.
        val maxHeight = with(LocalDensity.current) {
            (LocalWindowInfo.current.containerSize.height * PortfolioModalMaxHeightFraction).toDp()
        }

        BoardgameModalSurface(
            shape = PortfolioModalShape,
            backgroundColor = MaterialTheme.colorScheme.surface,
            edges = BoardgameModalExtrusionEdges.LeftAndBottom,
            modifier = Modifier.widthIn(max = 480.dp).heightIn(max = maxHeight),
        ) {
            content()
        }
    }
}
