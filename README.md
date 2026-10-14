# Storage Bridge (Storage Drawers)

Automatic item transfer between Storage Drawers controllers and storage inventories from other mods.

> Formerly **Storage Bridge** (`storage_bridge`). Renamed in 2.0.0 to `storage_bridge_storage_drawers`; the mod stores no world data, so replacing the old jar with the new one is safe.

Storage Bridge (Storage Drawers) lets a **Storage Drawers Controller** exchange matching items directly with inventories exposed by other storage mods, without hoppers or pipes, using each mod's public `IItemHandler` capability.

## Features

- **Apothic-Enchanting Library integration** — right-clicking a Storage Drawers Controller's front face (empty hand, or holding an enchanted book) moves any `minecraft:enchanted_book` stacks from the player's inventory into an Apothic-Enchanting Library (`apothic_enchanting:library` "Enchantment Library" or `apothic_enchanting:ender_library` "Library of Alexandria") reachable through the Controller's drawer network. One-directional: the Library consumes every book it accepts and cannot return them.
- **Sophisticated Storage integration** — the same right-click gesture also moves items into a reachable Sophisticated Storage chest or barrel, but only items the chest/barrel already contains a matching stack of (never dumps unrelated items into an empty container).
- **Apotheosis Gem Case integration** — the same right-click gesture also moves unsocketed gem stacks into a reachable Apotheosis Gem Case or Ender Gem Case; the Gem Case's own capability rejects anything that isn't a valid gem.

## Requirements

| | |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.249+ |
| Storage Drawers | Required |
| Apothic-Enchanting | Required (Library integration) |
| Sophisticated Storage | Required (chest/barrel integration) |
| Apotheosis + Placebo | Required (Gem Case integration) |

All five are declared as `required` dependencies in the mod metadata (`neoforge.mods.toml`).

## Building from source

```
./gradlew build
```

The built jar is placed in `build/libs/`.
