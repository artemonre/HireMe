package com.artemonre.hireme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

// Chrome styled like the app's other "board game" pieces (BoardgameChip, BoardgameCardVertical):
// a darker side face peeks out from behind the front face to suggest physical thickness, with a
// drop shadow and a diagonal light-to-dark gradient across the front completing the effect. Used
// both for PortfolioModal's chrome and for the desktop-wide app frame (see PortfolioScreen).
//
// Those other pieces get the peek by offsetting the side face *outside* the front face's own
// bounds, relying on an unclipped ancestor to let it show. That trick doesn't work here: every host
// this is used from (ModalBottomSheet's Surface, Dialog's Surface, the app's own root Box) either
// clips its content to its own bounds or has no unused space beyond them for a child to overflow
// into. Instead, the front face is inset from the shared box by the extrusion offset on whichever
// edges recede, leaving the darker side layer — sized to the full, uninset box — peeking out of the
// freed-up space rather than outside the box altogether.
enum class BoardgameExtrusionEdges { Left, LeftAndBottom, LeftAndRight }

private val BoardgameSurfaceElevation = 8.dp
private val BoardgameSurfaceExtrusionOffset = 3.dp

@Composable
fun BoardgameTablet(
    shape: Shape,
    backgroundColor: Color,
    edges: BoardgameExtrusionEdges,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val sideColor = lerp(backgroundColor, Color.Black, 0.35f)

    val frontInsetModifier = when (edges) {
        BoardgameExtrusionEdges.Left ->
            Modifier.padding(start = BoardgameSurfaceExtrusionOffset)
        BoardgameExtrusionEdges.LeftAndBottom ->
            Modifier.padding(start = BoardgameSurfaceExtrusionOffset, bottom = BoardgameSurfaceExtrusionOffset)
        BoardgameExtrusionEdges.LeftAndRight ->
            Modifier.padding(horizontal = BoardgameSurfaceExtrusionOffset)
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(elevation = BoardgameSurfaceElevation, shape = shape)
                .clip(shape)
                .background(sideColor),
        )

        Box(
            modifier = frontInsetModifier
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            lerp(backgroundColor, Color.White, 0.16f),
                            backgroundColor,
                            lerp(backgroundColor, Color.Black, 0.16f),
                        ),
                    ),
                )
        ) {
            content()
        }
    }
}

@Composable
@Preview
private fun BoardgameTabletLeftPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BoardgameTablet(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                backgroundColor = MaterialTheme.colorScheme.surface,
                edges = BoardgameExtrusionEdges.Left,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "Bottom sheet content", modifier = Modifier.padding(24.dp))
            }
        }
    }
}

@Composable
@Preview
private fun BoardgameTabletLeftAndBottomPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BoardgameTablet(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colorScheme.surface,
                edges = BoardgameExtrusionEdges.LeftAndBottom,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "Dialog content", modifier = Modifier.padding(24.dp))
            }
        }
    }
}

@Composable
@Preview
private fun BoardgameTabletLeftAndRightPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceDim) {
            BoardgameTablet(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colorScheme.surfaceBright,
                edges = BoardgameExtrusionEdges.LeftAndRight,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "App frame content", modifier = Modifier.padding(24.dp))
            }
        }
    }
}
