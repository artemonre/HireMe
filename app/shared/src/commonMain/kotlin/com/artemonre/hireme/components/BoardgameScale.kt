package com.artemonre.hireme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

// A horizontal "scale": a title alongside a row of small round indicators ("rounds"), with a
// thumb composable overlaid on top of the round at `value` (1-based; 0 means "empty" — no thumb
// shown). Draws no background of its own — meant to sit on whatever surface/card hosts it.
//
// Title placement:
//  - StartTop/CenterTop/EndTop: title sits above the rounds, aligned start/center/end. It's always
//    kept within the rounds' own width (never wider than the first-to-last round span).
//  - Start: title sits inline, to the left of the rounds, with its text baseline aligned to the
//    bottom of the round indicators (like a form label next to a control). Fills the available
//    width and gives the title the leftover space via weight(1f), so the rounds land in a
//    consistent trailing column across a list of these rows regardless of title length.
enum class BoardgameScaleTitlePlacement { Start, StartTop, CenterTop, EndTop }

private val BoardgameScaleDotDiameter = 8.dp
private val BoardgameScaleDotSpacing = 8.dp
private val BoardgameScaleHorizontalPadding = 8.dp
private val BoardgameScaleTitleGap = 4.dp

private fun boardgameScaleWidth(count: Int) =
    BoardgameScaleDotDiameter * count +
        BoardgameScaleDotSpacing * (count - 1).coerceAtLeast(0) +
        BoardgameScaleHorizontalPadding * 2

@Composable
fun BoardgameScale(
    count: Int,
    value: Int,
    color: Color,
    title: @Composable () -> Unit,
    thumb: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    titlePlacement: BoardgameScaleTitlePlacement = BoardgameScaleTitlePlacement.StartTop,
) {
    when (titlePlacement) {
        BoardgameScaleTitlePlacement.Start ->
            ScaleWithInlineTitle(count, value, color, title, thumb, modifier)
        BoardgameScaleTitlePlacement.StartTop ->
            ScaleWithTopTitle(count, value, color, title, thumb, modifier, Alignment.Start)
        BoardgameScaleTitlePlacement.CenterTop ->
            ScaleWithTopTitle(count, value, color, title, thumb, modifier, Alignment.CenterHorizontally)
        BoardgameScaleTitlePlacement.EndTop ->
            ScaleWithTopTitle(count, value, color, title, thumb, modifier, Alignment.End)
    }
}

@Composable
private fun ScaleWithTopTitle(
    count: Int,
    value: Int,
    color: Color,
    title: @Composable () -> Unit,
    thumb: @Composable () -> Unit,
    modifier: Modifier,
    titleAlignment: Alignment.Horizontal,
) {
    val scaleWidth = boardgameScaleWidth(count)

    Column(
        modifier = modifier.widthIn(max = scaleWidth),
        horizontalAlignment = titleAlignment,
    ) {
        // widthIn(max = scaleWidth) + clipToBounds() guarantee the title never visually extends
        // past the first/last round, regardless of what the title composable does internally.
        // The horizontal padding matches ScaleRounds' own inset, so a Start/End-aligned title's
        // edge lines up with the first/last round rather than the container's raw edge.
        Box(
            modifier = Modifier
                .widthIn(max = scaleWidth)
                .padding(horizontal = BoardgameScaleHorizontalPadding)
                .clipToBounds(),
        ) {
            title()
        }
        Spacer(Modifier.height(BoardgameScaleTitleGap))
        ScaleRounds(count = count, value = value, color = color, thumb = thumb)
    }
}

@Composable
private fun ScaleWithInlineTitle(
    count: Int,
    value: Int,
    color: Color,
    title: @Composable () -> Unit,
    thumb: @Composable () -> Unit,
    modifier: Modifier,
) {
    // Fills the available width so the rounds land in the same trailing column across a list of
    // these rows regardless of each row's title length — the title takes whatever space that
    // leaves via weight(1f) rather than sizing to its own text. alignByBaseline (rather than
    // Alignment.Bottom) matches the title's text baseline to the rounds, since a Text's own layout
    // box extends below its baseline (descender space) and would otherwise look mis-aligned;
    // ScaleRounds has no baseline of its own, so Row falls back to aligning by its full height,
    // which for these small round indicators reads the same as bottom-aligned.
    Row(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f).alignByBaseline()) {
            title()
        }
        Spacer(Modifier.width(BoardgameScaleTitleGap))
        Box(modifier = Modifier.alignByBaseline()) {
            ScaleRounds(count = count, value = value, color = color, thumb = thumb)
        }
    }
}

@Composable
private fun ScaleRounds(
    count: Int,
    value: Int,
    color: Color,
    thumb: @Composable () -> Unit,
) {
    val clampedValue = value.coerceIn(0, count)

    Layout(
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(BoardgameScaleDotSpacing),
                modifier = Modifier.padding(horizontal = BoardgameScaleHorizontalPadding),
            ) {
                repeat(count) {
                    Box(
                        modifier = Modifier
                            .size(BoardgameScaleDotDiameter)
                            .clip(CircleShape)
                            .background(color),
                    )
                }
            }
            if (clampedValue > 0) {
                Box { thumb() }
            }
        },
    ) { measurables, constraints ->
        val roundsPlaceable = measurables[0].measure(constraints)
        val thumbPlaceable = if (measurables.size > 1) measurables[1].measure(Constraints()) else null

        val thumbX: Int
        val thumbY: Int
        if (thumbPlaceable != null) {
            val dotDiameterPx = BoardgameScaleDotDiameter.roundToPx()
            val dotSpacingPx = BoardgameScaleDotSpacing.roundToPx()
            val horizontalPaddingPx = BoardgameScaleHorizontalPadding.roundToPx()
            val dotIndex = clampedValue - 1
            val dotCenterX = horizontalPaddingPx +
                dotIndex * (dotDiameterPx + dotSpacingPx) +
                dotDiameterPx / 2
            thumbX = dotCenterX - thumbPlaceable.width / 2
            // Bottom-aligned rather than vertically centered: the round sits on the same "table"
            // the thumb does, so their bottoms should line up, not their centers.
            thumbY = roundsPlaceable.height - thumbPlaceable.height
        } else {
            thumbX = 0
            thumbY = 0
        }

        layout(roundsPlaceable.width, roundsPlaceable.height) {
            roundsPlaceable.placeRelative(0, 0)
            thumbPlaceable?.placeRelative(thumbX, thumbY)
        }
    }
}

@Composable
@Preview
private fun BoardgameScalePreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                BoardgameScale(
                    count = 10,
                    value = 3,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    title = { Text("Skill", style = MaterialTheme.typography.labelMedium) },
                    thumb = {
                        BoardgamePawn(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    titlePlacement = BoardgameScaleTitlePlacement.StartTop,
                )
                Spacer(Modifier.height(16.dp))
                BoardgameScale(
                    count = 5,
                    value = 3,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    title = { Text("Skill", style = MaterialTheme.typography.labelMedium) },
                    thumb = {
                        BoardgamePawn(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    titlePlacement = BoardgameScaleTitlePlacement.Start,
                )
            }
        }
    }
}
