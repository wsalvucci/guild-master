package com.example.demo.domain.model.tasks

import com.example.demo.domain.model.items.OutputItemData
import com.example.demo.domain.model.items.ReqItemData
import com.example.demo.domain.model.skills.CharacterStat
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
    fun instantiate(
        initiatingCharacterId: String
    ) : Task {
        return Task(
            uuid = Uuid.random().toString(),
            startedAt = Clock.System.now().epochSeconds,
            name = name,
            description = description,
            category = category,
            reqStatLevels = reqStatLevels,
            workPerSecond = workPerTick,
            totalWork = totalWork,
            workCompletedPerCharacter = listOf(
                0.0 to initiatingCharacterId
            ),
            tags = tags,
            reqItems = reqItems,
            outputItems = outputItems,
            experienceGain = experienceGain,
            isBackground = isBackground,
            collected = false
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
    val workCompletedPerCharacter: List<Pair<Double, String>>,
    val tags: List<TaskTag>,
    val reqItems: List<ReqItemData>,
    val outputItems: List<OutputItemData>,
    val experienceGain: List<Pair<Int, CharacterStat>>,
    val isBackground: Boolean,
    val collected: Boolean
) {
    val workCompleted get() = workCompletedPerCharacter.fold(0.0) { acc, pair -> acc + pair.first}
}

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
        override val key: String get() = "skill_type-$subKey"
        data object Production : SkillTypeTag() {
            override val subKey = "production"
            override val name: String = "Production"
        }
    }

    sealed class DurationTag : TaskTag() {
        val color = 0xFFFF0000
        abstract val subKey: String
        override val key: String get() = "duration-$subKey"

        data object VeryShort : DurationTag() {
            override val subKey: String get() = "very_short"
            override val name: String get() = "Very Short"
        }

        data object Short : DurationTag() {
            override val subKey: String get() = "short"
            override val name: String get() = "Short"
        }

        data object Medium : DurationTag() {
            override val subKey: String get() = "medium"
            override val name: String get() = "Medium"
        }

        data object Long : DurationTag() {
            override val subKey: String get() = "long"
            override val name: String get() = "Long"
        }

        data object VeryLong : DurationTag() {
            override val subKey: String get() = "very_long"
            override val name: String get() = "Very Long"
        }
    }

    data object Unknown : TaskTag() {
        override val key: String get() = "unknown"
        override val name: String get() = "Unknown"
    }

    companion object {
        private val all: List<TaskTag> by lazy(LazyThreadSafetyMode.PUBLICATION) {
            listOf(
                SkillTypeTag.Production,
                DurationTag.VeryShort,
                DurationTag.Short,
                DurationTag.Medium,
                DurationTag.Long,
                DurationTag.VeryLong,
            )
        }

        private val bykey: Map<String, TaskTag> by lazy(LazyThreadSafetyMode.PUBLICATION) {
            all.associateBy { it.key }
        }

        fun fromKey(key: String): TaskTag = bykey[key] ?: Unknown
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
        override val key: String get() = "production-$subKey"

        data object Mining : Production() {
            override val name: String get() = "Mining"
            override val subKey: String get() = "mining"
        }

        data object Woodcutting : Production() {
            override val name: String get() = "Woodcutting"
            override val subKey: String get() = "woodcutting"
        }
    }

    data object Unknown : TaskCategory() {
        override val key: String
            get() = "unknown"

        override val name: String
            get() = "Unknown"
    }

    companion object {
        private val all: List<TaskCategory> by lazy(LazyThreadSafetyMode.PUBLICATION) {
            listOf(
                Production.Mining,
                Production.Woodcutting,
            )
        }

        private val byKey: Map<String, TaskCategory> by lazy(LazyThreadSafetyMode.PUBLICATION) {
            all.associateBy { it.key }
        }

        fun fromKey(key: String): TaskCategory = byKey[key] ?: Unknown
    }
}

