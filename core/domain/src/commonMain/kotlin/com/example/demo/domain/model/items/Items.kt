package com.example.demo.domain.model.items

import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ItemKey(
    val typeKey: String,
    val variantKey: String,
) {
    fun serialize(): String = "$typeKey$SEPARATOR$variantKey"

    companion object {
        private const val SEPARATOR = "::"

        fun deserialize(value: String): ItemKey {
            val parts = value.split(SEPARATOR)
            return ItemKey(
                typeKey = parts.getOrElse(0) { "" },
                variantKey = parts.getOrElse(1) { "" },
            )
        }
    }
}

fun ItemKey.toDisplayName(): String {
    val type = CraftingItemType.fromKey(typeKey) ?: return typeKey
    val variant = CraftingVariantType.fromKey(variantKey) ?: return typeKey
    val variantDisplay = variant.displayName
    return if (variantDisplay != null) "$variantDisplay ${type.displayName}" else type.displayName
}

data class ItemInstance(
    val uuid: String,
    val key: ItemKey,
    val quality: Double?,
) {
    val name: String get() = key.toDisplayName()
}

data class ReqItemData(
    val itemKey: ItemKey,
    val quantity: Int = 1,
    val minQuality: Double = 0.0,
)

data class OutputItemData(
    val itemKey: ItemKey,
    val quantity: Int,
    val minQuality: Double,
    val maxQuality: Double,
)

@OptIn(ExperimentalUuidApi::class)
fun OutputItemData.instantiate(): ItemInstance = ItemInstance(
    uuid = Uuid.random().toString(),
    key = itemKey,
    quality = if (minQuality < maxQuality) Random.nextDouble(minQuality, maxQuality) else minQuality,
)

data class SellableData(val basePrice: Int = 0)

data class WearableDescriptor(
    val wearableType: CraftingItemType.WearableType,
    val material: CraftingVariantType.MetalType,
)

data class WearableStats(
    val weight: Double,
    val defense: Double,
)

fun WearableDescriptor.toStats(): WearableStats = WearableStats(
    weight = 5.0 * material.statMultipliers.weightMultiplier,
    defense = wearableType.baseDefense * material.statMultipliers.armorDefenseMultiplier,
)

fun ItemKey.toWearableDescriptor(): WearableDescriptor? {
    val type = CraftingItemType.fromKey(typeKey) as? CraftingItemType.WearableType ?: return null
    val variant = CraftingVariantType.fromKey(variantKey) as? CraftingVariantType.MetalType ?: return null
    return WearableDescriptor(type, variant)
}
