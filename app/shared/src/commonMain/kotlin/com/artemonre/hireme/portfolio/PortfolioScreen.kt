package com.artemonre.hireme.portfolio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme
import com.artemonre.hireme.theme.ThemeMode
import hireme.app.shared.generated.resources.Res
import hireme.app.shared.generated.resources.avatar_placeholder
import org.jetbrains.compose.resources.painterResource

private val ThemeMode.label: String
    get() = name.lowercase().replaceFirstChar { it.uppercase() }

// A vertical "board game card" for the photo and headline info: rounded outer corners, a portrait
// slot filling the top half (rounded only where it meets the card's own top corners), and name /
// title / tagline centered in the bottom half. Deliberately flat for now — no shadow, no texture.
private val ProfileCardWidth = 240.dp
private val ProfileCardAspectRatio = 5f / 7f
private val ProfileCardCornerRadius = 16.dp

// The background spans the full window, but the content column is capped to a fraction of it so
// lines of text and rows of cards don't stretch edge-to-edge on very wide (desktop) windows. Below
// WideLayoutBreakpoint the mobile single-column layout already uses the full width, so that same
// breakpoint doubles as the floor here — the cap never kicks in narrower than that.
private const val ContentWidthFraction = 0.7f
private val ContentMinWidth = WideLayoutBreakpoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    profile: PortfolioProfile = myProfile,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onAvatarPositioned: (center: Offset, radiusPx: Float) -> Unit = { _, _ -> },
) {
    val snackbarHostState = remember { SnackbarHostState() }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val contentMaxWidth = (maxWidth * ContentWidthFraction)
            .coerceAtLeast(ContentMinWidth)
            .coerceAtMost(maxWidth)

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxHeight()
                .widthIn(max = contentMaxWidth)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenPadding)
                    .padding(top = ScreenPadding),
                horizontalArrangement = Arrangement.End,
            ) {
                SingleChoiceSegmentedButtonRow {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = themeMode == mode,
                            onClick = { onThemeModeChange(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ThemeMode.entries.size,
                            ),
                            icon = {},
                            label = { Text(mode.label) },
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWideScreen = maxWidth >= WideLayoutBreakpoint

                if (isWideScreen) {
                    // Two columns, top-aligned: a fixed-width profile card on the start side, with
                    // contacts (and skills, beneath them) filling the rest of the row's width.
                    // Skills' height is capped so that column never grows past the profile card's
                    // height, scrolling internally instead of pushing the sections below it down.
                    var profileHeaderHeightPx by remember { mutableStateOf(0) }
                    var contactsHeightPx by remember { mutableStateOf(0) }
                    val density = LocalDensity.current

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ScreenPadding),
                        horizontalArrangement = Arrangement.spacedBy(ScreenPadding),
                        verticalAlignment = Alignment.Top,
                    ) {
                        ProfileHeader(
                            profile = profile,
                            onAvatarPositioned = onAvatarPositioned,
                            modifier = Modifier
                                .onGloballyPositioned { profileHeaderHeightPx = it.size.height },
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.onGloballyPositioned { contactsHeightPx = it.size.height }) {
                                ContactsSection(profile.contacts, isWideScreen = true, snackbarHostState = snackbarHostState)
                            }
                            Spacer(Modifier.height(24.dp))
                            val skillsMaxHeight = with(density) {
                                (profileHeaderHeightPx - contactsHeightPx).coerceAtLeast(0).toDp()
                            }
                            SkillsSection(
                                profile.skills,
                                isWideScreen = true,
                                modifier = Modifier.heightIn(max = skillsMaxHeight),
                            )
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // ContactsSection pads itself the same way other sections do, so its
                        // margin is applied here directly rather than on the wrapping Column
                        // (which would double up with ContactsSection's own padding).
                        ProfileHeader(
                            profile = profile,
                            onAvatarPositioned = onAvatarPositioned,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenPadding),
                        )
                        Spacer(Modifier.height(24.dp))
                        ContactsSection(profile.contacts, isWideScreen = false, snackbarHostState = snackbarHostState)
                        Spacer(Modifier.height(24.dp))
                        SkillsSection(profile.skills, isWideScreen = false)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            BioSection(profile.bio)

            Spacer(Modifier.height(24.dp))
            ExperienceSection(profile.experience)

            Spacer(Modifier.height(24.dp))
            ProjectsSection(profile.projects)

            Spacer(Modifier.height(24.dp))
            LinksSection(profile.links)

            Spacer(Modifier.height(24.dp))
            TechnologiesSection(profile.applicationTechnologies, profile.serverTechnologies)
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}

@Composable
private fun ProfileHeader(
    profile: PortfolioProfile,
    onAvatarPositioned: (center: Offset, radiusPx: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        val cardShape = RoundedCornerShape(ProfileCardCornerRadius)

        Column(
            modifier = Modifier
                .width(ProfileCardWidth)
                .aspectRatio(ProfileCardAspectRatio)
                .clip(cardShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), cardShape),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .onGloballyPositioned { coordinates ->
                        val bounds = coordinates.boundsInRoot()
                        val radiusPx = minOf(bounds.width, bounds.height) / 2f
                        onAvatarPositioned(bounds.center, radiusPx)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.avatar_placeholder),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer),
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(topStart = ProfileCardCornerRadius, topEnd = ProfileCardCornerRadius)),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = profile.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = profile.tagline,
                    style = MaterialTheme.typography.bodySmall,
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
private fun PortfolioScreenLightPreview() {
    HireMeTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PortfolioScreen()
        }
    }
}

@Composable
@Preview
private fun PortfolioScreenDarkPreview() {
    HireMeTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PortfolioScreen()
        }
    }
}
