package com.example.demo.domain.model.items

object ItemCatalog {
    private val byTagId = LinkedHashMap<String, Item>() // or mutableMapOf

    fun register(item: Item): Item {
        val id = item.tag
        require(id !in byTagId) { "Duplicate tagId: $id" }
        byTagId[id] = item
        return item
    }

    /** Every registered item, stable order if you use LinkedHashMap + insertion order */
    val all: Collection<Item> get() = byTagId.values

    /** Load/save / dynamic lookup */
    fun getByTagId(tagId: String): Item? = byTagId[tagId]

    fun requireByTagId(tagId: String): Item =
        getByTagId(tagId) ?: error("Unknown item: $tagId")
}

private fun Item.register() {
    ItemCatalog.register(this)
}

object ItemList {
    object Armor {
        object Head {
            object Metal {
                object Bronze {
                    val BronzeFullHelmet = Item(
                        name = "Bronze Full Helmet",
                        tag = "bronze_full_helmet",
                        description = "A full helmet made of bronze.",
                        wearable = WearableData(
                            wearableType = ItemType.Equippable.Head
                        ),
                    ).also { it.register() }
                }
            }
        }
    }
    object Resource {
        object Raw {
            object Scavange {
                val Rocks = Item(
                    name = "Rocks",
                    tag = "rocks",
                    description = "Some rocks.",
                    minMaxQuality = 0.0 to 1.0
                ).also { it.register() }
                val Sticks = Item(
                    name = "Sticks",
                    tag = "sticks",
                    description = "Some sticks.",
                    minMaxQuality = 0.0 to 1.0
                ).also { it.register() }
            }
            object Ore {
                val CopperOre = Item(
                    name = "Copper Ore",
                    tag = "copper_ore",
                    description = "Copper ore used for forging.",
                    minMaxQuality = 0.0 to 1.0
                ).also { it.register() }
                val TinOre = Item(
                    name = "Tin Ore",
                    tag = "tin_ore",
                    description = "Tin ore used for forging.",
                    minMaxQuality = 0.0 to 1.0
                ).also { it.register() }
            }
        }
        object Processed {
            val BronzeIngot = Item(
                name = "Bronze Ingot",
                tag = "bronze_ingot",
                description = "Bronze ingots used for forging.",
                minMaxQuality = 0.0 to 1.0
            ).also { it.register() }
        }
    }
}
