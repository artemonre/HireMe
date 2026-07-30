package com.artemonre.hireme.portfolio

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultCameraDistance
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.components.BoardgameCardAspectRatio
import com.artemonre.hireme.components.BoardgameCardVertical
import com.artemonre.hireme.theme.HireMeTheme
import hireme.app.shared.generated.resources.Res
import hireme.app.shared.generated.resources.user_photo
import hireme.app.shared.generated.resources.user_photo_alt
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.imageResource

// Tapping the card's bottom-right corner flips it 180 degrees around its vertical axis, the same
// physical-card motion as the app's theme-switch flip (see PortfolioScreen's SurfaceFlip*
// constants). Duplicated here rather than shared: that flip swaps a *snapshot* of the
// about-to-be-stale content for freshly recomposed content, while this one swaps between two
// permanently-live composables, so the surrounding plumbing doesn't have much in common beyond
// these tuning values.
private const val ProfileFlipSwapThresholdDegrees = 90f
private const val ProfileFlipDepthToWidthRatio = 1.4f
private const val ProfileFlipDurationMillis = 550

// The bottom-right hit zone that triggers the flip: half the card's width, an eighth of its
// height.
private const val FlipZoneWidthFraction = 0.5f
private const val FlipZoneHeightFraction = 0.125f

private fun GraphicsLayerScope.applyProfileFlipCameraDistance() {
    cameraDistance = if (size.width > 0f) (size.width * ProfileFlipDepthToWidthRatio) / 72f else DefaultCameraDistance
}

// Tap feedback for the flip zone: a circle that grows out from the touch point and fades, rather
// than the default ripple — which would either be clipped to this zone's wide, short rectangle
// (bounded) or recentered on the zone's middle instead of the finger (unbounded). Neither reads
// as "round around where you tapped," so this draws that directly.
private val TapCircleRadius = 20.dp
private const val TapCircleAlpha = 0.24f
private const val TapCircleGrowDurationMillis = 300
private const val TapCircleFadeInMillis = 75
private const val TapCircleFadeOutMillis = 150

private class TapCircleIndicationNode(
    private val interactionSource: InteractionSource,
    private val color: Color,
    private val radius: Dp,
) : Modifier.Node(), DrawModifierNode {
    private var position by mutableStateOf(Offset.Zero)
    private val growProgress = Animatable(0f)
    private val alpha = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        position = interaction.pressPosition
                        coroutineScope.launch {
                            growProgress.snapTo(0f)
                            growProgress.animateTo(1f, tween(TapCircleGrowDurationMillis, easing = FastOutSlowInEasing))
                        }
                        coroutineScope.launch { alpha.animateTo(TapCircleAlpha, tween(TapCircleFadeInMillis)) }
                    }
                    is PressInteraction.Release, is PressInteraction.Cancel -> {
                        coroutineScope.launch { alpha.animateTo(0f, tween(TapCircleFadeOutMillis)) }
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (alpha.value > 0f) {
            drawCircle(
                color = color,
                radius = radius.toPx() * growProgress.value,
                center = position,
                alpha = alpha.value,
            )
        }
    }
}

private data class TapCircleIndication(private val color: Color, private val radius: Dp) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        TapCircleIndicationNode(interactionSource, color, radius)
}

@Composable
private fun rememberTapCircleIndication(
    color: Color = MaterialTheme.colorScheme.onSurface,
    radius: Dp = TapCircleRadius,
): IndicationNodeFactory = remember(color, radius) { TapCircleIndication(color, radius) }

@Composable
fun ProfileHeader(
    profile: PortfolioProfile,
    altProfile: PortfolioProfile,
    modifier: Modifier = Modifier,
) {
    var isFlipped by remember { mutableStateOf(false) }
    var isFlipping by remember { mutableStateOf(false) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val flip: () -> Unit = {
        if (!isFlipping) {
            isFlipping = true
            scope.launch {
                rotation.animateTo(
                    targetValue = if (isFlipped) 0f else 180f,
                    animationSpec = tween(ProfileFlipDurationMillis, easing = LinearOutSlowInEasing),
                )
                isFlipped = !isFlipped
                isFlipping = false
            }
        }
    }

    Box(modifier = modifier.aspectRatio(BoardgameCardAspectRatio)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    rotationY = rotation.value
                    alpha = if (rotation.value < ProfileFlipSwapThresholdDegrees) 1f else 0f
                    applyProfileFlipCameraDistance()
                },
        ) {
            ProfileHeaderFace(profile = profile, image = Res.drawable.user_photo, modifier = Modifier.fillMaxSize())
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    rotationY = rotation.value - 180f
                    alpha = if (rotation.value >= ProfileFlipSwapThresholdDegrees) 1f else 0f
                    applyProfileFlipCameraDistance()
                },
        ) {
            ProfileHeaderFace(profile = altProfile, image = Res.drawable.user_photo_alt, modifier = Modifier.fillMaxSize())
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth(FlipZoneWidthFraction)
                .fillMaxHeight(FlipZoneHeightFraction)
                .clickable(interactionSource = null, indication = rememberTapCircleIndication(), onClick = flip),
        )
    }
}

@Composable
private fun ProfileHeaderFace(
    profile: PortfolioProfile,
    image: DrawableResource,
    modifier: Modifier = Modifier,
) {
    // Its own resource load rather than a bitmap passed down from the caller — so this face, like
    // its text content, doesn't share any state with the other face.
    BoardgameCardVertical(
        image = imageResource(image),
        title = profile.name,
        subtitle = profile.title,
        description = profile.shortDescription,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        titleColor = MaterialTheme.colorScheme.onPrimaryContainer,
        subtitleColor = MaterialTheme.colorScheme.onPrimaryContainer,
        descriptionColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
                    altProfile = altProfile,
                    modifier = Modifier.width(240.dp),
                )
            }
        }
    }
}
