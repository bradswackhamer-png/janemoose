# Changelog - Jane Moose Machinery

All notable changes to the Jane Moose mod will be documented in this file.

## [1.0.3] - 2026-10-25

### Added & Overhauled
- **Realistic Jerry Can (`diesel_canister.png`)**:
  - Completely replaced the flat placeholder texture with a handcrafted 32x32 realistic pressed-steel Jerry Can.
  - Authentic Industrial Machinery Yellow finish with rolled perimeter weld seams and rounded corners.
  - Stamped 3D "X" stiffening ribs with embossed lighting (lit upper bevels and shadow grooves) to simulate heavy-duty pressed sheet metal.
  - Heavy-duty triple-bar carry handles (black powder-coated steel) with open spacing.
  - Angled pour spout on the upper shoulder with a brass cam-lock cap, retaining pin, and air breather vent screw.
  - High-contrast black stencil banner across the upper chest with amber "DIESEL" lettering.
- **High-Detail Implement Icons (32x32 Overhaul)**:
  - **Cultivators (`wooden_cultivator.png`, `iron_cultivator.png`, `diamond_cultivator.png`)**:
    - Modeled after real heavy chisel cultivators: top A-frame hitch pin, structural cross-toolbar, curved spring-steel C-shanks with top tension coil mounts, and flared duckfoot sweep plow points.
    - Tier differentiation: Wood (oak beam with forged iron sweeps), Iron (Jane Moose green beam with gold badge and polished steel sweeps), Diamond (cyan armored frame with radiant gemstone sweeps).
  - **Planters (`wooden_planter.png`, `iron_planter.png`, `diamond_planter.png`)**:
    - Modeled after agricultural row-crop planters: hitch toolbar, tapered seed hoppers with latching lids, flexible seed drop tubes, double-disk furrow opener coulters, and trailing V-closing press wheels.
    - Tier differentiation: Wood (single timber hopper box), Iron (twin green hoppers with yellow lids), Diamond (triple high-capacity hoppers with cyan sight-glass level windows).
  - **Harvesters (`wooden_harvester.png`, `iron_harvester.png`, `diamond_harvester.png`)**:
    - Modeled after combine grain headers: 4-bat rotating pickup reel with end star spiders, horizontal gathering cross-auger drum, outer crop divider snouts, and bottom sickle cutterbar with rock guards and triangular knife sections.
    - Tier differentiation: Wood (oak reel slats with iron sickle), Iron (combine yellow reel bats with green spiders and sharp steel sickle knives), Diamond (cyan diamond-reinforced reel bats and diamond-edged cutter blades).
- **Tools & Vehicle Icons Upgrades**:
  - **Mechanic's Wrench (`wrench.png`)**: 32x32 heavy forged chrome-vanadium combination wrench featuring a 15° angled open-end jaw, 12-point box ring, and green recessed comfort grip.
  - **Jane Moose Tractor (`tractor.png`)**: 32x32 authentic John Deere 4020 silhouette featuring 16-facet chevron rear tires, yellow rims, front steer wheel, green hood with yellow stripe, dual headlights, operator station, and chrome exhaust smokestack with rain flapper.

---

## [1.0.2] - 2026-10-24

### Added
- **Fermentation Barrel Block**:
  - Custom 3D rounded oak barrel block with bulging bilge, dual riveted iron hoops, and top fermentation airlock.
  - Interactive Custom GUI (`FermentationBarrelScreen`) with oak/brass styling, animated fermentation progress bar, 60-second countdown timer, and golden-amber Ethanol tank gauge ($8,000\text{ mB}$ capacity).
  - Converts **Wheat** into **Ethanol Biofuel** at exactly **1 Bucket ($1,000\text{ mB}$) per 1 real-life minute** ($1,200\text{ ticks}$).
  - Quick-draw support: right-click the barrel in the world with an empty bucket to instantly extract an Ethanol Bucket, or use the GUI input/output slots for automated bucket filling.
  - Full automation: implements `WorldlyContainer` so hoppers and pipes can feed wheat and buckets into the barrel.
  - Craftable in survival with planks, iron ingots, and a bucket.
- **Ethanol Bucket Item**:
  - Added `janemoose:ethanol_bucket` (+1,000 mB fuel, craftRemainder bucket), fully integrated with the `#c:ethanol` and `#c:fuels/ethanol` tags.
- **Ethanol Biofuel & Dual-Fuel Engine (Refinery Mod Integration)**:
  - Tractor now supports refueling with **Ethanol** (`refinery:ethanol_bucket`, `janemoose:ethanol_bucket`, `#c:ethanol`, `#c:fuels/ethanol`) alongside Diesel.
  - Complete closed-loop farming cycle: harvest crops with Jane Moose implements, ferment them into Ethanol in the Fermentation Barrel or Refinery mod's Industrial Fermenter, and power your tractor.

---

## [1.0.1] - 2026-10-24

### Added
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
