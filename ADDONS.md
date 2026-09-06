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
