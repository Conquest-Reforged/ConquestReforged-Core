# Registering content from another namespace

Core used to assume every block, item, tag and creative-tab entry it touched lived in the
`conquest` namespace. It no longer does: third party mods and addons can register their own
content through the same builder API under their own namespace.

`conquest` is still the default everywhere, so the first-party submodules (Classical, Medieval,
Asian, Modern, EarlyModern, Main) are unaffected — they don't need to change anything.

## The namespace registry

`com.conquestrefabricated.core.Namespaces` is the single place that knows which namespaces are
"ours". Register yours once, early — from your mod initializer, before you register any blocks:

```java
Namespaces.register("myaddon");
```

Or, if you want everything of yours that isn't listed in a creative-tab ordering file to land in
a particular Conquest tab:

```java
Namespaces.register("myaddon", "rr_utility");
```

Registering makes core's block-data registry, asset/lang/loot/tag generation, block stats and
creative tabs treat your namespace the same way they treat `conquest`.

## Blocks

`Props` (and everything built on it, ie `VanillaProps`) resolves unqualified names and textures
against a namespace that defaults to `Namespaces.DEFAULT` (`conquest`). There are three ways to
point it at yours.

**1. Per builder** — `namespace(..)` can go anywhere in the chain, because names and textures are
resolved lazily at registration time:

```java
VanillaProps.stone()
        .namespace("myaddon")
        .name("basalt_ashlar")
        .texture("*", "block/basalt_ashlar")
        .register(types);
```

**2. Around a whole init block** — the previous default is restored afterwards, so this is safe to
wrap around your own registration call without leaking into anyone else's:

```java
Props.withDefaultNamespace("myaddon", () -> {
    ColumnsInit.init(types);
    RoofingInit.init(types);
});
```

**3. Qualified inline** — an explicit `namespace:path` always wins over the builder's namespace:

```java
VanillaProps.stone()
        .name("myaddon:basalt_ashlar")
        .texture("*", "myaddon:block/basalt_ashlar")
        .family("conquest:andesite")   // families may point at another namespace
        .register(types);
```

`namespace(..)` also registers the namespace for you, so an explicit `Namespaces.register` call is
only needed if you want to declare a fallback creative tab.

Note that `family(..)` resolves immediately (it looks the parent block up in the registry), so
either qualify its argument or call `namespace(..)` before it.

## Block lore

`Props.lore(..)` adds tooltip lines to the item form of every block a builder registers:

```java
VanillaProps.stone()
        .name("basalt_ashlar")
        .lore("Quarried from the black cliffs.", "Favoured by the old masons.")
        .register(types);
```

The text you pass is the **English (en_us) source**. The lang datagen writes it out against a
generated key; translators override that key in their own lang file, and nothing in the game reads
your Java string at runtime.

**Lore belongs to the family, not to the block or the `register(..)` call.** The key comes from
the family id, so one entry covers every shape — and every *pass*. A family that has to be split
across several registrations declares its lore once and the rest inherit it by pointing at the same
`family(..)`:

```java
VanillaProps.stone()
        .name("marble_pediment_raking")
        .lore("§9The raking crowns and frames the", "§9pediment of a classical temple.")
        .register(TypeList.of(HingeToggle3.class));

VanillaProps.stone()
        .name("marble_pediment_raking_summit")
        .family("marble_pediment_raking")     // same lore, no second .lore(..) call
        .register(TypeList.of(HalfToggle4.class));

VanillaProps.stone()
        .name("marble_pediment_raking_vertical_slab")
        .family("marble_pediment_raking")
        .register(TypeList.of(PedimentVerticalSlab.class));
```

All four blocks share one set of keys:

```
lore.conquest.marble_pediment_raking          # a single line uses the bare key
lore.conquest.marble_pediment_raking.0        # several lines are indexed
lore.conquest.marble_pediment_raking.1
```

The id is the `family(..)` target when there is one, otherwise `<namespace>:<singular name>` — so
the family's root block, whose name is what everyone else's `family(..)` points at, lands on the
same id without needing to declare a family of its own.

It follows that members of one family cannot have *different* lore: the second declaration is
ignored and logged. Declare it on whichever member reads best — the lookup happens when the
tooltip is drawn, so registration order doesn't matter.

**It appends to annotation lore.** Blocks whose class carries `@ItemDescription` (the stock "3
Toggleable Variants (Right-Click)" style hints) keep it; yours is added underneath, in the order
you passed the lines. Calling `.lore(..)` twice appends rather than replacing.

**It is collapsed behind SHIFT.** However many lines a block has, the tooltip shows a single green
`Hold SHIFT to reveal description...` until the player holds shift, then the lines are written out
in full. A block with no lore shows no hint, and `@ItemDescription` hints are never collapsed —
they are functional information, not flavour text.

The hint's own text is the lang key `tooltip.conquest.hold_shift`, which Core ships, so addons get
it for free. Its colour is `Lore.HINT_COLOR` if you are building Core and want blue instead.

Run the lang datagen after adding lore. If two families end up sharing a name, they share a lore
key too — datagen keeps the first and logs a warning naming the key.

## Crafting tools

A set of crafting tools is a held item that opens a stonecutter-style picker: put an ingredient in
the input slot, pick a shape from the grid, take the block out. Core ships three —
`CraftingTools.WOODWORKING`, `CraftingTools.MASON` and `CraftingTools.METALWORKING` — and blocks
opt in through `Props`:

```java
VanillaProps.stone()
        .name("granite_ashlar")
        .craftedWith(CraftingTools.MASON.id(), Blocks.GRANITE)
        .register(types);
```

The ingredient can be named in whichever way is handiest, with an optional yield — see
[Naming an ingredient](#naming-an-ingredient):

```java
        .craftedWith(CraftingTools.WOODWORKING.id(), ItemTags.PLANKS, 4)
        .craftedWith(CraftingTools.MASON.id(), ModTags.STONE)
        .craftedWith(CraftingTools.MASON.id(), "granite_ashlar")
```

**Only the family's parent gets the recipe.** That is the block a builder registers first, or
whatever `parent(..)` was pointed at. Every other member of the `TypeList` — slab, stairs, wall,
vertical slab — is cut from that parent by the stonecutting recipes Core already generates, so one
`craftedWith(..)` call covers the whole family. Run the recipe datagen after adding it.

A builder whose `parent(..)` or `family(..)` points at someone else's block has no parent of its own,
so `craftedWith(..)` on it generates nothing — its blocks are already reachable by stonecutting from
that other family's parent, and that parent is where the tool recipe belongs.

### Reaching the rest of the family

A tool recipe is only ever written for the family's parent, but the picker can still offer the whole
family. The toggle under the input slot switches between two lists — never both at once:

- `+` — **base blocks.** The tool recipes that accept what is in the input slot.
- `-` — **family shapes.** Everything one stonecutting step from the input itself, at the cut's own
  yield. No recipe file per shape, and datapack edits to those cuts are picked up for free.

So the flow is two passes: granite in, take the ashlar block on the base list; put the ashlar block
back in, switch to family shapes, take its slab or stairs.

Family shapes are gated by `StationMenu.worksWith(..)`, because the stonecutting graph knows nothing
about which station is open — without it a set of woodworking tools would happily cut granite. A set
of tools works a material it has a recipe for, plus anything it made itself, so its own output can go
back in to be shaped.

Stations opt in with `StationMenu.supportsVariants()`; the crafting tools do, the arms station does
not, since nothing is cut from iron.

For one-off recipes that don't belong to a block family, build them directly:

```java
ToolCraftingRecipeBuilder.toolCrafting(CraftingTools.MASON.id(), Blocks.CLAY, ModBlocks.ROOF_TILES)
        .count(4)
        .save(this.output);
```

### Shaping something with no recipe

The recipe graph is how a station knows what it may work, which leaves out anything it neither makes
nor consumes. Vanilla stone bricks already have a family of slabs, stairs and walls, but no mason
recipe touches them, so the family toggle offers nothing for them.

Each station reads an item tag of extra materials it will shape anyway:

| Station | Tag |
|---|---|
| Mason's tools | `conquest:mason_tools/shapes` |
| Woodworking tools | `conquest:woodworking_tools/shapes` |
| Metalworking tools | `conquest:metalworking_tools/shapes` |
| Loom | `conquest:loom/shapes` |
| Pottery wheel | `conquest:pottery_wheel/shapes` |
| Painter's kit | `conquest:painters_kit/shapes` |

Put one in `src/main/resources/data/conquest/tags/item/mason_tools/shapes.json`:

```json
{
  "values": [
    "minecraft:stone_bricks",
    "minecraft:deepslate_bricks",
    "#minecraft:stone_bricks"
  ]
}
```

Anything listed can be put in the input slot and cut into its family with the `+` toggle, at whatever
yield the stonecutting recipes already give. Nothing appears on the base list — that is still driven
by real recipes — so the flow is: material in, toggle on, take the shape.

**Write these by hand under `resources`, not `generated`.** Core does not generate them, precisely so
that a datagen run cannot overwrite your whitelist. Tags merge, so every module can contribute to the
same one; a module that would rather do it programmatically can append to the same tag from its own
item tag provider:

```java
this.valueLookupBuilder(CraftingTools.MASON.shapesTag()).add(Items.STONE_BRICKS);
```

An undefined tag is simply empty, so a station with no whitelist behaves exactly as before.

### Adding your own tool set

Tool sets are told apart by id on a shared `conquest:tool_crafting` recipe type, so a new one needs
an item and a menu type rather than a new recipe type:

```java
public static final CraftingTool GLASSBLOWING =
        CraftingTools.register(CraftingTool.of(Identifier.fromNamespaceAndPath("myaddon", "glassblowing_tools")));
```

Then register what its registration phase hands you — `GLASSBLOWING.createItem()` into
`BuiltInRegistries.ITEM` under `itemKey()`, `GLASSBLOWING.createMenu()` into `BuiltInRegistries.MENU`
under `itemId()` — and on the client point the menu type at `StationScreen::new`. Core's
`CraftingToolsInit` on each loader does exactly this for every set in `CraftingTools.all()`, so
registering before Core's init runs is enough to be picked up. The item needs a model, and lang
entries for `item.<ns>.<path>`, `container.<ns>.<path>` and `tooltip.<ns>.item.<path>`.

## Arms station recipes for your gear

Core generates these for you — one call in your recipe provider covers every piece of armour and
every weapon your module registers:

```java
ArmsStationRecipeBuilder.Generated gear = ArmsStationRecipeBuilder.allEquipment(output, items);
```

Each piece gets a recipe taking the matching vanilla input, so any chestplate reforges into any of
your chestplates, any sword into any of your swords, and the input's material carries across:

| registered as | input |
|---|---|
| `ArmorType.HELMET` / `CHESTPLATE` / `LEGGINGS` / `BOOTS` | `#minecraft:head_armor` / `chest_armor` / `leg_armor` / `foot_armor` |
| `WeaponType.SWORD` / `AXE` / `SPEAR` | `#minecraft:swords` / `axes` / `spears` |
| `WeaponType.BOW` / `CROSSBOW` / `SHIELD` | `minecraft:bow` / `crossbow` / `shield` |

### Registering so the generator can see it

Item components are **not bound during data generation** — `Item.components()` throws there — so the
generator cannot look at an item and work out that it is a helmet. It reads what you recorded at
registration instead, which means gear has to go through `ModItemHelper`:

```java
helper.armor("crusader_chestplate", ArmorMaterials.IRON, ArmorType.CHESTPLATE);

helper.sword("bastard_sword", ToolMaterial.IRON, 4.0F, -2.4F, 0.0F, 3.5F, 0.1F);
helper.axe("bearded_axe", ToolMaterial.IRON, 6.0F, -3.1F, 0.0F, 3.0F, 0.1F);
helper.bow("english_longbow", 384, 15);
helper.crossbow("arbalest", 465, 15);
helper.shield("heater_shield", 336);
```

Anything with a shape those don't cover — a pike, a lance, an animated subclass — goes through the
general form, which takes your own factory:

```java
helper.weapon(WeaponType.SPEAR, "pike", properties ->
        new Item(ModItemHelper.pike(properties, ToolMaterial.IRON, 1.2F, 0.0F, 5.0F, 0.1F)));
```

A weapon registered through plain `register(..)` has no recorded kind and is silently passed over.
`Generated.skipped()` counts those, and Core logs the number, so a mismatch shows up in the datagen
output rather than as a missing recipe in game.

## Workstations: the loom and the pottery wheel

A workstation is a block you stand at with slots of its own — unlike the handheld kits, it keeps its
material and keeps working with nobody watching. It works like the crafting tools — input slot, picker, family toggle — with two differences:

- **Weaving takes time.** Picking a cloth starts a craft that ticks down in the block, and the loom
  keeps going with the screen shut. The bar beside the result slot shows how far along it is.
- **Family shapes are instant.** A cut is shaping, not weaving, so selecting one works through the
  whole input stack in a single tick.

Blocks opt in through `Props`, the same way they do for the crafting tools:

```java
VanillaProps.cloth()
        .name("red_canvas")
        .woven(Blocks.RED_WOOL)
        .register(types);
```

The ingredient is named the same way `craftedWith(..)` names one — see
[Naming an ingredient](#naming-an-ingredient) — with an optional yield and time in ticks:

```java
        .woven(ItemTags.WOOL, 2, 160)
```

Leaving the time out gives `WeavingRecipe.DEFAULT_TIME` — 100 ticks, the same five seconds an iron
ingot takes to smelt. **Only the family's parent gets the recipe**, exactly as with `craftedWith(..)`;
the rest of the family is reached through the picker's family toggle, so one `woven(..)` call covers
the whole `TypeList`. Run the recipe datagen after adding it.

For one-off recipes that don't belong to a block family, build them directly:

```java
WeavingRecipeBuilder.weaving(Items.STRING, ModBlocks.LINEN)
        .count(2)
        .time(160)
        .save(this.output);
```

Recipes land under `<result namespace>:<result path>_from_loom` and look like this:

```json
{
  "type": "conquest:weaving",
  "ingredient": "minecraft:red_wool",
  "result": { "id": "conquest:red_canvas", "count": 1 },
  "time": 100
}
```

`time` and `count` are both optional, and the generator leaves them out when they are at their
defaults — so a plain five-second recipe writes just the `ingredient` and the `result`.

A `time` of `0` makes a recipe of your own instant, in the same way family shapes are. Use it
sparingly — it is what separates a loom from a set of tools.

### Adding another workstation

The loom and the pottery wheel are the same machine wearing different hats, and the shared parts are
in `content.station`:

| Piece | What it gives you |
|---|---|
| `TimedStationRecipe` | the on-disk shape — ingredient, result, optional `time` |
| `WorkstationBlockEntity` | two slots, the remembered job, the ticking craft, hopper faces |
| `WorkstationMenu` | the picker, the confirm/stop control, the family toggle |
| `WorkstationScreen` | the progress bar and that control, drawn |

A new one is four thin classes and a holder. The pottery wheel is the worked example — its recipe,
menu and block entity are about thirty lines each, and its screen is six:

```java
public class PotteryWheelScreen extends WorkstationScreen<PotteryWheelMenu> {
    @Override protected String langPrefix() { return PotteryWheelStation.LANG_PREFIX; }
}
```

`langPrefix()` is how each station names its own work: the screen appends `.confirm`, `.stop`,
`.no_selection` and `.progress`, so a loom says *Weaving: 40%* where a wheel says *Shaping: 40%*.

**Each station keeps its own recipe type.** A wheel should not offer a loom's cloths, and a recipe
type is the cheapest way to say so — they share everything except the two lines naming the type and
serializer.

### Throwing on the wheel

Blocks opt in exactly as they do for the loom:

```java
VanillaProps.stone()
        .name("terracotta_amphora")
        .thrown(Items.CLAY_BALL)
        .register(types);
```

Recipes land under `<result namespace>:<result path>_from_wheel` as `conquest:pottery`. The same
family rule applies, and the same yield and time arguments.

The wheel is **two blocks**, foot and head, like a bed. Only the foot carries the block entity; the
head sends the player to it, so either half opens the same wheel.

### What the loom draws

The weave on the block follows its slots: the finished cloth in the output slot if there is one,
otherwise whatever is going in, otherwise nothing. Only a product with a weave of its own shows —
`LoomWeaves` holds that table, and it is also what `HAS_THREAD` is set from, so a loom part way
through a job with plain wool in it is drawn bare rather than in a fallback white.

Adding a weave means adding a sprite under
`conquest:block/7_tools/3_utility/loom/weaves/<size>/loom_weave_<name>` for each size and an entry in
`LoomWeaves`. The ids and their indices are the ones looms have saved since 1.20, so they must not be
reordered — a loom placed long ago stores the product id and nothing else.

Looms saved before the loom had slots are translated on load: the cloth they were dressed with is put
back into the output slot, which leaves the weave identical while making it something a player can
take out again. If that cloth is not a registered item — a rug from a module that is not loaded — the
loom keeps drawing what it always drew rather than being stripped.

## Painting with a painter's kit

The painter's kit is a held item like the crafting tools, with one difference that runs all the way
down: it takes **two** inputs. A base material in the upper slot, and something to colour it with in
the lower one.

```java
VanillaProps.stone()
        .name("red_stucco")
        .painted(Blocks.COBBLESTONE, Items.RED_DYE)
        .register(types);
```

The base is usually a whole family of materials, so there is an overload for that:

```java
        .painted(ModTags.PLASTER, Items.LIME, 4)
```

Only `(ItemLike, ItemLike)` and `(TagKey, ItemLike)` have overloads of their own — a full grid of
them would be sixteen methods. Wrap anything else with `RecipeIngredient.of(..)`, which covers tags,
ids and bare strings alike:

```java
        .painted(RecipeIngredient.basesOf(ModTags.PLASTER), RecipeIngredient.of("lime_wash"))
```

Recipes land under `<result namespace>:<result path>_from_painting`:

```json
{
  "type": "conquest:painting",
  "base": "minecraft:cobblestone",
  "paint": "minecraft:red_dye",
  "result": { "id": "conquest:red_stucco", "count": 1 }
}
```

For one-off recipes outside a block family, `PaintingRecipeBuilder.painting(base, paint, result)`.

### What the picker shows

Put a base in on its own and the kit lists **everything that base could become**, with each option
marked by the paint it is waiting for — so you can see that cobblestone takes lime or a dye without
having to guess. Those options are drawn on vanilla's disabled-slot background, their tooltip reads
`Needs: Red Dye`, and clicking them does nothing. Add the paint and they come alive.

That is the one place the kit departs from the other stations, which only ever list what they can make
right now. Two ingredients make a picker that hides everything until both are in far too quiet.

### Reshaping costs no paint

The family toggle behaves differently here, deliberately. A kit offers family shapes **only for blocks
it painted itself**:

- red stucco in the base slot → its slabs, stairs and walls, and **no paint is spent** cutting them.
- cobblestone in the base slot → nothing. Being able to paint cobblestone should not turn a painter's
  kit into a stonecutter.

That is `PaintersKitMenu.worksWith(..)` narrowed to "did this kit produce it", where the crafting
tools also accept anything they have a recipe for.

## The lime cycle

Three items in Core, and one tag that decides what feeds them.

| Item | Made by | From |
|---|---|---|
| Quicklime | smelting | `#conquest:lime_sources` |
| Slaked Lime | shapeless crafting | quicklime + a water bucket (the bucket comes back) |
| Slaked Lime | dropping it in water | quicklime, wherever it lands |
| Lime Plaster | shapeless crafting | slaked lime + `#minecraft:sand`, yielding 2 |

`#conquest:lime_sources` is an **item** tag built as the union of four block tags, plus vanilla
calcite:

```json
{ "values": [
    { "id": "#conquest:natural_marble",    "required": false },
    { "id": "#conquest:natural_limestone", "required": false },
    { "id": "#conquest:natural_chalk",     "required": false },
    { "id": "#conquest:natural_calcite",   "required": false },
    "minecraft:calcite" ] }
```

So **a module joins the lime cycle by tagging a block and nothing else** — add
`ModTags.NATURAL_CHALK` to your chalk in `Props.tags(..)` and it burns to quicklime. The sub-tags are
`required: false`, so a pack without a given module still loads.

Those four are listed in `ModTags.MIRRORED_TO_ITEMS`, the block tags Core publishes as item tags of
the same id whether or not a recipe asks for one — ordinary block tags are only mirrored when
something is crafted from them (see [How a block tag reaches a recipe](#how-a-block-tag-reaches-a-recipe)),
but a tag that other tags are *built from* has to exist regardless, since a tag can only include tags
from its own registry.

**Each module mirrors its own blocks.** Core registers almost none, so the item tags it writes are
empty files that the content modules merge into — which only happens in a module whose data
generator runs `ModItemTagProvider`.

### Slaking in the world

Quicklime turns to slaked lime the moment its dropped stack touches water, with a hiss and a puff of
steam. There is no loader-neutral hook for "this item entity is in water", so each loader carries a
one-line `ItemEntityMixin` that calls `Lime.slakeInWater(..)`; all the deciding is in that one shared
method.

## Naming an ingredient

`craftedWith(..)` and `woven(..)` both take their input as a `RecipeIngredient`, and there is an
overload for every way you might have of naming one:

| You have | Write | Goes into the recipe as |
|---|---|---|
| a static block or item | `Blocks.GRANITE` | `"minecraft:granite"` |
| an item tag | `ItemTags.PLANKS` | `"#minecraft:planks"` |
| a block tag | `ModTags.STONE` | `"#conquest:stone"` |
| an id | `Identifier.parse("conquest:granite_ashlar")` | `"conquest:granite_ashlar"` |
| an id, as a string | `"granite_ashlar"` | `"conquest:granite_ashlar"` |
| a block tag, whole blocks only | `RecipeIngredient.basesOf(ModTags.BRICKS)` | `"#conquest:bricks/bases"` |

Most Conquest blocks are built from templates rather than declared one by one, so there is no static
field to point at. The last two rows are for those: a bare string path resolves against the Conquest
namespace, exactly as `ModTags.blockTag(..)` does, so `"granite_ashlar"` and
`"conquest:granite_ashlar"` mean the same thing while `"minecraft:stone"` reaches outside.

**Name Conquest's own content by id, not by static field.** Blocks and items are created during
registration, and blocks are declared first, so a static holding one of Core's items is still null
while a module declares its blocks — capturing it there stores that null. Use the id constant beside
it instead:

```java
.painted(RecipeIngredient.basesOf(ModTags.BRICKS), RecipeIngredient.of(Lime.LIME_PLASTER))     // null
.painted(RecipeIngredient.basesOf(ModTags.BRICKS), RecipeIngredient.of(Lime.LIME_PLASTER_ID))  // works
```

A null ingredient is rejected where it is declared, with a message saying exactly that, rather than
surfacing as an NPE inside data generation much later.

**The id does not have to resolve where the data is generated.** Modules generate separately but are
read together, so a Classical block crafted from a Main one is ordinary, and no module should have to
depend on every other just to name a block. An id that is not registered in the generating module is
written out as it stands, with a warning in the generator's log:

```
Recipe ingredient 'conquest:pale_limestone' is not registered in the module generating this data.
Writing it as-is - correct if that id is a typo, expected if it belongs to another module.
```

Nothing can tell a sibling module's block from a typo, so that is a warning rather than a failure.
The cost is at the other end: if the module owning that id is *not* installed, the recipe fails to
load and Minecraft logs a parse error for it. Where that matters — an ingredient that should simply
be absent rather than broken when a module is missing — name a **tag** instead, which resolves to
nothing quietly.

**Block tags are usually the useful one**, since that is already how a whole family is grouped. Tags
of either kind go through the same `TagKey<?>` overload — `TagKey<Item>` and `TagKey<Block>` erase to
the same signature, so they cannot be separate overloads — and anything that is neither is rejected
where it is declared.

### Excluding the shapes cut from a family

`Props` are shared by every member of a family, so a block tag holds the whole family: `ModTags.BRICKS`
is every brick *shape*, not every brick — 264 entries in Classical, for 12 blocks. Feed that to an
ingredient and a slab counts as a brick, which is usually a way to lose material rather than a
feature.

`basesOf(..)` takes only the block each family is built from:

```java
.craftedWith(CraftingTools.MASON.id(), RecipeIngredient.basesOf(ModTags.BRICKS))
```

That writes `#conquest:bricks/bases`, and Core generates that tag beside the full one — so the same
block tag can be read both ways by different recipes. Plain `of(..)` still takes the whole family,
which is what you want for an ingredient like "any log shape I have lying around".

"Base" here is the same notion as the picker's own base-blocks-versus-shapes toggle: a block is a base
if it has no parent, or is its own family's parent. It is the identical test that decides which member
of a family gets the tool recipe in the first place.

### How a block tag reaches a recipe

A recipe can only ever match on an **item** tag, so a block tag ingredient is written out as the item
tag with the same id. For most vanilla block tags that counterpart already exists
(`BlockTags.PLANKS` and `ItemTags.PLANKS` are both `minecraft:planks`). Conquest's own tags are
declared as block tags only, so `ModItemTagProvider` mirrors them: every block tag used as an
ingredient gets an item tag of the same id, filled with the items of the blocks carrying it.

That mirroring is why declaring `craftedWith(MASON.id(), ModTags.STONE)` just works. It only covers
tags actually used as an ingredient, so it stays empty until you craft from one, and picks up new
ones on its own — but it does mean the recipe and tag datagen have to be run together, which they are.

## Module credit on advanced tooltips

With advanced tooltips on (F3+H), a block's item shows which Conquest module it came from, in blue
italics under the lore.

A module is **not** the same thing as a namespace. Every first-party submodule registers its blocks
into the one `conquest` namespace while shipping under its own mod id, so a registry name can't say
which module built a block.

**Usually you don't have to do anything.** Core infers the module from the call stack: the first
frame outside Core is your init class, and the mod id is read out of its package root — so
`com.myaddon.content.blocks.init.StoneInit` is credited to `myaddon`. The guess is only accepted if
a mod with that id is actually loaded, so a package that doesn't follow the `com.<modid>` convention
falls through to Core rather than inventing a module.

Declare it explicitly when your package root isn't your mod id, or when you want a specific
display name:

```java
Modules.register("myaddon", "My Addon");            // id -> display name
Modules.scope("myaddon", BlockRegistrar::blocks);   // everything inside is tagged
```

`scope` restores the previous module afterwards, so it nests safely and modules registering before
or after are unaffected. It also overrides inference, which makes it worth using if you register
blocks from a helper that lives in someone else's package. A single builder can be tagged with
`Props.module("myaddon")`.

The display name resolves in this order:

1. the name passed to `Modules.register(id, name)`
2. the name your mod metadata declares (`fabric.mod.json` `name` / `neoforge.mods.toml`
   `displayName`), via architectury
3. the module id itself

Core registers short names for its own family — `Classical`, `Medieval`, `Modern`, `Early Modern`,
`Asian`, `Main` — because their metadata names disagree with each other and read poorly in a
tooltip.

Translators can override the resolved name with the lang key `module.<id>.name`. Nothing breaks if
it is absent; the resolved name is used as the fallback.

Blocks Core can neither infer nor be told about fall back to `conquest` / "Conquest Reforged".

## Tags

`ModTags.blockTag(..)` / `ModTags.itemTag(..)` are public and take either a bare path (resolved
against `conquest`) or a qualified one:

```java
public static final TagKey<Block> MY_TAG = ModTags.blockTag("myaddon:basalts");
```

## Creative tabs

### Adding to Conquest's tabs

Core's tabs are ordered by `assets/<namespace>/groups/<label>.txt` files. Every registered
namespace gets a chance to contribute to every tab, and all copies of a given file on the
classpath are read rather than just the first — so ship your ordering under your own namespace:

```
assets/myaddon/groups/k_stone.txt
```

with one item id per line (`myaddon:basalt_ashlar`). Entries are appended after ours, in namespace
registration order. Anything you don't list falls into the tab you passed to
`Namespaces.register(namespace, tabLabel)`, if you declared one.

### Creating your own tabs

`AddonGroups.create(..)` registers a tab under your namespace, so `myaddon:a_stone` never collides
with a Conquest tab even if you reuse one of their labels:

```java
public static CreativeModeTab STONE;

public static void init() {
    STONE = AddonGroups.create("myaddon", "a_stone", 40, AddonGroups.icon("myaddon:basalt_ashlar"));
}
```

Call it during mod init, before your blocks register. `order` decides where the tab sorts among
Conquest's (theirs run 0–34) and also picks its row and column, so use consecutive numbers.
Icon helpers: `AddonGroups.icon(blockId)`, `AddonGroups.icon(blockId, fallbackId)` for an icon that
may not be installed, and `AddonGroups.itemIcon(itemId)`.

The tab's title comes from a lang key of the form `itemGroup.<namespace>.<label>`:

```json
{ "itemGroup.myaddon.a_stone": "Stone" }
```

### Putting content in a tab

Two mechanisms, and you generally want both:

- **`Props.group(tab)`** assigns a block *family* to the tab. This is what ties the family to the
  tab for the palette. Note that Core switches tabs to "root items only" during client init, so on
  its own this shows one entry per family, not every shape.
- **The tab's `groups/<label>.txt`** lists the ids that appear in the tab and the order they appear
  in. Anything named there that exists is added, so this is how you list every shape of a family —
  and the only way to place a **plain item**, which has no family.

A tab with no blocks assigned to it is perfectly valid: its entire contents then come from its
`.txt` file.

## Asset generation

The datagen providers (`ModLangProvider`, `ModLootTableProvider`, `ModBlockTagProvider`,
`ModModelProvider`, `ModRecipeProvider`, `WorldPainterGenerator`) all iterate registered
namespaces, so running datagen with your mod on the classpath generates lang entries, loot tables,
tags, models and recipes for your blocks under `assets/myaddon/` — the same pipeline the
submodules use. Register your namespace before datagen runs.
