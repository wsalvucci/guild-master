package com.example.demo.domain.model.tasks

import com.example.demo.domain.model.items.OutputItemData
import com.example.demo.domain.model.items.ReqItemData
import com.example.demo.domain.model.skills.CharacterStat
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 *
 */
data class TaskTemplate(
    val name: String,
    val description: String,
    val category: TaskCategory,
    var reqStatLevels: List<ReqStatData> = emptyList(),
    var workPerTick: Double = 0.0,
    var totalWork: Double = 0.0,
    var tags: List<TaskTag> = emptyList(),
    var reqItems: List<ReqItemData> = emptyList(),
    var outputItems: List<OutputItemData> = emptyList(),
    var experienceGain: List<Pair<Int, CharacterStat>> = emptyList(),
    val isBackground: Boolean = false
) {
    @OptIn(ExperimentalUuidApi::class)
    fun instantiate() : Task {
        return Task(
            uuid = Uuid.random().toString(),
            startedAt = Clock.System.now().epochSeconds,
            name = name,
            description = description,
            category = category,
            reqStatLevels = reqStatLevels,
            workPerSecond = workPerTick,
            totalWork = totalWork,
            workCompleted = 0.0,
            workCompetedPerCharacter = emptyList(),
            tags = tags,
            reqItems = reqItems,
            outputItems = outputItems,
            experienceGain = experienceGain,
            isBackground = isBackground
        )
    }
}

/**
 * @param uuid Unique identifier for all tasks instances
 * @param name The task name
 * @param description The task description
 * @param reqStatLevels The list of stats required for the task and their minimum levels
 * @param workPerSecond The amount of work done per simulation tick
 * @param tags The list of tags associated with the task
 * @param reqItems The items required for the task and their required quantities and properties
 * @param outputItems The items earned for completing the task and their quantities and properties
 * @param isBackground Tasks done in the background can be done simultaneously. Only one
 * non-background task can be performed at once.
 */
data class Task(
    val uuid: String,
    val startedAt: Long,
    val name: String,
    val description: String,
    val category: TaskCategory,
    val reqStatLevels: List<ReqStatData>,
    val workPerSecond: Double,
    val totalWork: Double,
    val workCompleted: Double,
    val workCompetedPerCharacter: List<Pair<Double, String>>,
    val tags: List<TaskTag>,
    val reqItems: List<ReqItemData>,
    val outputItems: List<OutputItemData>,
    val experienceGain: List<Pair<Int, CharacterStat>>,
    val isBackground: Boolean
)

data class ReqStatData(
    val stat: CharacterStat,
    val minLevel: Int,
    val bonusWorkAboveMinLevel: Int,
    val maxBonusLevel: Int,
)

sealed class TaskTag {
    abstract val key: String // Use for db linking
    abstract val name: String

    sealed class SkillTypeTag : TaskTag() {
        val color = 0xFF0000FF
        abstract val subKey: String
        override val key = "skill_type-$subKey"
        data object Production : SkillTypeTag() {
            override val subKey = "production"
            override val name: String = "Production"
        }
    }

    sealed class DurationTag : TaskTag() {
        val color = 0xFFFF0000
        abstract val subKey: String
        override val key = "duration-$subKey"

        data object VeryShort : DurationTag() {
            override val subKey = "very_short"
            override val name: String = "Very Short"
        }

        data object Short : DurationTag() {
            override val subKey = "short"
            override val name: String = "Short"
        }

        data object Medium : DurationTag() {
            override val subKey = "medium"
            override val name: String = "Medium"
        }

        data object Long : DurationTag() {
            override val subKey = "long"
            override val name: String = "Long"
        }

        data object VeryLong : DurationTag() {
            override val subKey = "very_long"
            override val name: String = "Very Long"
        }
    }
}

data class ActiveCharacterTask(
    val task: Task,
    val inProgress: Boolean,
    val workCompleted: Boolean,
    val startedAt: Long,
)

sealed class TaskCategory {
    abstract val name: String
    abstract val key: String

    sealed class Production : TaskCategory() {
        abstract val subKey: String
        override val key: String = "production-$subKey"

        data object Mining : Production() {
            override val name: String = "Mining"
            override val subKey = "mining"
        }

        data object Woodcutting : Production() {
            override val name: String = "Woodcutting"
            override val subKey: String = "woodcutting"
        }
    }
}

