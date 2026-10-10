# Agent Handoff: Chronon Wand Mod Development

## 1. Project Overview & Current State of Reasoning

### About the Project
- **Mod Name**: Chronon Wand
- **Mod ID**: `chronon_wand`
- **Environment**: NeoForge 21.1.228+ for Minecraft 1.21.1, Java 21
- **Current Version**: `2.0.1` (defined in [`gradle.properties`](file:///e:/Owen/Projects/Mods/Chronon_wand/gradle.properties))
- **Workspace Location**: `e:\Owen\Projects\Mods\Chronon_wand`
- **Prism Launcher Test Instances**:
  1. `E:\Games\PrismLauncher\instances\Chronon wand\minecraft\`
  2. `E:\Games\PrismLauncher\instances\All the Mods 10 Aeronautics\minecraft\`
  3. `E:\Games\PrismLauncher\instances\ATM10SKY Aeronautics\minecraft\`

### Current Architecture & Mechanics
1. **Chronon Power System**:
   - Integrated with Tempad's TVA Chronon API (`earth.terrarium.tempad.api.tva_device.ChrononHandler`).
   - The mod adds tools (Chronon Wand, storage cells) and machines (Metronome Port, Chronon Accelerator).
   - Neither the Metronome Port nor the Chronon Accelerator connect wirelessly to player metronomes (they only use adjacent Metronome blocks or direct fluid piping).
2. **Chronon Accelerator Block**:
   - A multi-tier, area-based tick acceleration block inspired by *Just Dire Things* (JDT) machines.
   - **Orientation**: Full 6-directional block (`FACING` property: `UP`, `DOWN`, `NORTH`, `SOUTH`, `EAST`, `WEST`).
   - **Power**: Requires Chronon fluid piped directly into its internal buffer (10,000 capacity) or a Metronome block placed adjacent to it. Does not overwrite the internal buffer when adjacent to a Metronome.
   - **Area Configuration**:
     - Configurable X, Y, Z Radius (supports half-steps: `0.0`, `0.5`, `1.0`, ... up to `5.0`).
     - Configurable X, Y, Z Offset (from `-9` to `+9`). Default offset when placed is `+1` in the direction the block faces.
   - **In-World Rendering**:
     - Custom Block Entity Renderer ([`ChrononAcceleratorRenderer`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/renderer/ChrononAcceleratorRenderer.java)).
     - Uses green wireframe and translucent green solid fill box.
     - Extends out by `0.001m` (`inflate(0.001)`) to avoid z-fighting without visible seams.
     - Uses [`ChrononRenderTypes.TRANSLUCENT_BOX`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/renderer/ChrononRenderTypes.java) with `COLOR_WRITE` (depth writing disabled) so adjacent/overlapping selection boxes never occlude boxes behind them.
3. **Tick Acceleration Support**:
   - Centralized via [`BlockTickHelper`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/util/BlockTickHelper.java).
   - Standard block ticking & random block ticks.
   - Standard block entities with `BlockEntityTicker`.
   - **AE2 Machines**: Supported dynamically via optional runtime integration ([`AE2TickHelper`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/AE2TickHelper.java) and [`AE2TickHandler`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/ae2/AE2TickHandler.java)) without crashing if AE2 is absent.
   - **Deny Tags Checked**:
     - `#chronon_wand:acceleration_deny`
     - `#justdirethings:tick_speed_deny`
     - `#tiab:un_acceleratable`
4. **Chronon Crystal Progression System (Option B Automation)**:
   - Full progression line implemented based on `Chronon crystal plans.xlsx`:
     1. `unfinished_chronon_crystal`: Crafted via Create Mechanical Crafter (center: `tempad:time_twister`).
     2. `chronon_infused_chronon_crystal`: 69,420 cycles in Mekanism Metallurgic Infuser (1,500 mB Chronon chemical per cycle).
     3. `chronon_infused_chronon_anticrystal`: 1,001 cycles in Mekanism Nucleosynthesizer (1 mB Antimatter chemical per cycle).
     4. `antitime_fluid`: Created in Create Mechanical Mixer from 2,000 mB JDT Time Fluid + 1 mB Mekanism Antimatter fluid.
     5. `antitime_warped_chronon_infused_chronon_anticrystal`: 70 cycles in PneumaticCraft Thermo Plant with Antitime Fluid at $\le 100\text{ K}$ (2 mB per cycle).
     6. `antitime_warped_chronon_infused_unobtainium_reinforced_chronon_anticrystal`: Upgraded via Smithing Table with Unobtainium Ingot.
     7. `orange_antitime_warped_chronon_infused_unobtainium_reinforced_chronon_anticrystal`: Dyed with Orange Dye in Crafting Table.
     8. `chronon_wand`: Crafted using the Orange Reinforced Crystal + Unobtainium Rods + Netherite Ingot.
   - **Progress Tracking**: Tracked on item stacks via `chronon_wand:process_count` Data Component. Displays a color-coded durability bar and hover tooltip showing `Current / Target (XX.X%)`.
   - **Custom Serializers**:
     - `chronon_wand:progressive_metallurgic_infusing`
     - `chronon_wand:progressive_nucleosynthesizing`
     - `chronon_wand:progressive_thermo_plant`
   - **Mekanism Chronon Chemical**: Registered via `MekanismCompat` and convertible 1:1 with `chronon_fluid` via Rotary Condensentrator (`chronon_wand:chronon_rotary`).
   - **Config Toggle**: Configurable in `chronon_wand-server.toml` under `enable_progression_recipes` (defaults to `true`). Recipes use condition serializer `chronon_wand:recipes_enabled`. A warning informs players that changing the setting requires a server restart.

---

## 2. Conversation & Development Summary

### Milestone 1: Crash Fixes & Cross-Pack Compatibility
- Resolved crash on heavy modpacks (All The Mods 10 Aeronautics).
- Separated AE2 `WirelessTerminalItem` mixin into legacy vs modern implementations to support differing versions of AE2 / AE2 Import Export Card addon.

### Milestone 2: AE2 Machine Acceleration & BlockTickHelper
- Implemented real AE2 grid ticking support in [`AE2TickHandler`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/ae2/AE2TickHandler.java) and soft delegate [`AE2TickHelper`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/AE2TickHelper.java), safely guarded by `ModList.get().isLoaded("ae2")`.
- Consolidated tick acceleration logic between the Chronon Wand, AcceleratorEntity, and Chronon Accelerator block in [`BlockTickHelper`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/util/BlockTickHelper.java).

### Milestone 3: Chronon Accelerator Implementation
- Created the core machine following the user's `to_import/plan.txt`:
  - [`ChrononAcceleratorBlock`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/block/ChrononAcceleratorBlock.java)
  - [`ChrononAcceleratorBlockEntity`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/block/entity/ChrononAcceleratorBlockEntity.java)
  - [`ChrononAcceleratorMenu`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/menu/ChrononAcceleratorMenu.java)
  - [`ChrononAcceleratorScreen`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/screen/ChrononAcceleratorScreen.java)
  - [`AcceleratorSettingsPayload`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/network/AcceleratorSettingsPayload.java)
- Designed a custom dark UI inspired by Just Dire Things with speed tier selectors, power gauge, radius/offset steppers, redstone mode cycling, and area toggle.
- Added half-step radius support (`0.5` grid alignment).

### Milestone 4: Refinements & User Feedback (from `changes.txt`)
- **6-Directional Placement**: Added `BlockStateProperties.FACING` and placement facing away from the player camera angle.
- **Removed Wireless Network**: Removed `MultiversalChrononHandler` fallback so the machine only consumes power from adjacent Metronomes or direct fluid piping.
- **Initial Offset**: The green selection box automatically initializes with an offset of 1 block in front of the active face.
- **Seam / Bounding Box Size**: Scaled bounding box inflation down from `0.005m` to `0.001m` (`1mm`).
- **Translucent Occlusion Bug**: Fixed selection boxes failing to render when behind other boxes by creating [`ChrononRenderTypes.TRANSLUCENT_BOX`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/renderer/ChrononRenderTypes.java) with depth-writing disabled (`COLOR_WRITE`).
- **Deprecation Clean-up**: Fixed `BlockStateBase.rotate` deprecation warning in `ChrononAcceleratorBlock.mirror` by using `mirror.mirror(Direction)`.

### Milestone 5: Bug Fixes & Optimization Round (v1.7.2)
- **AE2 Machine Acceleration**: Implemented `AE2TickHelper` and `AE2TickHandler` to accelerate AE2 machines (`IGridTickable` through `IGridConnectedBlockEntity`).
- **Loot Table & Pickaxe Tag**: Added loot table and mineable tag for Chronon Accelerator. Added deny tags for `#chronon_wand:acceleration_deny`, `#justdirethings:tick_speed_deny`, and `#tiab:un_acceleratable`.
- **Ender Metronome Disconnect**: Completely removed `MultiversalChrononHandler` fallback from `MetronomePortBlockEntity` so it will never connect wirelessly to the ender metronome network.
- **Buffer Duplication Bug**: Fixed `ChrononAcceleratorBlockEntity` setting the internal fluid buffer to the adjacent Metronome's total energy, which bypassed capacity limits and duplicated fluid.
- **Stalling Fix**: Pre-checked power consumption against `nextAccumulator` so unfulfilled ticks don't accumulate into unattainable deficits.
- **Neighbor Caching**: Cached adjacent metronome lookup in both `ChrononAcceleratorBlockEntity` and `MetronomePortBlockEntity`, invalidated on neighbor block updates or every 10 ticks.

### Milestone 6: Chronon Crystal Progression System (v1.8.0)
- **Progression Plan Execution**: Implemented the complete progression line from `Chronon crystal plans.xlsx`.
- **Option B Automation Logic**: Crystals hold their process count in `chronon_wand:process_count`. Multi-cycle recipes loop items through standard automation conduits until target is reached and transforms into the next stage crystal.
- **Custom Compat Recipe Types**:
  - `ProgressiveMetallurgicInfuserRecipe` subclassing Mekanism's `ItemStackChemicalToItemStackRecipe`.
  - `ProgressiveNucleosynthesizingRecipe` subclassing Mekanism's `NucleosynthesizingRecipe`.
  - `ProgressiveThermoPlantRecipe` subclassing PneumaticCraft's `ThermoPlantRecipe`.
- **Fluid & Bucket Support**: Added `antitime_fluid` (source, flowing, fluid type, block) and `antitime_fluid_bucket`. Registered client extensions with deep cosmic violet tint (`0xFF351052`).
- **Item Models & Textures**: Created 16x16 pixel-accurate textures matching mod aesthetics for all 6 crystals and `antitime_fluid_bucket`.
- **Localizations**: Complete English localization for all items, fluids, chemical, blocks, and progress tooltips.
- **Dual-Instance Deployment**: Compiled and copied `chronon_wand-1.8.0.jar` to both Prism test instances and `builds/`.

### Milestone 7: Compatibility Fixes & Recipe Display Refinements (v2.0.0)
- **NeoForge 21.1.228+ Support**: Lowered minimum NeoForge dependency to `21.1.228` in `gradle.properties` and verified clean compilation.
- **AE2 Import Export Card Compat**: Fixed hard `@Shadow` crash on `ae2importexportcard` by replacing with safe reflective invocation. Gated slot mixins in `AE2IECMixinPlugin` so versions $< 1.7.0$ (like `1.4.3`) are gracefully ignored without crashing modloading.
- **PneumaticCraft Thermo Plant Recipe Fix**:
  - In `antitime_warped_chronon_infused_chronon_anticrystal_thermo.json`, fixed fluid ingredient key (`"fluid"` instead of `"id"`), outputs key (`"item_output"` instead of `"outputItem"`), and specified explicit temperature range (`min: 0, max: 100`).
  - In `ProgressiveThermoPlantRecipe.java`, updated `getOutputItem()` so static queries (JEI/EMI/REI) return the target finished crystal, while runtime queries during machine ticking return the incremented crystal until target count is reached.
- **Metallurgic Infuser Recipe Fix**:
  - In `ProgressiveMetallurgicInfuserRecipe.java`, overrode `getOutputDefinition()` to return `Collections.singletonList(targetOutput)` so Mekanism's JEI and EMI categories display the final output crystal.
  - Overrode `getResultItem(HolderLookup.Provider)` to return `targetOutput.copy()` so EMI, JEI, and vanilla lookups properly index the recipe under `chronon_infused_chronon_crystal`.
  - Overrode `getGroup()` to return `"metallurgic_infuser"`.
- **Deployment**: Removed `ATM10SKY Aeronautics` deployment task from `build.gradle` per user request. Mod is deployed exclusively to `Chronon wand`, `All the Mods 10 Aeronautics`, and `builds/`.

---

## 3. Strict Rules & Operational Constraints

1. **Versioning Rule (STRICT)**:
   - When fixing a round of bugs, increment `z` (`x.y.z` -> `x.y.(z+1)`).
   - When adding a new feature, increment `y` and reset `z` (`x.y.z` -> `x.(y+1).0`).
   - Version is maintained in [`gradle.properties`](file:///e:/Owen/Projects/Mods/Chronon_wand/gradle.properties) (`mod_version`).
2. **Dual-Instance Deployment**:
   - Whenever building the jar with `cmd.exe /c "gradlew.bat build -x test"`, the resulting jar from `build/libs/` MUST be deployed to BOTH:
     - `E:\Games\PrismLauncher\instances\Chronon wand\minecraft\mods\`
     - `E:\Games\PrismLauncher\instances\All the Mods 10 Aeronautics\minecraft\mods\`
   - Delete old `chronon_wand-*.jar` versions from those directories when deploying new versions.
3. **Soft Dependencies**:
   - Never import AE2, Mekanism, or PneumaticCraft classes directly into non-compat classes. Use reflection or isolation in `com.ohh.chronon_wand.compat.*` guarded by `ModList.get().isLoaded(...)`.
4. **Git**:
   - Don't commit changes to git, let the user do that.

### Milestone 7: Recipe Viewer Compat & PNC Thermo Plant Fixes
- **Root Cause Analysis for Recipe Visibility**:
  1. The user was testing in the lightweight instance `Chronon wand` where only **EMI** was installed, but **JEI** was missing.
  2. PneumaticCraft: Repressurized (PNC) does **not** have a native EMI plugin; it only provides a JEI plugin (`me.desht.pneumaticcraft.common.thirdparty.jei.JEIPlugin`).
  3. EMI displays JEI plugins through its bridge (JEMI), which is only active when JEI itself is loaded in the instance.
  4. In addition, [`ProgressiveThermoPlantRecipe`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/pneumaticcraft/ProgressiveThermoPlantRecipe.java) lacked an override for `getResultItem(HolderLookup.Provider registries)`. Because the PNC superclass returns `ItemStack.EMPTY`, JEI/EMI could not associate the recipe with the output item `antitime_warped_chronon_infused_chronon_anticrystal`.
- **Fixes Applied**:
  1. Overrode `getResultItem(HolderLookup.Provider registries)`, `assemble(CraftingInput, HolderLookup.Provider registries)`, and `getGroup()` in [`ProgressiveThermoPlantRecipe.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/pneumaticcraft/ProgressiveThermoPlantRecipe.java).
  2. Removed the corrupt 0-byte file `src/main/resources/data/chronon_wand/recipe/chronon_accelerator.json` which was causing a `JsonParseException` (EOFException) on datapack reloads.
  3. Copied `jei-1.21.1-neoforge-19.57.0.449.jar` into the `Chronon wand` instance mods directory (`E:\Games\PrismLauncher\instances\Chronon wand\minecraft\mods\`).
  4. Built and deployed updated `chronon_wand-2.0.0.jar` to both `Chronon wand` and `All the Mods 10 Aeronautics`.

---

## 4. Key Files & Reference Index

| File | Purpose |
|---|---|
| [`ChrononAcceleratorBlock.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/block/ChrononAcceleratorBlock.java) | Directional block definition, placement logic, neighbor change invalidation, menu provider |
| [`ChrononAcceleratorBlockEntity.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/block/entity/ChrononAcceleratorBlockEntity.java) | Machine logic, acceleration loop, power extraction, NBT & network sync |
| [`MetronomePortBlock.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/block/MetronomePortBlock.java) | Metronome Port block definition & neighbor update handling |
| [`MetronomePortBlockEntity.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/block/entity/MetronomePortBlockEntity.java) | Metronome Port logic, physical-only connection, side config |
| [`ChrononAcceleratorMenu.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/menu/ChrononAcceleratorMenu.java) | Container menu & data slots |
| [`ChrononAcceleratorScreen.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/screen/ChrononAcceleratorScreen.java) | Custom GUI screen |
| [`ProgressiveCrystalItem.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/item/ProgressiveCrystalItem.java) | Multi-cycle crystal item with dynamic durability bar and hover tooltip |
| [`ProgressiveMetallurgicInfuserRecipe.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/mekanism/ProgressiveMetallurgicInfuserRecipe.java) | Custom Mekanism Infuser recipe handling incremental crystal progress |
| [`ProgressiveNucleosynthesizingRecipe.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/mekanism/ProgressiveNucleosynthesizingRecipe.java) | Custom Mekanism Nucleosynthesizer recipe handling incremental crystal progress |
| [`ProgressiveThermoPlantRecipe.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/pneumaticcraft/ProgressiveThermoPlantRecipe.java) | Custom PNC Thermo Plant recipe handling incremental crystal progress |
| [`ChrononWandServerConfig.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/config/ChrononWandServerConfig.java) | Server config with `enable_progression_recipes` toggle |
| [`RecipesEnabledCondition.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/recipe/condition/RecipesEnabledCondition.java) | NeoForge recipe condition checking config toggle |
| [`ModFluids.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/fluid/ModFluids.java) | `chronon_fluid` and `antitime_fluid` registrations |
| [`BlockTickHelper.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/util/BlockTickHelper.java) | Universal block & entity tick dispatcher with deny tags support |
| [`AE2TickHelper.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/AE2TickHelper.java) | Soft AE2 machine acceleration delegate |
| [`AE2TickHandler.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/ae2/AE2TickHandler.java) | Direct AE2 grid machine ticking implementation |
| [`gradle.properties`](file:///e:/Owen/Projects/Mods/Chronon_wand/gradle.properties) | Mod metadata & version tracking |
| [`changes.md`](file:///e:/Owen/Projects/Mods/Chronon_wand/changes.md) | Project changelog following Keep a Changelog standard |

---

## 5. Next Steps & Potential Future Work

1. **Restart Minecraft Instance**:
   - The user must restart their Minecraft client so the newly added JEI mod is loaded into `Chronon wand`.
   - On restart, EMI's JEMI bridge will automatically load PneumaticCraft's `JEIPlugin` and index the Thermopneumatic Processing Plant category and recipes.
2. **In-Game Verification**:
   - Verify that the Thermopneumatic Processing Plant category appears in EMI/JEI and shows the Antitime-Warped Crystal recipe.
   - Verify multi-cycle automation with item pipes/hoppers.

---

## 6. Commitments & Workflow Constraints

- **Semantic Versioning (SemVer 2.0.0)**:
  - Format: `MAJOR.MINOR.PATCH` (e.g., `2.0.1`).
  - Increment `PATCH` for backward-compatible bug fixes.
  - Increment `MINOR` for new backward-compatible functionality / content.
  - Increment `MAJOR` for breaking changes / incompatible architecture shifts.
  - Keep `mod_version` in [`gradle.properties`](file:///e:/Owen/Projects/Mods/Chronon_wand/gradle.properties), [`changes.md`](file:///e:/Owen/Projects/Mods/Chronon_wand/changes.md), and `handoff.md` synchronized whenever versioning changes.
- **Keep a Changelog**:
  - Maintain [`changes.md`](file:///e:/Owen/Projects/Mods/Chronon_wand/changes.md) following [Keep a Changelog](https://keepachangelog.com/) with `[Unreleased]` and versioned sections (`Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, `Security`).
- **Git Commit Rules**:
  - Automatically commit changes to git locally with the version number included in the commit message (e.g. `v2.0.1: <description>`).
  - **DO NOT PUSH TO REMOTE**: Never execute `git push`.
- **Instance Deployment Restrictions**:
  - Strictly **DO NOT deploy** to `ATM10SKY Aeronautics` (`E:\Games\PrismLauncher\instances\ATM10SKY Aeronautics\minecraft\mods\`).
  - Only deploy to `Chronon wand` and `All the Mods 10 Aeronautics`.


