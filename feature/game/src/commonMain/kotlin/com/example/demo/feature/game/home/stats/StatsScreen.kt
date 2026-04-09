package com.example.demo.feature.game.home.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demo.domain.model.worldsave.CharacterData
import com.example.demo.domain.model.skills.CharacterStat
import com.example.demo.domain.model.skills.CharacterStatData
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.util.xpToNextLevel
import com.example.demo.feature.game.utils.getSkillIcon
import com.example.demo.ui.theme.DemoTheme
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

@Composable
fun StatsScreen(
    stats: CharacterData?,
    modifier: Modifier = Modifier,
) {
    if (stats == null) return
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            stats.skills.groupBy { it.skill.type }.map { (skillType, list) ->
                // Group Column
                Column(
                    modifier = modifier
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.height(2.dp).weight(1f).background(MaterialTheme.colorScheme.surface)
                        )
                        Text(skillType.toString())
                        Box(
                            modifier = Modifier.height(2.dp).weight(1f).background(MaterialTheme.colorScheme.surface)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        list.forEach { item ->
                            SkillItem(
                                data = item,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillItem(
    data: CharacterStatData,
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(data.skill.getSkillIcon()),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(data.skill.name)
                }
                Text("Lv. " + data.level.toString())
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(data.experience / xpToNextLevel(data.level).toFloat())
                        .height(5.dp)
                        .background(color = MaterialTheme.colorScheme.surface)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Status...")
                Row {
                    Text(data.experience.toString() + "/" + xpToNextLevel(data.level).toString())
                }
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun SkillItemPreview() {
    DemoTheme(
        themeId = AppThemeId.DEFAULT,
        mode = ThemeMode.DARK
    ) {
        SkillItem(
            data = CharacterStatData(
                skill = CharacterStat.Mining,
                level = 1,
                experience = 0
            )
        )
    }
}

@Preview(
    showBackground = true,
    heightDp = 600,
)
@Composable
private fun StatsScreenPreview() {
    DemoTheme(
        themeId = AppThemeId.DEFAULT,
        mode = ThemeMode.DARK
    ) {
        StatsScreen(
            stats = CharacterData(
                skills = listOf(
                    CharacterStatData(
                        skill = CharacterStat.Mining,
                        level = 1,
                        experience = 3
                    ),
                    CharacterStatData(
                        skill = CharacterStat.Smithing,
                        level = 1,
                        experience = 5
                    ),
                    CharacterStatData(
                        skill = CharacterStat.Stamina,
                        level = 1,
                        experience = 7
                    ),
                ),
            )
        )
    }
}