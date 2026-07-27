package com.artemonre.hireme.portfolio

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.components.BoardgameExtrusionEdges
import com.artemonre.hireme.components.BoardgameSurface

private val PortfolioModalShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PortfolioModal(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    // The sheet's own Surface (shape/color/drag handle) is replaced by BoardgameSurface below,
    // so it's made transparent and unclipped here rather than doubling up on chrome.
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RectangleShape,
        containerColor = Color.Transparent,
        dragHandle = null,
    ) {
        BoardgameSurface(
            shape = PortfolioModalShape,
            backgroundColor = MaterialTheme.colorScheme.surface,
            edges = BoardgameExtrusionEdges.Left,
            modifier = Modifier.fillMaxWidth(),
        ) {
            content()
        }
    }
}
