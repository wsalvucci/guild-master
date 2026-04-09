package com.example.demo.domain.model.items

sealed interface Item {
    val core: CoreData
    val name get() = core.name
    val description get() = core.description
}

data class CoreData(val name: String, val description: String)
data class SellableData(val basePrice: Int = 0)
data class QualityData(val itemQuality: Double = 1.0)
data class EnchantableData(val validEnchantments: List<Int/* TODO */> = emptyList())

interface SellableItem : Item {
    val sellable: SellableData
    val basePrice get() = sellable.basePrice
}

interface QualityItem : Item {
    val quality: QualityData
    val itemQuality get() = quality.itemQuality
}

interface EnchantableItem : Item {
    val enchantable: EnchantableData
    val validEnchantments get() = enchantable.validEnchantments
}

data class WearableData(val wearableType: ItemType.Equippable)
data class WearableItem(
    override val core: CoreData,
    override val sellable: SellableData = SellableData(),
    override val quality: QualityData = QualityData(),
    override val enchantable: EnchantableData = EnchantableData(),
    val wearable: WearableData,
) : SellableItem, QualityItem, EnchantableItem

data class RawResource(
    override val core: CoreData,
    override val sellable: SellableData = SellableData(),
    override val quality: QualityData = QualityData(),
) : SellableItem, QualityItem

data class ProcessedResource(
    override val core: CoreData,
    override val sellable: SellableData = SellableData(),
    override val quality: QualityData = QualityData(),
) : SellableItem, QualityItem

sealed class ItemType {
    abstract val key: String // Use for db linking
    sealed class Equippable : ItemType() {
        abstract val subKey: String
        override val key = "equippable-$subKey"
        data object Head : Equippable() {
            override val subKey: String = "head"
        }
        data object Neck : Equippable() {
            override val subKey: String = "neck"
        }
        data object Chest : Equippable() {
            override val subKey: String = "chest"
        }
        data object Hands : Equippable() {
            override val subKey: String = "hands"
        }
        data object Finger : Equippable() {
            override val subKey: String = "finger"
        }
        data object Back : Equippable() {
            override val subKey: String = "back"
        }
        data object Belt : Equippable() {
            override val subKey: String = "belt"
        }
        data object Legs : Equippable() {
            override val subKey: String = "legs"
        }
        data object Feet : Equippable() {
            override val subKey: String = "feet"
        }
    }

    sealed class Resource : ItemType() {
        abstract val subKey: String
        override val key = "resource-$subKey"
        data object Raw : Resource() {
            override val subKey: String = "raw"
        }
        data object Processed : Resource() {
            override val subKey: String = "processed"
        }
    }
}

data class ReqItemData(
    val item: Item,
    val quantity: Int = 1,
    val minQuality: Double = 0.0,
    // val enchantments?
    // val max/min age?
)

data class OutputItemData(
    val item: Item,
    val quantity: Int,
    val quality: Double,
    // val enchantments
)
