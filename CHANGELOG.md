# Changelog - Jane Moose Machinery

All notable changes to the Jane Moose mod will be documented in this file.

## [1.0.1] - 2026-10-24

### Added
- **Fermentation Barrel Block**:
  - Added a dedicated 3D rounded oak barrel block with bulging bilge, dual riveted iron hoops, and top fermentation airlock.
  - Interactive Custom GUI (`FermentationBarrelScreen`) with oak/brass styling, animated fermentation progress bar, 60-second countdown timer, and golden-amber Ethanol tank gauge ($8,000\text{ mB}$ capacity).
  - Converts **Wheat** into **Ethanol Biofuel** at exactly **1 Bucket ($1,000\text{ mB}$) per 1 real-life minute** ($1,200\text{ ticks}$).
  - Quick-draw support: right-click the barrel in the world with an empty bucket to instantly extract an Ethanol Bucket, or use the GUI input/output slots for automated bucket filling.
  - Full automation: implements `WorldlyContainer` so hoppers and pipes can feed wheat and buckets into the barrel.
  - Craftable in survival with planks, iron ingots, and a bucket.
- **Ethanol Bucket Item**:
  - Added `janemoose:ethanol_bucket` (+1,000 mB fuel, craftRemainder bucket), fully integrated with the `#c:ethanol` tag.
- **Ethanol & Biofuel Integration (Refinery Mod Compatibility)**:
  - Tractor now supports refueling with **Ethanol** (`refinery:ethanol_bucket`, `#c:ethanol`, `#c:fuels/ethanol`) alongside Diesel (`refinery:diesel_bucket`, `janemoose:diesel_canister`, `#c:diesel`).
  - Complete closed-loop farming cycle: harvest crops with Jane Moose, ferment them into Ethanol in the Refinery mod's Industrial Fermenter, and refuel the tractor (+1,000 mB per bucket, returning the empty bucket).
- **Working Dynamic Headlights**: Realistic front torch illumination (Light Level 15) originating directly from the tractor grille lenses ($1.85$ blocks forward, $Y+0.85$), illuminating a 15-block radius ahead of the vehicle.
- **Assignable Keybinding**: Dedicated key mapping for headlights under `Jane Moose Machinery` (Default key: `[L]`).
- **Dashboard Lights Toggle**: Added an interactive `Lights: ON / OFF` button to the tractor dashboard GUI.
- **Waterlogging & Terrain Adaptability**: Light placement supports waterlogged blocks and adjusts elevation over slopes and crops without damaging them.

### Fixed
- **POM Shader Text Scrambling**: Clamped height and normal maps on decal stripes so Parallax Occlusion Mapping (POM) shaders render "JANE MOOSE 4020" crisply without distortion.
- **Decal Overlap & Typos**: Separated "JANE MOOSE" from "4020" with a 30px gap and fixed "DIESEL" stamping inside the model number.
- **Z-Fighting & Texture Flickering**:
  - Removed overlapping nose cowl box that obstructed headlights and grille.
  - Inset wheel rim diagonal crossbars by `0.02F` to prevent outer cap flickering.
  - Decoupled frame rails from rear axle and offset the rear hitch to eliminate green chassis flickering.
- **Handheld Attachment Placement**: Right-clicking ground with an attachment item now places the 3D entity directly instead of hoe-tilling dirt.
- **Dashboard Hopper Crash**: Eliminated synthetic enum switch classes that caused `ZipException` / `ClassNotFoundException` when opening Seed Hopper / Grain Tank screens.

---

## [1.0.0] - 2026-10-23
- Initial CurseForge release build.
- John Deere 4020 inspired Jane Moose tractor with rideable physics, 16-facet wheels, diesel fueling, and dashboard GUI.
- Cultivators, Planters, and Harvesters across 3 tiers (Wood, Iron, Diamond).
- 2K diffuse textures with LabPBR 1.3 normal and specular maps.
