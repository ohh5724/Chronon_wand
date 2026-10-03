# Agent Handoff: Chronon Wand Mod Development

## 1. Project Overview & Current State of Reasoning

### About the Project
- **Mod Name**: Chronon Wand
- **Mod ID**: `chronon_wand`
- **Environment**: NeoForge 21.1.250 for Minecraft 1.21.1, Java 21
- **Current Version**: `1.7.2` (defined in [`gradle.properties`](file:///e:/Owen/Projects/Mods/Chronon_wand/gradle.properties))
- **Workspace Location**: `e:\Owen\Projects\Mods\Chronon_wand`
- **Prism Launcher Test Instances**:
  1. `E:\Games\PrismLauncher\instances\Chronon wand\minecraft\`
  2. `E:\Games\PrismLauncher\instances\All the Mods 10 Aeronautics\minecraft\`

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
   - Never import AE2 classes directly into non-compat classes. Use reflection or isolation in `com.ohh.chronon_wand.compat.ae2.*` and `compat/AE2TickHelper.java` guarded by `ModList.get().isLoaded("ae2")`.
4. **Git**
   - Don't commit changes to git, let the user do that.

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
| [`ChrononAcceleratorRenderer.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/renderer/ChrononAcceleratorRenderer.java) | Client-side 3D bounding box renderer |
| [`ChrononRenderTypes.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/renderer/ChrononRenderTypes.java) | Custom `RenderType` without depth-writing for translucent boxes |
| [`AcceleratorSettingsPayload.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/network/AcceleratorSettingsPayload.java) | C2S network packet for syncing GUI settings to server |
| [`BlockTickHelper.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/util/BlockTickHelper.java) | Universal block & entity tick dispatcher with deny tags support |
| [`AE2TickHelper.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/AE2TickHelper.java) | Soft AE2 machine acceleration delegate |
| [`AE2TickHandler.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/ae2/AE2TickHandler.java) | Direct AE2 grid machine ticking implementation |
| [`gradle.properties`](file:///e:/Owen/Projects/Mods/Chronon_wand/gradle.properties) | Mod metadata & version tracking |

---

## 5. Next Steps & Potential Future Work

1. **Verify In-Game Behavior**:
   - Verify that AE2 machines (inscribers, molecular assemblers, growth accelerators, chargers, vibration chambers) advance faster when accelerated by either the Wand or the Chronon Accelerator.
   - Verify that Metronome Port and Chronon Accelerator properly draw power exclusively from adjacent Metronomes or fluid buffer pipes, and not wirelessly.
2. **Pending User Requests**:
   - Monitor `to_import/plan.txt` or `to_import/changes.txt` for crafting recipe implementations or future feature adjustments.
