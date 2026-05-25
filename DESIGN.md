# Design Notes

## Item & Crafting System

### Philosophy

Every item in the game is a **derived item** — fully described by a `CraftingItemType` (what it is) and a `CraftingVariantType` (what it's made of). There are no statically-defined "concrete" item objects. A "Copper Ore" is not a stored object — it is the intersection of `CraftingItemType.Ore` and `CraftingVariantType.OreType.Copper`. A "Bronze Helmet" is `CraftingItemType.WearableType.ArmorType.Helmet` and `CraftingVariantType.MetalType.Bronze`.

This means:
- `Item` / `ConcreteItem` as a class does not exist
- `ItemList` as a static object registry does not exist
- Stats, display names, and all other properties are **computed at runtime** from the type + variant combination
- Storage and persistence only ever hold two strings: `typeKey` and `variantKey`

---

### Item Identity & Persistence

```kotlin
data class ItemKey(val typeKey: String, val variantKey: String)

data class ItemInstance(
    val uuid: String,
    val key: ItemKey,
    val quality: Double?,
)
```

`ItemKey` is the only thing persisted per item. Everything else is derived. Lookup is:

```kotlin
val type    = CraftingItemType.fromKey(typeKey)
val variant = CraftingVariantType.fromKey(variantKey)
```

Both `CraftingItemType` and `CraftingVariantType` must expose a stable `abstract val key: String` on every leaf node, and each sealed hierarchy must provide a `fromKey(key: String)` reverse lookup.

---

### `CraftingItemType` — the "what"

Describes the structural role of an item. Carries base stats and crafting metadata. Every leaf node has a stable `key`.

```
CraftingItemType
├── RawStone         key: "raw-stone"
├── RawWood          key: "raw-wood"
├── Ore              key: "ore"
├── Ingot            key: "ingot"
└── WearableType
    ├── ArmorType
    │   └── Helmet   key: "wearable-armor-helmet"   baseDefense: 10.0, metalIngredientCount: 2
    └── ClothingType
        └── Cloak    key: "wearable-clothing-cloak"  baseDefense: 2.0,  threadIngredientCount: 1
```

`extraIngredients` on `CraftingItemType` allows a type to declare additional required crafting materials beyond the primary variant ingredient (e.g. platelegs requiring a leather strip alongside ingots).

---

### `CraftingVariantType` — the "what it's made of"

Describes the material tier or species. Carries stat multipliers that scale the base stats on the type. Every leaf node has a stable `key`.

```
CraftingVariantType
├── StoneType
│   └── Generic      key: "stone-generic"
├── WoodType
│   ├── Generic      key: "wood-generic"
│   ├── Balsa        key: "wood-balsa"
│   ├── Oak          key: "wood-oak"
│   └── ... (16 types total)
├── OreType
│   ├── Copper       key: "ore-copper"    minMiningLevel: 1
│   ├── Tin          key: "ore-tin"       minMiningLevel: 1
│   └── Iron         key: "ore-iron"      minMiningLevel: 15
└── MetalType
    ├── Bronze       key: "metal-bronze"  armorDefenseMultiplier: 0.8, weightMultiplier: 0.9
    └── Iron         key: "metal-iron"    armorDefenseMultiplier: 1.2, weightMultiplier: 1.1
```

---

### Item Taxonomy (all items)

Every item that exists in the game is one of these `(type, variant)` pairs:

| Display Name | Type | Variant |
|---|---|---|
| Rocks | `RawStone` | `StoneType.Generic` |
| Sticks | `RawWood` | `WoodType.Generic` |
| Birch Wood | `RawWood` | `WoodType.Birch` |
| Copper Ore | `Ore` | `OreType.Copper` |
| Tin Ore | `Ore` | `OreType.Tin` |
| Iron Ore | `Ore` | `OreType.Iron` |
| Bronze Ingot | `Ingot` | `MetalType.Bronze` |
| Iron Ingot | `Ingot` | `MetalType.Iron` |
| Bronze Helmet | `WearableType.ArmorType.Helmet` | `MetalType.Bronze` |
| Iron Helmet | `WearableType.ArmorType.Helmet` | `MetalType.Iron` |
| Cloak | `WearableType.ClothingType.Cloak` | *(TBD — needs a cloth/fiber variant)* |

New items are added by adding a type, a variant, or both — never by registering a named object.

---

### Display Names

Display names are computed from type and variant at runtime. The default format is `"${variant.displayName} ${type.displayName}"` (e.g. "Bronze Helmet", "Copper Ore").

For variants where the variant name adds no useful information (e.g. `StoneType.Generic`), the type's display name alone is used (e.g. "Rocks" not "Generic Rocks"). This is handled by a `displayName: String?` override on the variant — `null` means "omit from the computed name".

---

### Stat Derivation

Stats for any item are computed by combining the type's base stats with the variant's multipliers:

```kotlin
fun ItemKey.toWearableStats(): WearableStats? {
    val type    = CraftingItemType.fromKey(typeKey) as? CraftingItemType.WearableType ?: return null
    val variant = CraftingVariantType.fromKey(variantKey) as? CraftingVariantType.MetalType ?: return null
    return WearableStats(
        weight  = 5.0 * variant.statMultipliers.weightMultiplier,
        defense = type.baseDefense * variant.statMultipliers.armorDefenseMultiplier,
    )
}
```

---

### Recipes — `RecipeTemplateIOData`

Recipe inputs and outputs are described with:

```kotlin
data class RecipeTemplateIOData(
    val type: CraftingItemType,
    val variant: CraftingVariantType,
    val amount: Int,
)
```

This is sufficient to describe any item in any recipe. Converting to an `ItemKey` for storage matching or instance creation:

```kotlin
fun RecipeTemplateIOData.toItemKey(): ItemKey =
    ItemKey(type.key, variant.key)
```

Ingredient checking compares an `ItemInstance.key` directly against `RecipeTemplateIOData.toItemKey()`.

---

### Crafting Flow (Target State)

1. Player opens a workstation screen (e.g. Forge)
2. `RootCraftingList.getRecipesFor(WorkstationType.FORGE)` returns recipes grouped by category
3. UI checks each recipe's `inputs` against `storage` — a recipe is available if storage contains `amount` instances matching each input's `ItemKey`
4. Player selects a recipe → matching ingredients are **consumed** from storage
5. A `Task` is created from the recipe with `reqItems` and `outputItems` populated from the recipe's `RecipeTemplateIOData`
6. On task completion, `SimulationEngine.collectTask` creates `ItemInstance`s using `RecipeTemplateIOData.toItemKey()` and adds them to storage

---

### Open TODOs

**Model**
- [ ] Add `abstract val key: String` to every leaf of `CraftingItemType` and `CraftingVariantType`
- [ ] Add `fromKey()` reverse lookup to both hierarchies
- [ ] Add `StoneType.Generic` to `CraftingVariantType`
- [ ] Add `WoodType.Generic` to `CraftingVariantType`
- [ ] Add `CraftingItemType.RawStone` and `CraftingItemType.RawWood`
- [ ] Add `displayName` override (nullable) to `CraftingVariantType` for suppressing "Generic" in display
- [ ] Replace `ItemInstance.template: Item` with `ItemInstance.key: ItemKey`
- [ ] Delete `Item`, `WearableData`, `EnchantableData`, `SellableData`, `QualityData`, `ReqItemData` (item-template form), `OutputItemData` (item-template form)
- [ ] Reconcile `WearableData2` → rename to `WearableDescriptor` or similar; make it the canonical wearable reference
- [ ] Update `OutputItemData` / `ReqItemData` in `Tasks.kt` to reference `ItemKey` instead of `Item`
- [ ] Update `PermanentTasks` entries to use `ItemKey(RawStone.key, StoneType.Generic.key)` etc.

**Crafting**
- [ ] Implement `RecipeTemplateIOData.toItemKey()`
- [ ] Implement ingredient availability check (storage contains required `ItemKey` + amount)
- [ ] Implement ingredient consumption in `SimulationEngine` when a crafting task starts
- [ ] Fix `IngotSmithing.allRecipes` to include `SmithIronIngot`
- [ ] Fix `createArmorRecipe` — `metalIngredientCount` is only on `ArmorType`, not `WearableType`; handle `ClothingType` separately
- [ ] Add a `WorkstationDefinition` registry to active code (currently only in commented-out `RecipeRegistry`)
- [ ] Define cloth/fiber variant for `ClothingType.Cloak` recipe

**UI**
- [ ] Build the Forge/Workbench screen (recipe list + per-recipe ingredient availability)
- [ ] Fishing and Farming subscreen buttons are no-ops
- [ ] Recipe display in `RawResourceTaskList` does not show required ingredients

**Deferred**
- [ ] Enchantment system — `Enchantments.kt` is empty; defer until design is ready
- [ ] `FishType` and `PlantType` have no concrete variants
- [ ] Wood tier levels — all `minWoodcuttingLevel` are placeholder `1`
