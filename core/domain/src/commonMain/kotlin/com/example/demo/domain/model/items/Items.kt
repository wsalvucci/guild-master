package com.example.demo.domain.model.items

import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Item(
    val name: String,
    val tag: String,
    val description: String,
    val minMaxQuality: Pair<Double, Double>? = null,
    val enchantable: EnchantableData? = null,
    val sellable: SellableData? = null,
    val wearable: WearableData? = null,
) {
    @OptIn(ExperimentalUuidApi::class)
    fun instantiate(
        uuid: String? = null,
        quality: Double? = null,
        qualityBonus: Double = 0.0,
        // setEnchantments: List<EnchantmentInstance>
    ) : ItemInstance {
        return ItemInstance(
            uuid = uuid ?: Uuid.random().toString(),
            name = name,
            tag = tag,
            description = description,
            minMaxQuality = minMaxQuality,
            quality = quality ?: minMaxQuality?.let {
                Random.nextDouble(it.first, it.second) + qualityBonus
            },
            enchantable = enchantable,
            sellable = sellable,
            wearable = wearable,
        )
    }
}

data class ItemInstance(
    val uuid: String,
    val name: String,
    val tag: String,
    val description: String,
    val minMaxQuality: Pair<Double, Double>? = null,
    val quality: Double?,
    val enchantable: EnchantableData?,
    val sellable: SellableData?,
    val wearable: WearableData?,
)

data class SellableData(val basePrice: Int = 0)
data class QualityData(val itemQuality: Double = 1.0)
data class EnchantableData(val validEnchantments: List<Int/* TODO */> = emptyList())
data class WearableData(val wearableType: ItemType.Equippable)

sealed class ItemType {
    abstract val key: String // Use for db linking
    sealed class Equippable : ItemType() {
        abstract val subKey: String
        override val key: String get() = "equippable-$subKey"
        data object Head : Equippable() {
            override val subKey: String get() = "head"
        }
        data object Neck : Equippable() {
            override val subKey: String get() = "neck"
        }
        data object Chest : Equippable() {
            override val subKey: String get() = "chest"
        }
        data object Hands : Equippable() {
            override val subKey: String get() = "hands"
        }
        data object Finger : Equippable() {
            override val subKey: String get() = "finger"
        }
        data object Back : Equippable() {
            override val subKey: String get() = "back"
        }
        data object Belt : Equippable() {
            override val subKey: String get() = "belt"
        }
        data object Legs : Equippable() {
            override val subKey: String get() = "legs"
        }
        data object Feet : Equippable() {
            override val subKey: String get() = "feet"
        }
    }

    sealed class Resource : ItemType() {
        abstract val subKey: String
        override val key get() = "resource-$subKey"
        data object Raw : Resource() {
            override val subKey: String get() = "raw"
        }
        data object Processed : Resource() {
            override val subKey: String get() = "processed"
        }
    }
}

data class ReqItemData(
    val itemTemplate: Item,
    val quantity: Int = 1,
    val minQuality: Double = 0.0,
    // val enchantments?
    // val max/min age?
)

data class OutputItemData(
    val itemTemplate: Item,
    val quantity: Int,
    val minQuality: Double,
    val maxQuality: Double,
    // val enchantments
)
