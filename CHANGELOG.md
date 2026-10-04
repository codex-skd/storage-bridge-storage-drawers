# Changelog
## [Unreleased]

### Added

- **`LICENSE`**: el mod declara `mod_license=All Rights Reserved` en `gradle.properties`, pero no
  habia fichero de licencia, asi que el snapshot publico no declaraba sus terminos. Ahora lo hace,
  en linea con el resto de mods de la coleccion.

---

Todos los cambios notables de este proyecto se documentan en este archivo.

## [2.0.0] - 2026-09-30

### Cambiado
- **Renombre del mod**: *Storage Bridge* → **Storage Bridge (Storage Drawers)**. Cambio incompatible de identidad (por eso MAJOR):
  - `mod_id`: `storage_bridge` → `storage_bridge_storage_drawers` (JAR `storage_bridge_storage_drawers-1.21.1-neoforge-21.1.249-<version>.jar`).
  - Paquete Java: `com.skd.storagebridge` → `com.skd.storagebridge.storagedrawers`; clase principal `StorageBridge` → `StorageBridgeStorageDrawers`.
  - Logo `storage_bridge.png` → `storage_bridge_storage_drawers.png`.
  - Repos GitLab/GitHub y slug: `storage-bridge` → `storage-bridge-storage-drawers`. Mismo proyecto CurseForge (`1697182`).
- Sin cambios funcionales: las tres integraciones (Apothic-Enchanting Library, Sophisticated Storage, Apotheosis Gem Case) se comportan igual que en 1.0.0. El mod no guarda datos de mundo, así que sustituir el JAR antiguo por el nuevo es seguro.

## [1.0.0] - 2026-09-16

### Quitado
- Mensajes de depuración en la barra de acción de las 3 integraciones (Library, SophisticatedStorage, Gem Case), usados durante la validación en beta.1/beta.2. Confirmado funcionando en un modpack real.

### Cambiado
- Primera versión estable: las tres integraciones (Apothic-Enchanting Library, SophisticatedStorage, Apotheosis Gem Case) quedan confirmadas funcionando en conjunto.

## [0.0.0-beta.2] - 2026-09-16

### Añadido
- Integración con SophisticatedStorage: el mismo clic derecho en el Controller también deposita items en un cofre/barril de SophisticatedStorage (`ChestBlockEntity`/`BarrelBlockEntity`/`LimitedBarrelBlockEntity`, vía `WoodStorageBlockEntity`) alcanzable a través de la red de cajones. Solo mueve items de los que el cofre/barril ya tenga al menos una pila (evita convertir un barril vacío en vertedero genérico). Los libros encantados siguen yendo a la Library, no a estos contenedores.
- Integración con Apotheosis Gem Case/Ender Gem Case (`GemCaseTile`): el mismo clic derecho deposita gemas sin engarzar del inventario del jugador; la propia capability del Gem Case filtra qué es una gema válida, sin lógica de "coincidencia" adicional de nuestro lado. Requiere Placebo como dependencia (usada internamente por Apotheosis).
- Refactor: la búsqueda BFS por la red de cajones se extrajo a `ControllerNetworkSearch` (utilidad compartida por todas las integraciones `compat.*`), reutilizada por la integración de la Library sin cambiar su comportamiento.

## [0.0.0-beta.1] - 2026-09-16

### Añadido
- Estructura inicial del repositorio (docs, workflow, CI/CD).
- Esqueleto Gradle/NeoForge del mod (`StorageBridge`, dependencias reales de StorageDrawers y Apothic-Enchanting en `libs/`).
- Depósito de libros encantados desde el inventario del jugador (mano vacía o sujetando un libro encantado) hacia una Library de Apothic-Enchanting (`apothic_enchanting:library` / `apothic_enchanting:ender_library`), al hacer clic derecho en la cara frontal del Controller de StorageDrawers (misma interacción vanilla de "guardar todo").
- La búsqueda de la Library recorre la red de cajones conectada al Controller (misma cadena de bloques `INetworked` y rango `controllerRange` que usa el propio StorageDrawers), no solo sus 6 vecinos directos.

### Cambiado
- Sustituido el primer enfoque (drenaje automático en segundo plano cada 100 ticks desde los cajones) por la interacción explícita descrita arriba — el diseño original no reflejaba lo que pedía el usuario.
