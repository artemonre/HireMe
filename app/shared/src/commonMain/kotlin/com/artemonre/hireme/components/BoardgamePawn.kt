package com.artemonre.hireme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

// A small square "board game pawn" used as an indicator, drawn as a real 3-face cube silhouette:
// a square front face (top-right) plus two receding parallelograms — left and bottom — that share
// full diagonal edges with the front face and with each other, tracing a single gapless hexagon.
// Earlier attempts stacked two copies of the same square shape (read as "two flat layers") or two
// axis-aligned rectangle strips for the sides (left two corners of the bounding box uncovered, and
// straight/axis-aligned strips can't read as a receding surface no matter how they're shaded).
// Slanted quads fix both: no gaps, and the faces actually look like they recede in 3D.
//  - front face: brightest, existing diagonal gradient — faces the viewer/light.
//  - left face: medium-dark solid — a side facing away from the light.
//  - bottom face: darkest solid — the underside, least lit.
// No rounded corners for now — sharp corners keep this to plain polygon math. Rotated slightly as
// a whole (right corner lifted) so it reads as a token dropped onto the board rather than a
// perfectly axis-aligned tile.
private val BoardgamePawnElevation = 10.dp
private val BoardgamePawnExtrusionOffset = 6.dp
private const val BoardgamePawnRotationDegrees = -15f

private fun hexagonPath(size: Size, offsetPx: Float): Path {
    val faceSize = size.minDimension - offsetPx
    val frontTopLeft = Offset(offsetPx, 0f)
    val frontTopRight = Offset(offsetPx + faceSize, 0f)
    val frontBottomRight = Offset(offsetPx + faceSize, faceSize)
    val baseBottomRight = Offset(faceSize, offsetPx + faceSize)
    val baseBottomLeft = Offset(0f, offsetPx + faceSize)
    val baseTopLeft = Offset(0f, offsetPx)

    return Path().apply {
        moveTo(frontTopLeft.x, frontTopLeft.y)
        lineTo(frontTopRight.x, frontTopRight.y)
        lineTo(frontBottomRight.x, frontBottomRight.y)
        lineTo(baseBottomRight.x, baseBottomRight.y)
        lineTo(baseBottomLeft.x, baseBottomLeft.y)
        lineTo(baseTopLeft.x, baseTopLeft.y)
        close()
    }
}

private class BoardgamePawnHexagonShape(private val extrusionOffset: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val offsetPx = with(density) { extrusionOffset.toPx() }
        return Outline.Generic(hexagonPath(size, offsetPx))
    }
}

@Composable
fun BoardgamePawn(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val leftFaceColor = lerp(color, Color.Black, 0.25f)
    val bottomFaceColor = lerp(color, Color.Black, 0.45f)

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .rotate(BoardgamePawnRotationDegrees)
            .shadow(
                elevation = BoardgamePawnElevation,
                shape = BoardgamePawnHexagonShape(BoardgamePawnExtrusionOffset),
            ),
    ) {
        val offsetPx = BoardgamePawnExtrusionOffset.toPx()
        val faceSize = size.minDimension - offsetPx

        val frontTopLeft = Offset(offsetPx, 0f)
        val frontTopRight = Offset(offsetPx + faceSize, 0f)
        val frontBottomRight = Offset(offsetPx + faceSize, faceSize)
        val frontBottomLeft = Offset(offsetPx, faceSize)
        val baseBottomRight = Offset(faceSize, offsetPx + faceSize)
        val baseBottomLeft = Offset(0f, offsetPx + faceSize)
        val baseTopLeft = Offset(0f, offsetPx)

        val frontPath = Path().apply {
            moveTo(frontTopLeft.x, frontTopLeft.y)
            lineTo(frontTopRight.x, frontTopRight.y)
            lineTo(frontBottomRight.x, frontBottomRight.y)
            lineTo(frontBottomLeft.x, frontBottomLeft.y)
            close()
        }
        val leftPath = Path().apply {
            moveTo(frontTopLeft.x, frontTopLeft.y)
            lineTo(frontBottomLeft.x, frontBottomLeft.y)
            lineTo(baseBottomLeft.x, baseBottomLeft.y)
            lineTo(baseTopLeft.x, baseTopLeft.y)
            close()
        }
        val bottomPath = Path().apply {
            moveTo(frontBottomLeft.x, frontBottomLeft.y)
            lineTo(frontBottomRight.x, frontBottomRight.y)
            lineTo(baseBottomRight.x, baseBottomRight.y)
            lineTo(baseBottomLeft.x, baseBottomLeft.y)
            close()
        }

        drawPath(bottomPath, color = bottomFaceColor)
        drawPath(leftPath, color = leftFaceColor)
        drawPath(
            frontPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    lerp(color, Color.White, 0.16f),
                    color,
                    lerp(color, Color.Black, 0.16f),
                ),
            ),
        )
    }
}

@Composable
@Preview
private fun BoardgamePawnPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(modifier = Modifier.padding(24.dp)) {
                BoardgamePawn(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(24.dp))
                BoardgamePawn(
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
