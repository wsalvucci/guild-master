package com.example.demo.domain.model.items

object ItemList {
    object Armor {
        object Head {
            object Metal {
                object Bronze {
                    val BronzeFullHelmet = WearableItem(
                        core = CoreData(
                            name = "Bronze Full Helmet",
                            description = "A full helmet made of bronze."
                        ),
                        wearable = WearableData(
                            wearableType = ItemType.Equippable.Head
                        ),
                    )
                }
            }
        }
    }
    object Resource {
        object Raw {
            val Rocks = RawResource(
                core = CoreData(
                    name = "Rocks",
                    description = "Some rocks.",
                ),
            )
            val CopperOre = RawResource(
                core = CoreData(
                    name = "Copper Ore",
                    description = "Copper ore used for forging."
                ),
            )
            val TinOre = RawResource(
                core = CoreData(
                    name = "Tin Ore",
                    description = "Tin ore used for forging."
                ),
            )
        }
        object Processed {
            val BronzeIngot = ProcessedResource(
                core = CoreData(
                    name = "Bronze Ingot",
                    description = "Bronze ingots used for forging."
                )
            )
        }
    }
}
