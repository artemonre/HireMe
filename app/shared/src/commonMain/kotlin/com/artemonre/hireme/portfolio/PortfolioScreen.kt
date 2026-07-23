package com.artemonre.hireme.portfolio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme
import com.artemonre.hireme.theme.ThemeMode
import hireme.app.shared.generated.resources.Res
import hireme.app.shared.generated.resources.avatar_placeholder
import org.jetbrains.compose.resources.painterResource

private val ThemeMode.label: String
    get() = name.lowercase().replaceFirstChar { it.uppercase() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    profile: PortfolioProfile = myProfile,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onAvatarPositioned: (center: Offset, radiusPx: Float) -> Unit = { _, _ -> },
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    // Two columns, top-aligned: profile on the start side, contacts (and skills,
                    // beneath them) on the other. Skills' height is capped so the right column never
                    // grows past the profile column's height (bio bottom), scrolling internally
                    // instead of pushing the sections below it down.
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
                                .weight(1f)
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
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
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
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
                modifier = Modifier.size(64.dp),
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(profile.name, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = profile.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(12.dp))
        Text(
            text = profile.bio,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
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
