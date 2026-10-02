# Agent Handoff: Chronon Wand Mod Development

## 1. Project Overview & Current State of Reasoning

### About the Project
- **Mod Name**: Chronon Wand
- **Mod ID**: `chronon_wand`
- **Environment**: NeoForge 21.1.250 for Minecraft 1.21.1, Java 21
- **Current Version**: `1.7.1` (defined in [`gradle.properties`](file:///e:/Owen/Projects/Mods/Chronon_wand/gradle.properties))
- **Workspace Location**: `e:\Owen\Projects\Mods\Chronon_wand`
- **Prism Launcher Test Instances**:
  1. `E:\Games\PrismLauncher\instances\Chronon wand\minecraft\`
  2. `E:\Games\PrismLauncher\instances\All the Mods 10 Aeronautics\minecraft\`

### Current Architecture & Mechanics
1. **Chronon Power System**:
   - Integrated with Tempad's TVA Chronon API (`earth.terrarium.tempad.api.tva_device.ChrononHandler`).
   - The mod adds tools (Chronon Wand, storage cells) and machines (Metronome Port, Chronon Accelerator).
2. **Chronon Accelerator Block**:
   - A multi-tier, area-based tick acceleration block inspired by *Just Dire Things* (JDT) machines.
   - **Orientation**: Full 6-directional block (`FACING` property: `UP`, `DOWN`, `NORTH`, `SOUTH`, `EAST`, `WEST`).
   - **Power**: Does **not** connect wirelessly to player metronomes. Requires Chronon fluid piped directly into its internal buffer (10,000 capacity) or a Metronome block placed adjacent to it.
   - **Area Configuration**:
     - Configurable X, Y, Z Radius (supports half-steps: `0.0`, `0.5`, `1.0`, ... up to `5.0`).
     - Configurable X, Y, Z Offset (from `-9` to `+9`). Default offset when placed is `+1` in the direction the block faces.
   - **In-World Rendering**:
     - Custom Block Entity Renderer ([`ChrononAcceleratorRenderer`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/renderer/ChrononAcceleratorRenderer.java)).
     - Uses green wireframe and translucent green solid fill box.
     - Extends out by `0.001m` (`inflate(0.001)`) to avoid z-fighting without visible seams.
     - Uses [`ChrononRenderTypes.TRANSLUCENT_BOX`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/renderer/ChrononRenderTypes.java) with `COLOR_WRITE` (depth writing disabled) so adjacent/overlapping selection boxes never occlude boxes behind them.
3. **Tick Acceleration Support**:
   - Standard block ticking & random block ticks.
   - Standard block entities with `BlockEntityTicker`.
   - **AE2 Machines**: Supported dynamically via optional runtime integration ([`BlockTickHelper`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/util/BlockTickHelper.java) and [`AE2TickHelper`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/AE2TickHelper.java)) without crashing if AE2 is absent.

---

## 2. Conversation & Development Summary

### Milestone 1: Crash Fixes & Cross-Pack Compatibility
- Resolved crash on heavy modpacks (All The Mods 10 Aeronautics).
- Separated AE2 `WirelessTerminalItem` mixin into legacy vs modern implementations to support differing versions of AE2 / AE2 Import Export Card addon.

### Milestone 2: AE2 Machine Acceleration
- Standard NeoForge ticking logic does not advance AE2 internal machines (Inscribers, Assemblers, Chargers, Growth Accelerators, Vibration Chambers) because AE2 uses its own grid/ticking nodes.
- Implemented reflection/capability-based ticking in [`AE2TickHelper`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/AE2TickHelper.java), safely guarded by `ModList.get().isLoaded("ae2")`.

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

---

## 4. Key Files & Reference Index

| File | Purpose |
|---|---|
| [`ChrononAcceleratorBlock.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/block/ChrononAcceleratorBlock.java) | Directional block definition, placement logic, menu provider |
| [`ChrononAcceleratorBlockEntity.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/block/entity/ChrononAcceleratorBlockEntity.java) | Machine logic, acceleration loop, power extraction, NBT & network sync |
| [`ChrononAcceleratorMenu.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/menu/ChrononAcceleratorMenu.java) | Container menu & data slots |
| [`ChrononAcceleratorScreen.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/screen/ChrononAcceleratorScreen.java) | Custom GUI screen |
| [`ChrononAcceleratorRenderer.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/renderer/ChrononAcceleratorRenderer.java) | Client-side 3D bounding box renderer |
| [`ChrononRenderTypes.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/client/renderer/ChrononRenderTypes.java) | Custom `RenderType` without depth-writing for translucent boxes |
| [`AcceleratorSettingsPayload.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/network/AcceleratorSettingsPayload.java) | C2S network packet for syncing GUI settings to server |
| [`BlockTickHelper.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/util/BlockTickHelper.java) | Universal block & entity tick dispatcher |
| [`AE2TickHelper.java`](file:///e:/Owen/Projects/Mods/Chronon_wand/src/main/java/com/ohh/chronon_wand/compat/AE2TickHelper.java) | AE2 machine acceleration integration |
| [`gradle.properties`](file:///e:/Owen/Projects/Mods/Chronon_wand/gradle.properties) | Mod metadata & version tracking |

---

## 5. Next Steps & Potential Future Work

1. **Verify In-Game Behavior**:
   - Verify that all 3 boxes from the user's latest screenshot render with full transparency and no disappearing faces/lines in `1.7.1`.
   - Verify power extraction works smoothly when placing a Metronome block directly adjacent (on top, bottom, or any of the 4 sides).
2. **Pending User Requests**:
   - Monitor `to_import/changes.txt` or `to_import/plan.txt` for any new feature requests or behavioral tweaks.
   - If the user requests advanced filtering (e.g. blacklisting/whitelisting specific blocks like JDT filters) or particle/sound effects while accelerating, plan these as a minor version bump (`1.8.0`).
