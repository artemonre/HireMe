package com.artemonre.hireme.portfolio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.components.BoardgameCardVertical
import com.artemonre.hireme.theme.HireMeTheme
import hireme.app.shared.generated.resources.Res
import hireme.app.shared.generated.resources.user_photo
import org.jetbrains.compose.resources.imageResource

@Composable
fun ProfileHeader(
    profile: PortfolioProfile,
    onHeaderPositioned: (center: Offset, radiusPx: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoardgameCardVertical(
        image = imageResource(Res.drawable.user_photo),
        title = profile.name,
        subtitle = profile.title,
        description = profile.tagline,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        titleColor = MaterialTheme.colorScheme.onPrimaryContainer,
        subtitleColor = MaterialTheme.colorScheme.onTertiaryContainer,
        descriptionColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = modifier.onGloballyPositioned { coordinates ->
            val bounds = coordinates.boundsInRoot()
            // Reveal starts as a circle whose diameter equals the card's width, not the
            // smaller of width/height — explicit rather than relying on the card's aspect
            // ratio happening to make width the smaller dimension.
            val radiusPx = bounds.width / 2f
            onHeaderPositioned(bounds.center, radiusPx)
        },
    )
}

@Composable
@Preview
private fun ProfileHeaderPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileHeader(
                    profile = myProfile,
                    onHeaderPositioned = { _, _ -> },
                    modifier = Modifier.width(240.dp),
                )
            }
        }
    }
}
