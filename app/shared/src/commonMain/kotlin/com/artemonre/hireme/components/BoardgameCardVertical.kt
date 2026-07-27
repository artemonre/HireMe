package com.artemonre.hireme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

// A vertical "board game card": rounded outer corners, a portrait image filling the top half
// (rounded only where it meets the card's own top corners), and title / subtitle / description
// centered in the bottom half. A darker "side" face peeks out past the bottom-left edge of the
// front face to suggest physical thickness, a drop shadow (attached to that back layer, since
// it's the surface actually touching the table) adds ambient depth, and a diagonal light-to-dark
// gradient across the front face simulates it catching light. Width is deliberately not fixed
// here — callers size it via `modifier`.
val BoardgameCardAspectRatio = 5f / 7f
val BoardgameCardCornerRadius = 8.dp
val BoardgameCardElevation = 8.dp
val BoardgameCardExtrusionOffset = 2.dp
val boardgameCardShape = RoundedCornerShape(BoardgameCardCornerRadius)
val boardgameCardImageShape =
    RoundedCornerShape(topStart = BoardgameCardCornerRadius, topEnd = BoardgameCardCornerRadius)

@Composable
fun BoardgameCardVertical(
    image: ImageBitmap,
    title: String,
    subtitle: String,
    description: String,
    backgroundColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    descriptionColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.aspectRatio(BoardgameCardAspectRatio)) {
        // Side face: a solid darker tint of backgroundColor, offset down-left so a sliver of it
        // peeks out past the front face's edges. Carries the shadow since it's the layer that's
        // physically lowest / touching the table.
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = -BoardgameCardExtrusionOffset, y = BoardgameCardExtrusionOffset)
                .shadow(elevation = BoardgameCardElevation, shape = boardgameCardShape)
                .clip(boardgameCardShape)
                .background(lerp(backgroundColor, Color.Black, 0.35f)),
        )

        Column(
            modifier = Modifier
                .matchParentSize()
                .clip(boardgameCardShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            lerp(backgroundColor, Color.White, 0.16f),
                            backgroundColor,
                            lerp(backgroundColor, Color.Black, 0.16f),
                        ),
                    ),
                )
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), boardgameCardShape),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    bitmap = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    filterQuality = FilterQuality.High,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                        .clip(boardgameCardImageShape)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), boardgameCardImageShape),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(color = titleColor),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleSmall.copy(color = subtitleColor),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(color = descriptionColor),
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
@Preview
private fun BoardgameCardVerticalPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                BoardgameCardVertical(
                    image = ImageBitmap(width = 240, height = 240),
                    title = "Title",
                    subtitle = "Subtitle",
                    description = "A short description goes here.",
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    subtitleColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    descriptionColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.width(240.dp),
                )
            }
        }
    }
}
