# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.1.3] - 2026-11-01

### Changed
- Updated Antitime fluid temperature to -727 K.
- Updated operating temperature range in the PneumaticCraft Thermopneumatic Processing Plant recipe for `antitime_warped_chronon_infused_chronon_anticrystal` to -750 K to -700 K.

## [2.1.2] - 2026-10-23

### Fixed
- Fixed missing texture on the 3D Chronon Wand model caused by Blockbench `.mtl` export defaulting to `wand_temp_texture.png`; properly mapped texture to `chronon_wand:item/chronon_wand`.

### Changed
- Updated Chronon Wand 3D model geometry and work-in-progress Blockbench project files.

## [2.1.1] - 2026-10-21

### Changed
- Updated the Chronon Wand 3D Wavefront OBJ model geometry and material bindings.
- Updated source model assets in `to_import/models/` and `to_import/textures/`.

## [2.1.0] - 2026-10-11

### Added
- Integrated custom 3D Wavefront OBJ model (`chronon_wand.obj` and `chronon_wand.mtl`) for the Chronon Wand item using NeoForge's built-in `neoforge:obj` model loader with `flip_v: true`.
- Added first-person, third-person, ground, and GUI slot diagonal display transformations in `chronon_wand.json`.
- Updated Chronon Wand texture to 32x32 resolution matching the new 3D model mapping.

## [2.0.2] - 2026-10-11

### Fixed
- Fixed build and dependency resolution failure on secondary machines lacking an `E:` drive by dynamically detecting the active Prism Launcher instance mods directory (`Chronon_wand` under AppData or `Chronon wand` under `E:`).
- Added existence guards (`onlyIf`) to instance deployment tasks (`copyJarToPrism`, `copyJarToAtm10`) to prevent missing directory/drive build failures on secondary machines.
- Added fallback for `copyJarToBuilds` to local `./builds` when the `E:` drive is unavailable.

## [2.0.1] - 2026-10-06

### Added
- Immediate neighbor update invalidation (`neighborChanged`) in `ChrononAcceleratorBlock` to refresh connected Metronome capability caches instantly on block placement or removal.
- Tagged `tempad:metronome` in `#chronon_wand:acceleration_deny` to prevent tick acceleration from targeting Metronome blocks.

### Fixed
- Fixed Chronon Accelerator and Metronome Port draining jump-start chronons (`bootChronons`) from adjacent Metronomes while they are still in their boot-up sequence (`bootTime > 0`).
- Fixed capability extraction on non-extractable Chronon handlers by strictly checking `handler.getCanExtract()`.

## [2.0.0] - 2026-10-05

### Added
- Progressive crystal crafting progression for the Chronon Wand:
  - `unfinished_chronon_crystal`
  - `chronon_infused_chronon_crystal`
  - `antitime_infused_chronon_crystal`
  - `antitime_warped_chronon_infused_chronon_anticrystal`
  - `antimatter_chronon_crystal`
- Integration with Mekanism Metallurgic Infuser (`ProgressiveMetallurgicInfuserRecipe`) and Nucleosynthesizer (`ProgressiveNucleosynthesizingRecipe`).
- Integration with PneumaticCraft: Repressurized Thermopneumatic Processing Plant (`ProgressiveThermoPlantRecipe`).
- Integration with Create Mechanical Mixer for Antitime fluid mixing.
- Custom fluids: `chronon_fluid` and `antitime_fluid`.
- Server config toggle `enable_progression_recipes` backed by NeoForge recipe condition `RecipesEnabledCondition`.
- Dynamic durability bar and progress cycle tooltips on progressive crystal items.

### Fixed
- Fixed Progressive Metallurgic Infuser recipe output definition and result item registration so recipes display properly in EMI and JEI.
- Fixed PneumaticCraft Thermopneumatic Processing Plant progressive recipes not displaying outputs in JEI / EMI (JEMI bridge).
- Removed corrupt 0-byte recipe file `chronon_accelerator.json` that caused JSON parse exceptions during datapack reload.
- Fixed crash when loading alongside older versions of the AE2 Import Export Card mod.

## [1.8.0] - 2026-10-03

### Added
- Tick acceleration support for Applied Energistics 2 (AE2) machines via `AE2TickHelper` and `AE2TickHandler`.

### Fixed
- Fixed rendering artifact where bounding selection boxes were hidden when positioned behind other selection boxes.

## [1.7.0] - 2026-10-02

### Added
- Chronon Accelerator Block: multi-tier, area-based tick acceleration block with directional facing, redstone controls, range configuration, and power buffering.
- Chronon Accelerator container menu and interactive GUI screen.

## [1.6.0] - 2026-09-28

### Added
- Compatibility with AE2 Import/Export Card mod.

### Fixed
- Fixed issue where server configuration was reset on game restart.

## [1.4.0] - 2026-09-18

### Added
- Full tier range of Applied Energistics 2 Chronon Storage Cells (1k to 256m).
- Crop acceleration support with the Chronon Wand.

