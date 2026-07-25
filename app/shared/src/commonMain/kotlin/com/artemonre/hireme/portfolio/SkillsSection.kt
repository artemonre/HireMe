package com.artemonre.hireme.portfolio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.artemonre.hireme.theme.HireMeTheme

private val ChipHorizontalSpacing = 8.dp
private val ChipVerticalSpacing = 4.dp
private val TitleToChipsGap = 8.dp
private const val NarrowMaxVisibleSkills = 10

@Composable
fun SkillsSection(skills: List<Skill>, isWideScreen: Boolean, modifier: Modifier = Modifier) {
    if (skills.isEmpty()) return

    var showAllSkills by remember { mutableStateOf(false) }

    if (isWideScreen) {
        // Title + chips are measured together in one pass so the whole block never exceeds the
        // incoming height constraint (bio bottom, see PortfolioScreen). Chips that don't fit are
        // replaced by a "+N more" chip instead of being cut off mid-row or scrolled.
        WideSkillsContent(
            skills = skills,
            onShowAllClick = { showAllSkills = true },
            modifier = modifier.fillMaxWidth(),
        )
    } else {
        // Narrow layout has no height constraint of its own — the page-level scroll handles
        // overflow — but the chip list itself is still capped, replacing trailing chips with a
        // "+N more" chip that opens the same all-skills sheet as on wide screens.
        NarrowSkillsContent(
            skills = skills,
            onShowAllClick = { showAllSkills = true },
            modifier = modifier.fillMaxWidth().padding(horizontal = ScreenPadding),
        )
    }

    if (showAllSkills) {
        AllSkillsSheet(skills = skills, onDismissRequest = { showAllSkills = false })
    }
}

@Composable
private fun NarrowSkillsContent(
    skills: List<Skill>,
    onShowAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedSkill by remember { mutableStateOf<Skill?>(null) }
    val density = LocalDensity.current
    val visibleSkills = skills.take(NarrowMaxVisibleSkills)
    val hiddenCount = skills.size - visibleSkills.size

    Column(modifier = modifier) {
        Text(
            text = "Skills",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(TitleToChipsGap))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ChipHorizontalSpacing),
            verticalArrangement = Arrangement.spacedBy(ChipVerticalSpacing),
        ) {
            visibleSkills.forEach { skill ->
                SkillChip(
                    skill = skill,
                    isExpanded = expandedSkill == skill,
                    onClick = { expandedSkill = if (expandedSkill == skill) null else skill },
                    onDismissDescription = { expandedSkill = null },
                    density = density,
                )
            }
            if (hiddenCount > 0) {
                OverflowChip(hiddenCount = hiddenCount, onClick = onShowAllClick)
            }
        }
    }
}

@Composable
private fun WideSkillsContent(
    skills: List<Skill>,
    onShowAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedSkill by remember { mutableStateOf<Skill?>(null) }
    val density = LocalDensity.current

    SubcomposeLayout(modifier = modifier) { constraints ->
        val hSpacing = ChipHorizontalSpacing.roundToPx()
        val vSpacing = ChipVerticalSpacing.roundToPx()
        val titleGap = TitleToChipsGap.roundToPx()
        val unboundedHeight = Constraints(maxWidth = constraints.maxWidth)

        val titlePlaceable = subcompose("title") {
            Text(
                text = "Skills",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }.first().measure(unboundedHeight)

        val chipsMaxHeight = (constraints.maxHeight - titlePlaceable.height - titleGap).coerceAtLeast(0)

        val chipPlaceables = skills.mapIndexed { index, skill ->
            subcompose("chip-$index") {
                SkillChip(
                    skill = skill,
                    isExpanded = expandedSkill == skill,
                    onClick = { expandedSkill = if (expandedSkill == skill) null else skill },
                    onDismissDescription = { expandedSkill = null },
                    density = density,
                )
            }.first().measure(unboundedHeight)
        }

        // Try showing every chip first, then progressively hide trailing chips (replacing them
        // with a "+N more" chip) until what's left fits within chipsMaxHeight.
        var visibleCount = skills.size
        var chosenRows: List<ChipRow> = emptyList()
        while (visibleCount >= 0) {
            val hiddenCount = skills.size - visibleCount
            val rowInput = if (hiddenCount == 0) {
                chipPlaceables
            } else {
                val overflowPlaceable = subcompose("overflow-$hiddenCount") {
                    OverflowChip(hiddenCount = hiddenCount, onClick = onShowAllClick)
                }.first().measure(unboundedHeight)
                chipPlaceables.take(visibleCount) + overflowPlaceable
            }
            val rows = packIntoRows(rowInput, constraints.maxWidth, hSpacing)
            if (rowsHeight(rows, vSpacing) <= chipsMaxHeight) {
                chosenRows = rows
                break
            }
            visibleCount--
        }

        val totalHeight = if (chosenRows.isEmpty()) {
            titlePlaceable.height
        } else {
            titlePlaceable.height + titleGap + rowsHeight(chosenRows, vSpacing)
        }

        layout(constraints.maxWidth, totalHeight.coerceAtMost(constraints.maxHeight)) {
            titlePlaceable.placeRelative(0, 0)
            var y = titlePlaceable.height + titleGap
            for (row in chosenRows) {
                var x = 0
                for (placeable in row.placeables) {
                    placeable.placeRelative(x, y)
                    x += placeable.width + hSpacing
                }
                y += row.height + vSpacing
            }
        }
    }
}

private class ChipRow(val placeables: List<Placeable>, val height: Int)

private fun packIntoRows(placeables: List<Placeable>, maxWidth: Int, horizontalSpacing: Int): List<ChipRow> {
    if (placeables.isEmpty()) return emptyList()

    val rows = mutableListOf(mutableListOf(placeables.first()))
    var rowWidth = placeables.first().width
    for (placeable in placeables.drop(1)) {
        val widthWithPlaceable = rowWidth + horizontalSpacing + placeable.width
        if (widthWithPlaceable > maxWidth) {
            rows.add(mutableListOf(placeable))
            rowWidth = placeable.width
        } else {
            rows.last().add(placeable)
            rowWidth = widthWithPlaceable
        }
    }
    return rows.map { row -> ChipRow(placeables = row, height = row.maxOf { it.height }) }
}

private fun rowsHeight(rows: List<ChipRow>, verticalSpacing: Int): Int =
    if (rows.isEmpty()) 0 else rows.sumOf { it.height } + verticalSpacing * (rows.size - 1)

@Composable
private fun OverflowChip(hiddenCount: Int, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text("+$hiddenCount more") },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    )
}

@Composable
private fun AllSkillsSheet(skills: List<Skill>, onDismissRequest: () -> Unit) {
    var expandedSkill by remember { mutableStateOf<Skill?>(null) }
    val density = LocalDensity.current

    PortfolioModal(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding)
                .padding(bottom = 8.dp),
        ) {
            Text(
                text = "All Skills",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ChipHorizontalSpacing),
                verticalArrangement = Arrangement.spacedBy(ChipVerticalSpacing),
            ) {
                skills.forEach { skill ->
                    SkillChip(
                        skill = skill,
                        isExpanded = expandedSkill == skill,
                        onClick = { expandedSkill = if (expandedSkill == skill) null else skill },
                        onDismissDescription = { expandedSkill = null },
                        density = density,
                    )
                }
            }
        }
    }
}

@Composable
private fun SkillChip(
    skill: Skill,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onDismissDescription: () -> Unit,
    density: Density,
) {
    Box {
        AssistChip(
            onClick = onClick,
            label = { Text(skill.name) },
            colors = if (skill.highlighted) {
                AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                AssistChipDefaults.assistChipColors()
            },
        )
        if (isExpanded && skill.description.isNotBlank()) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = with(density) { IntOffset(0, 40.dp.roundToPx()) },
                onDismissRequest = onDismissDescription,
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    shadowElevation = 4.dp,
                    modifier = Modifier.widthIn(max = 220.dp),
                ) {
                    Text(
                        text = skill.description,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun SkillsSectionWidePreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                SkillsSection(myProfile.skills, isWideScreen = true)
            }
        }
    }
}

@Composable
@Preview
private fun SkillsSectionNarrowPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                SkillsSection(myProfile.skills, isWideScreen = false)
            }
        }
    }
}
