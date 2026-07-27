package com.artemonre.hireme.portfolio

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.DefaultCameraDistance
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.components.BoardgameExtrusionEdges
import com.artemonre.hireme.components.BoardgameSurface
import com.artemonre.hireme.theme.HireMeTheme
import com.artemonre.hireme.theme.ThemeMode
import com.artemonre.hireme.theme.surfaceDimLight
import kotlinx.coroutines.launch

private val ThemeMode.label: String
    get() = name.lowercase().replaceFirstChar { it.uppercase() }

// The background spans the full window, but the content column is capped to a fraction of it so
// lines of text and rows of cards don't stretch edge-to-edge on very wide (desktop) windows. Below
// WideLayoutBreakpoint the mobile single-column layout already uses the full width, so that same
// breakpoint doubles as the floor here — the cap never kicks in narrower than that.
private const val ContentWidthFraction = 0.7f
private val ContentMinWidth = WideLayoutBreakpoint
private val PortfolioCardShape = RoundedCornerShape(16.dp)

// ProfileHeader has no width of its own — the wide two-column layout gives it this fixed size
// (matching the contacts/skills column next to it), while the narrow/mobile layout instead
// gives it fillMaxWidth() so the card stretches close to the screen width.
private val ProfileCardWideWidth = 240.dp

// The margin around the card is a fixed color rather than a theme color deliberately: it reads as
// the "table" the tablet card sits on, which isn't meant to change look with the card's own
// theme — always the light theme's dim tone, even when the card itself is in dark theme.
private val PortfolioBackgroundColor = surfaceDimLight

// A theme switch flips the "surface" (the card on wide desktop layouts, or the whole page on
// narrow ones) like a physical card: it rotates 180 degrees around its own vertical axis, showing
// the old theme's snapshot as the front face (rotating away) and the live, already-recomposed new
// theme as the back face (rotating into view). Deliberately scoped to just the surface, not the
// background around it — the background isn't meant to read as the same physical object as the
// card sitting on it, so it doesn't flip at all.
private const val SurfaceFlipRestRotation = 180f
private const val SurfaceFlipSwapThresholdDegrees = 90f
private const val SurfaceFlipDepthToWidthRatio = 1.4f
private const val SurfaceFlipDurationMillis = 550

private fun GraphicsLayerScope.applySurfaceFlipCameraDistance() {
    // How strong the perspective distortion looks depends on how big the flipping surface is
    // (a full desktop window vs. a narrow mobile layout), not just density, so this is derived
    // from the layer's own pixel size rather than a flat constant.
    cameraDistance = if (size.width > 0f) (size.width * SurfaceFlipDepthToWidthRatio) / 72f else DefaultCameraDistance
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    profile: PortfolioProfile = myProfile,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Rests at 180 (the live/back face fully forward, no flip in progress) both before the
    // first-ever flip and after every subsequent one; a flip animates this from 0 back to 180.
    val surfaceFlipRotation = remember { Animatable(SurfaceFlipRestRotation) }
    var oldSurfaceSnapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    val surfaceSnapshotLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()

    val flippingOnThemeModeChange: (ThemeMode) -> Unit = { mode ->
        scope.launch {
            // Capture the surface's current (still old-themed) frame before the state change
            // below causes it to recompose with the new theme's colors.
            val snapshot = surfaceSnapshotLayer.toImageBitmap()
            // Snap here, in the same coroutine as the snapshot/mode change, so the very first
            // frame drawn with the new snapshot already starts the flip from its beginning.
            // Doing this later (e.g. in a LaunchedEffect keyed on the snapshot) leaves a stray
            // frame where the rotation still holds its previous end value (180, fully flipped),
            // which briefly renders the new theme unflipped before snapping back and replaying
            // the flip.
            surfaceFlipRotation.snapTo(0f)
            oldSurfaceSnapshot = snapshot
            onThemeModeChange(mode)
        }
    }

    // Keyed on the snapshot itself (not on themeMode) so a mode change that doesn't actually
    // flip light/dark still clears the snapshot instead of leaving it stuck.
    LaunchedEffect(oldSurfaceSnapshot) {
        if (oldSurfaceSnapshot != null) {
            surfaceFlipRotation.animateTo(
                SurfaceFlipRestRotation,
                tween(SurfaceFlipDurationMillis, easing = LinearOutSlowInEasing),
            )
            oldSurfaceSnapshot = null
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val contentMaxWidth = (maxWidth * ContentWidthFraction)
            .coerceAtLeast(ContentMinWidth)
            .coerceAtMost(maxWidth)
        // Whether the content column actually leaves visible margin on both sides — i.e. whether
        // there's room, on a wide desktop window, to make the content read as a physical object
        // sitting on the (differently-colored) window background rather than being the background.
        val hasSideMargin = contentMaxWidth < maxWidth

        // Fixed color, not derived from the current theme — see PortfolioBackgroundColor above.
        Box(modifier = Modifier.fillMaxSize().background(PortfolioBackgroundColor))

        val contentModifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxHeight()
            .widthIn(max = contentMaxWidth)

        Box(
            modifier = contentModifier
                // Rotation lives on this outer layer, not inside drawWithContent below — a
                // graphicsLayer only transforms how its already-rendered output composites into
                // its parent, so the recording inside always captures this box's flat, unrotated
                // appearance regardless of the live rotation.
                .graphicsLayer {
                    rotationY = surfaceFlipRotation.value - SurfaceFlipRestRotation
                    alpha = if (surfaceFlipRotation.value >= SurfaceFlipSwapThresholdDegrees) 1f else 0f
                    applySurfaceFlipCameraDistance()
                }
                .drawWithContent {
                    surfaceSnapshotLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawContent()
                }
                // Placed after drawWithContent (i.e. innermost) so this fill is part of what
                // drawContent() replays inside graphicsLayer.record above, not drawn directly to
                // the real canvas. Otherwise the snapshot has transparent gaps wherever content
                // doesn't fully cover it (e.g. the narrow layout's Column doesn't paint its own
                // background), letting whatever's drawn behind it bleed through those gaps.
        ) {
            if (hasSideMargin) {
                // Top and bottom already run flush with the window's own edges (fillMaxHeight),
                // so only the left/right margins are visible — extrude those two edges, not
                // bottom.
                BoardgameSurface(
                    shape = PortfolioCardShape,
                    backgroundColor = MaterialTheme.colorScheme.surfaceBright,
                    edges = BoardgameExtrusionEdges.LeftAndBottom,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    PortfolioScreenContent(
                        profile = profile,
                        themeMode = themeMode,
                        onThemeModeChange = flippingOnThemeModeChange,
                        snackbarHostState = snackbarHostState,
                    )
                }
            } else {
                PortfolioScreenContent(
                    profile = profile,
                    themeMode = themeMode,
                    onThemeModeChange = flippingOnThemeModeChange,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        val snapshot = oldSurfaceSnapshot
        if (snapshot != null) {
            Image(
                bitmap = snapshot,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = contentModifier
                    .graphicsLayer {
                        rotationY = surfaceFlipRotation.value
                        alpha = if (surfaceFlipRotation.value < SurfaceFlipSwapThresholdDegrees) 1f else 0f
                        applySurfaceFlipCameraDistance()
                    },
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}

@Composable
private fun PortfolioScreenContent(
    profile: PortfolioProfile,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
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
                        modifier = Modifier
                            .width(ProfileCardWideWidth)
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
                Column {
                    // ContactsSection pads itself the same way other sections do, so its
                    // margin is applied here directly rather than on the wrapping Column
                    // (which would double up with ContactsSection's own padding).
                    ProfileHeader(
                        profile = profile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ScreenPadding),
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
