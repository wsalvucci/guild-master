package com.example.demo.domain.model.skills

data class CharacterStatData(
    val skill: CharacterStat,
    val level: Long,
    val experience: Long,
)

enum class CharacterStatKeys {
    MINING,
    STAMINA,
    SMITHING,
    WOODCUTTING,
    UNKNOWN;

    companion object {
        fun fromString(key: String): CharacterStatKeys {
            return when (key) {
                "mining" -> MINING
                "stamina" -> STAMINA
                "smithing" -> SMITHING
                "woodcutting" -> WOODCUTTING
                else -> UNKNOWN
            }
        }

        fun toString(key: CharacterStatKeys): String {
            return when (key) {
                MINING -> "mining"
                STAMINA -> "stamina"
                SMITHING -> "smithing"
                WOODCUTTING -> "woodcutting"
                else -> "unknown"
            }
        }
    }
}

sealed class CharacterStat() {
    abstract val key: CharacterStatKeys
    abstract val name: String
    abstract val type: StatType

    data object Mining: CharacterStat() {
        override val key = CharacterStatKeys.MINING
        override val name = "Mining"
        override val type = StatType.Production
    }

    data object Smithing: CharacterStat() {
        override val key = CharacterStatKeys.SMITHING
        override val name = "Smithing"
        override val type = StatType.Production
    }

    data object Stamina: CharacterStat() {
        override val key = CharacterStatKeys.STAMINA
        override val name = "Stamina"
        override val type = StatType.Combat
    }

    data object Woodcutting: CharacterStat() {
        override val key = CharacterStatKeys.WOODCUTTING
        override val name = "Woodcutting"
        override val type = StatType.Production
    }

    data object Unknown: CharacterStat() {
        override val key = CharacterStatKeys.UNKNOWN
        override val name = "Unknown"
        override val type = StatType.Unknown
    }

    companion object {
        fun getFromKey(key: CharacterStatKeys): CharacterStat {
            return when (key) {
                CharacterStatKeys.MINING -> Mining
                CharacterStatKeys.STAMINA -> Stamina
                else -> Unknown
            }
        }
    }
}


sealed class StatType() {
    data object Production : StatType()
    data object Combat : StatType()
    data object Unknown : StatType()
}