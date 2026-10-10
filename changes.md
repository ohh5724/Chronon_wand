# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

