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
//
// The extrusion "thickness" is a fraction of the pawn's own rendered size, not a fixed dp value —
// a fixed offset only looks cube-like at the one size it was tuned against (24dp originally);
// at any other Modifier.size, a constant dp offset is a wildly different proportion of the whole
// shape (e.g. 6dp is 25% of 24dp but 37.5% of 16dp), so the silhouette stops reading as a cube.
//
// The hexagon's own face sits toward the top-right of its tight bounding box, not centered in it
// (that's the whole point — it's what makes it read as a receding cube). Left uncorrected, that
// means the composable's reported size (whatever Modifier.size gives it) and its *visual* center
// don't coincide, so any generic "center this on a point" placement (e.g. BoardgameScale centering
// a thumb on a round) ends up looking like the pawn is floating up-right of where it should sit.
// To fix that without changing the public sizing contract (Modifier.size(X) should still mean
// "occupies exactly X×X"), the hexagon is drawn slightly smaller than the full box — using
// innerSize = X / (1 + fraction) as its own tight bounds — and shifted down by the freed-up space,
// leaving blank margin only on the top and right (the two sides the face already leans away from).
// That specific margin placement is what makes the face's center land exactly on the box's center.
private val BoardgamePawnElevation = 10.dp
private const val BoardgamePawnExtrusionFraction = 0.25f
private const val BoardgamePawnRotationDegrees = -15f

private class BoardgamePawnVertices(totalSize: Float, extrusionFraction: Float) {
    private val innerSize = totalSize / (1f + extrusionFraction)
    private val offsetPx = innerSize * extrusionFraction
    private val faceSize = innerSize - offsetPx
    private val yShift = offsetPx

    val frontTopLeft = Offset(offsetPx, yShift)
    val frontTopRight = Offset(offsetPx + faceSize, yShift)
    val frontBottomRight = Offset(offsetPx + faceSize, faceSize + yShift)
    val frontBottomLeft = Offset(offsetPx, faceSize + yShift)
    val baseBottomRight = Offset(faceSize, offsetPx + faceSize + yShift)
    val baseBottomLeft = Offset(0f, offsetPx + faceSize + yShift)
    val baseTopLeft = Offset(0f, offsetPx + yShift)

    val hexagon: Path
        get() = Path().apply {
            moveTo(frontTopLeft.x, frontTopLeft.y)
            lineTo(frontTopRight.x, frontTopRight.y)
            lineTo(frontBottomRight.x, frontBottomRight.y)
            lineTo(baseBottomRight.x, baseBottomRight.y)
            lineTo(baseBottomLeft.x, baseBottomLeft.y)
            lineTo(baseTopLeft.x, baseTopLeft.y)
            close()
        }
}

private class BoardgamePawnHexagonShape(private val extrusionFraction: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(BoardgamePawnVertices(size.minDimension, extrusionFraction).hexagon)
}

@Composable
fun BoardgamePawn(
    color: Color,
    modifier: Modifier = Modifier.size(16.dp),
) {
    val leftFaceColor = lerp(color, Color.Black, 0.25f)
    val bottomFaceColor = lerp(color, Color.Black, 0.45f)

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .rotate(BoardgamePawnRotationDegrees)
            .shadow(
                elevation = BoardgamePawnElevation,
                shape = BoardgamePawnHexagonShape(BoardgamePawnExtrusionFraction),
            ),
    ) {
        val v = BoardgamePawnVertices(size.minDimension, BoardgamePawnExtrusionFraction)

        val frontPath = Path().apply {
            moveTo(v.frontTopLeft.x, v.frontTopLeft.y)
            lineTo(v.frontTopRight.x, v.frontTopRight.y)
            lineTo(v.frontBottomRight.x, v.frontBottomRight.y)
            lineTo(v.frontBottomLeft.x, v.frontBottomLeft.y)
            close()
        }
        val leftPath = Path().apply {
            moveTo(v.frontTopLeft.x, v.frontTopLeft.y)
            lineTo(v.frontBottomLeft.x, v.frontBottomLeft.y)
            lineTo(v.baseBottomLeft.x, v.baseBottomLeft.y)
            lineTo(v.baseTopLeft.x, v.baseTopLeft.y)
            close()
        }
        val bottomPath = Path().apply {
            moveTo(v.frontBottomLeft.x, v.frontBottomLeft.y)
            lineTo(v.frontBottomRight.x, v.frontBottomRight.y)
            lineTo(v.baseBottomRight.x, v.baseBottomRight.y)
            lineTo(v.baseBottomLeft.x, v.baseBottomLeft.y)
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
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(24.dp))
                BoardgamePawn(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(24.dp))
                BoardgamePawn(
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
    }
}
