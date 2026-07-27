package com.artemonre.hireme.portfolio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        modifier = modifier,
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
                    modifier = Modifier.width(240.dp),
                )
            }
        }
    }
}
