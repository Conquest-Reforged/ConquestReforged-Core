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
