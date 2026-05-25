package com.example.demo.domain.model.items

sealed class CraftingVariantType(
    open val name: String,
) {
    abstract val key: String

    /** Shown as the leading word in computed item names (e.g. "Bronze" in "Bronze Helmet").
     *  Return null to suppress the variant word and show only the type name (e.g. "Rocks"). */
    open val displayName: String? get() = name

    sealed class StoneType(
        override val name: String,
    ) : CraftingVariantType(name = name) {
        override val key: String get() = "stone-${name.lowercase()}"

        data object Generic : StoneType(name = "Generic") {
            override val displayName: String? = null
        }

        companion object {
            fun fromKey(key: String): StoneType? = when (key) {
                Generic.key -> Generic
                else -> null
            }
        }
    }

    sealed class WoodType(
        override val name: String,
        open val minWoodcuttingLevel: Int,
    ) : CraftingVariantType(name = name) {
        override val key: String get() = "wood-${name.lowercase()}"

        data object Generic   : WoodType(name = "Generic",   minWoodcuttingLevel = 0) {
            override val displayName: String? = null
        }
        data object Balsa     : WoodType(name = "Balsa",     minWoodcuttingLevel = 1)
        data object Willow    : WoodType(name = "Willow",    minWoodcuttingLevel = 1)
        data object Spruce    : WoodType(name = "Spruce",    minWoodcuttingLevel = 1)
        data object Alder     : WoodType(name = "Alder",     minWoodcuttingLevel = 1)
        data object Pine      : WoodType(name = "Pine",      minWoodcuttingLevel = 1)
        data object Birch     : WoodType(name = "Birch",     minWoodcuttingLevel = 1)
        data object Mahogany  : WoodType(name = "Mahogany",  minWoodcuttingLevel = 1)
        data object Cedar     : WoodType(name = "Cedar",     minWoodcuttingLevel = 1)
        data object Cypress   : WoodType(name = "Cypress",   minWoodcuttingLevel = 1)
        data object Oak       : WoodType(name = "Oak",       minWoodcuttingLevel = 1)
        data object Elm       : WoodType(name = "Elm",       minWoodcuttingLevel = 1)
        data object Ash       : WoodType(name = "Ash",       minWoodcuttingLevel = 1)
        data object Beech     : WoodType(name = "Beech",     minWoodcuttingLevel = 1)
        data object Hickory   : WoodType(name = "Hickory",   minWoodcuttingLevel = 1)
        data object Acacia    : WoodType(name = "Acacia",    minWoodcuttingLevel = 1)
        data object Ebony     : WoodType(name = "Ebony",     minWoodcuttingLevel = 1)
        data object Ironwood  : WoodType(name = "Ironwood",  minWoodcuttingLevel = 1)

        companion object {
            fun fromKey(key: String): WoodType? = when (key) {
                Generic.key  -> Generic
                Balsa.key    -> Balsa
                Willow.key   -> Willow
                Spruce.key   -> Spruce
                Alder.key    -> Alder
                Pine.key     -> Pine
                Birch.key    -> Birch
                Mahogany.key -> Mahogany
                Cedar.key    -> Cedar
                Cypress.key  -> Cypress
                Oak.key      -> Oak
                Elm.key      -> Elm
                Ash.key      -> Ash
                Beech.key    -> Beech
                Hickory.key  -> Hickory
                Acacia.key   -> Acacia
                Ebony.key    -> Ebony
                Ironwood.key -> Ironwood
                else -> null
            }
        }
    }

    sealed class OreType(
        override val name: String,
        open val minMiningLevel: Int,
    ) : CraftingVariantType(name = name) {
        override val key: String get() = "ore-${name.lowercase()}"

        data object Copper : OreType(name = "Copper", minMiningLevel = 1)
        data object Tin    : OreType(name = "Tin",    minMiningLevel = 1)
        data object Iron   : OreType(name = "Iron",   minMiningLevel = 15)

        companion object {
            fun fromKey(key: String): OreType? = when (key) {
                Copper.key -> Copper
                Tin.key    -> Tin
                Iron.key   -> Iron
                else -> null
            }
        }
    }

    data class MetalTypeStatMultipliers(
        val armorDefenseMultiplier: Double = 1.0,
        val weaponAttackMultiplier: Double = 1.0,
        val weightMultiplier: Double = 1.0,
        val durabilityMultiplier: Double = 1.0,
    )

    sealed class MetalType(
        override val name: String,
        open val statMultipliers: MetalTypeStatMultipliers = MetalTypeStatMultipliers(),
    ) : CraftingVariantType(name = name) {
        override val key: String get() = "metal-${name.lowercase()}"

        data object Bronze : MetalType(
            name = "Bronze",
            statMultipliers = MetalTypeStatMultipliers(
                armorDefenseMultiplier = 0.8,
                weightMultiplier = 0.9,
            ),
        )
        data object Iron : MetalType(
            name = "Iron",
            statMultipliers = MetalTypeStatMultipliers(
                armorDefenseMultiplier = 1.2,
                weightMultiplier = 1.1,
            ),
        )

        companion object {
            fun fromKey(key: String): MetalType? = when (key) {
                Bronze.key -> Bronze
                Iron.key   -> Iron
                else -> null
            }
        }
    }

    sealed class FishType(
        override val name: String,
        open val minFishingLevel: Int,
    ) : CraftingVariantType(name = name) {
        override val key: String get() = "fish-${name.lowercase()}"

        companion object {
            fun fromKey(key: String): FishType? = null
        }
    }

    sealed class PlantType(
        override val name: String,
        open val minFarmingLevel: Int,
        open val seedsPerHarvest: Int,
    ) : CraftingVariantType(name = name) {
        override val key: String get() = "plant-${name.lowercase()}"

        companion object {
            fun fromKey(key: String): PlantType? = null
        }
    }

    companion object {
        fun fromKey(key: String): CraftingVariantType? =
            StoneType.fromKey(key)
                ?: WoodType.fromKey(key)
                ?: OreType.fromKey(key)
                ?: MetalType.fromKey(key)
                ?: FishType.fromKey(key)
                ?: PlantType.fromKey(key)
    }
}

sealed class CraftingItemType(
    open val name: String,
    val extraIngredients: List<RecipeTemplateIOData> = emptyList(),
) {
    abstract val key: String
    val displayName: String get() = name

    data object RawStone : CraftingItemType(name = "Rocks") {
        override val key: String get() = "raw-stone"
    }

    data object RawWood : CraftingItemType(name = "Sticks") {
        override val key: String get() = "raw-wood"
    }

    data class Ore(
        override val name: String = "Ore",
    ) : CraftingItemType(name) {
        override val key: String get() = "ore"
    }

    data class Ingot(
        override val name: String = "Ingot",
    ) : CraftingItemType(name = name) {
        override val key: String get() = "ingot"
    }

    sealed class WearableType(
        override val name: String,
        open val tag: String,
        open val baseDefense: Double = 0.0,
    ) : CraftingItemType(name = name) {

        sealed class ArmorType(
            override val name: String,
            override val tag: String,
            override val baseDefense: Double,
            open val metalIngredientCount: Int,
        ) : WearableType(name = name, tag = tag, baseDefense = baseDefense) {
            override val key: String get() = "wearable-armor-$tag"

            data object Helmet : ArmorType(
                name = "Helmet",
                tag = "helmet",
                baseDefense = 10.0,
                metalIngredientCount = 2,
            )

            companion object {
                fun fromKey(key: String): ArmorType? = when (key) {
                    Helmet.key -> Helmet
                    else -> null
                }
            }
        }

        sealed class ClothingType(
            override val name: String,
            override val tag: String,
            override val baseDefense: Double,
            open val threadIngredientCount: Int,
        ) : WearableType(name = name, tag = tag, baseDefense = baseDefense) {
            override val key: String get() = "wearable-clothing-$tag"

            data object Cloak : ClothingType(
                name = "Cloak",
                tag = "cloak",
                baseDefense = 2.0,
                threadIngredientCount = 1,
            )

            companion object {
                fun fromKey(key: String): ClothingType? = when (key) {
                    Cloak.key -> Cloak
                    else -> null
                }
            }
        }

        companion object {
            const val KEY_PREFIX = "wearable"

            fun fromKey(key: String): WearableType? =
                ArmorType.fromKey(key) ?: ClothingType.fromKey(key)
        }
    }

    companion object {
        fun fromKey(key: String): CraftingItemType? = when {
            key == RawStone.key                        -> RawStone
            key == RawWood.key                         -> RawWood
            key == Ore().key                           -> Ore()
            key == Ingot().key                         -> Ingot()
            key.startsWith(WearableType.KEY_PREFIX)    -> WearableType.fromKey(key)
            else                                       -> null
        }
    }
}
