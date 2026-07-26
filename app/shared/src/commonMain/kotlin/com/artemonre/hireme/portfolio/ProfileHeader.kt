package com.artemonre.hireme.portfolio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme
import hireme.app.shared.generated.resources.Res
import hireme.app.shared.generated.resources.user_photo
import org.jetbrains.compose.resources.imageResource

// A vertical "board game card" for the photo and headline info: rounded outer corners, a portrait
// slot filling the top half (rounded only where it meets the card's own top corners), and name /
// title / tagline centered in the bottom half. Deliberately flat for now — no shadow, no texture.
// Width is deliberately not fixed here — callers size it via `modifier` (e.g. a fixed width
// alongside the wide-layout's contacts column, or fillMaxWidth() on narrow/mobile screens).
private val ProfileCardAspectRatio = 5f / 7f
private val ProfileCardCornerRadius = 8.dp
private val cardShape = RoundedCornerShape(ProfileCardCornerRadius)
private val cardImageShape = RoundedCornerShape(topStart = ProfileCardCornerRadius, topEnd = ProfileCardCornerRadius)

@Composable
fun ProfileHeader(
    profile: PortfolioProfile,
    onAvatarPositioned: (center: Offset, radiusPx: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .aspectRatio(ProfileCardAspectRatio)
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), cardShape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onGloballyPositioned { coordinates ->
                    val bounds = coordinates.boundsInRoot()
                    val radiusPx = minOf(bounds.width, bounds.height) / 2f
                    onAvatarPositioned(bounds.center, radiusPx)
                },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                bitmap = imageResource(Res.drawable.user_photo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                filterQuality = FilterQuality.High,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                    .clip(cardImageShape)
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), cardImageShape),
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
                text = profile.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = profile.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = profile.tagline,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
@Preview
private fun ProfileHeaderPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileHeader(
                    profile = myProfile,
                    onAvatarPositioned = { _, _ -> },
                    modifier = Modifier.width(240.dp),
                )
            }
        }
    }
}
