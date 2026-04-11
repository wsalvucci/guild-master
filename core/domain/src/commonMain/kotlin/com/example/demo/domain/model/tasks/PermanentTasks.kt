package com.example.demo.domain.model.tasks

import com.example.demo.domain.model.items.ItemList
import com.example.demo.domain.model.items.OutputItemData
import com.example.demo.domain.model.skills.CharacterStat

object PermanentTasks {
    val CollectRocks = TaskTemplate(
        name = "Collect Rocks",
        description = "Walk around town and collect some rocks.",
        category = TaskCategory.Production.Mining,
        reqStatLevels = listOf(
            ReqStatData(
                stat = CharacterStat.Mining,
                minLevel = 1,
                bonusWorkAboveMinLevel = 1,
                maxBonusLevel = 5
            )
        ),
        workPerTick = 1.0,
        totalWork = 10.0,
        tags = listOf(
            TaskTag.SkillTypeTag.Production,
            TaskTag.DurationTag.VeryShort
        ),
        outputItems = listOf(
            OutputItemData(
                itemTemplate = ItemList.Resource.Raw.Scavange.Rocks,
                minQuality = 0.0,
                maxQuality = 2.0,
                quantity = 5
            )
        ),
        experienceGain = listOf(
            4 to CharacterStat.Mining
        )
    )

    val CollectSticks = TaskTemplate(
        name = "Collect Sticks",
        description = "Walk around town and collect some sticks.",
        category = TaskCategory.Production.Woodcutting,
        reqStatLevels = listOf(
            ReqStatData(
                stat = CharacterStat.Woodcutting,
                minLevel = 1,
                bonusWorkAboveMinLevel = 1,
                maxBonusLevel = 5
            )
        ),
        workPerTick = 1.0,
        totalWork = 10.0,
        tags = listOf(
            TaskTag.SkillTypeTag.Production,
            TaskTag.DurationTag.VeryShort
        ),
        outputItems = listOf(
            OutputItemData(
                itemTemplate = ItemList.Resource.Raw.Scavange.Sticks,
                minQuality = 0.0,
                maxQuality = 2.0,
                quantity = 5
            )
        ),
        experienceGain = listOf(
            4 to CharacterStat.Woodcutting
        )
    )

    val allTasks = listOf(
        CollectRocks,
        CollectSticks,
    ).associateBy { it.name }
}
