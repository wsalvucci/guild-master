package com.example.demo.domain.model.items

enum class RecipeCategory {
    SMELTING,
    ARMOR_FORGING,
    MATERIAL_PROCESSING,
    RESEARCH,
}

enum class WorkstationType {
    FORGE,
    TANNERY,
    CARPENTER_BENCH,
    RESEARCH_DESK,
}

data class WorkstationDefinition(
    val workstation: WorkstationType,
    val displayName: String,
    val categories: Set<RecipeCategory>,
)

object WorkstationRegistry {
    val all: List<WorkstationDefinition> = listOf(
        WorkstationDefinition(
            workstation = WorkstationType.FORGE,
            displayName = "Forge",
            categories = setOf(RecipeCategory.SMELTING, RecipeCategory.ARMOR_FORGING),
        ),
        WorkstationDefinition(
            workstation = WorkstationType.TANNERY,
            displayName = "Tannery",
            categories = setOf(RecipeCategory.MATERIAL_PROCESSING),
        ),
        WorkstationDefinition(
            workstation = WorkstationType.RESEARCH_DESK,
            displayName = "Research Desk",
            categories = setOf(RecipeCategory.RESEARCH),
        ),
    )
}

data class RecipeTemplateIOData(
    val type: CraftingItemType,
    val variant: CraftingVariantType,
    val amount: Int,
)

fun RecipeTemplateIOData.toItemKey(): ItemKey = ItemKey(type.key, variant.key)

data class RecipeTemplate(
    val name: String,
    val category: RecipeCategory,
    val workstation: WorkstationType,
    val inputs: List<RecipeTemplateIOData>,
    val outputs: List<RecipeTemplateIOData>,
)

sealed interface CraftingList {
    val allRecipes: List<RecipeTemplate>
}

object RootCraftingList : CraftingList {
    override val allRecipes: List<RecipeTemplate> = listOf(
        *IngotSmithing.allRecipes.toTypedArray(),
        *ArmorSmithing.allRecipes.toTypedArray(),
    )

    fun getRecipesFor(workstation: WorkstationType): Map<RecipeCategory, List<RecipeTemplate>> {
        return allRecipes
            .filter { it.workstation == workstation }
            .groupBy { it.category }
    }
}

object ArmorSmithing : CraftingList {
    private val metals = listOf(CraftingVariantType.MetalType.Bronze, CraftingVariantType.MetalType.Iron)
    private val types = listOf(CraftingItemType.WearableType.ArmorType.Helmet)

    override val allRecipes: List<RecipeTemplate> = metals.flatMap { metal ->
        types.map { type -> createArmorRecipe(metal, type) }
    }
}

object IngotSmithing : CraftingList {
    private val category = RecipeCategory.SMELTING
    private val workstation = WorkstationType.FORGE

    val SmithBronzeIngot = RecipeTemplate(
        name = "SmithBronzeIngot",
        category = category,
        workstation = workstation,
        inputs = listOf(
            RecipeTemplateIOData(
                type = CraftingItemType.Ore(),
                variant = CraftingVariantType.OreType.Copper,
                amount = 1,
            ),
            RecipeTemplateIOData(
                type = CraftingItemType.Ore(),
                variant = CraftingVariantType.OreType.Tin,
                amount = 1,
            ),
        ),
        outputs = listOf(
            RecipeTemplateIOData(
                type = CraftingItemType.Ingot(),
                variant = CraftingVariantType.MetalType.Bronze,
                amount = 1,
            ),
        ),
    )

    val SmithIronIngot = RecipeTemplate(
        name = "SmithIronIngot",
        category = category,
        workstation = workstation,
        inputs = listOf(
            RecipeTemplateIOData(
                type = CraftingItemType.Ore(),
                variant = CraftingVariantType.OreType.Iron,
                amount = 1,
            ),
        ),
        outputs = listOf(
            RecipeTemplateIOData(
                type = CraftingItemType.Ingot(),
                variant = CraftingVariantType.MetalType.Iron,
                amount = 1,
            ),
        ),
    )

    override val allRecipes: List<RecipeTemplate> = listOf(
        SmithBronzeIngot,
        SmithIronIngot,
    )
}

/**
 * Combines an armor type (carrying base stats and metal count) with a metal variant
 * to produce a complete RecipeTemplate.
 */
fun createArmorRecipe(
    metal: CraftingVariantType.MetalType,
    type: CraftingItemType.WearableType.ArmorType,
): RecipeTemplate {
    val metalIngredient = RecipeTemplateIOData(
        type = CraftingItemType.Ingot(),
        variant = metal,
        amount = type.metalIngredientCount,
    )
    return RecipeTemplate(
        name = "Forge ${metal.name} ${type.name}",
        category = RecipeCategory.ARMOR_FORGING,
        workstation = WorkstationType.FORGE,
        inputs = listOf(metalIngredient) + type.extraIngredients,
        outputs = listOf(RecipeTemplateIOData(type, metal, 1)),
    )
}
