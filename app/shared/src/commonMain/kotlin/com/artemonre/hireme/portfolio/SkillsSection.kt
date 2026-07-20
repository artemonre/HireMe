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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.artemonre.hireme.theme.HireMeTheme

@Composable
fun SkillsSection(skills: List<Skill>) {
    if (skills.isEmpty()) return

    var expandedSkill by remember { mutableStateOf<Skill?>(null) }
    val density = LocalDensity.current

    Text(
        text = "Skills",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(8.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        skills.forEach { skill ->
            Box {
                AssistChip(
                    onClick = {
                        expandedSkill = if (expandedSkill == skill) null else skill
                    },
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
                if (expandedSkill == skill && skill.description.isNotBlank()) {
                    Popup(
                        alignment = Alignment.TopCenter,
                        offset = with(density) { IntOffset(0, 40.dp.roundToPx()) },
                        onDismissRequest = { expandedSkill = null },
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
    }
}

@Composable
@Preview
private fun SkillsSectionPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                SkillsSection(myProfile.skills)
            }
        }
    }
}
