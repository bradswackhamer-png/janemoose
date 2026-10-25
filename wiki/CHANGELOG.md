# 📜 Version Changelog & Release History

Welcome to the **Jane Moose 4020** version archive. This page documents all major releases, feature additions, shader improvements, and bug fixes.

---

## 🚜 [v1.0.3] — Realistic Jerry Can & High-Detail Implement Icons Overhaul
*Released on October 25, 2026*

### 🎨 Art & Texture Overhaul
* **Realistic Jerry Can (`diesel_canister.png`)**:
  * Upgraded from flat placeholder square to an authentic 32x32 pressed-steel Jerry Can.
  * Heavy-equipment Industrial Machinery Yellow finish with rolled perimeter weld seams.
  * Stamped 3D "X" stiffening ribs with embossed highlights and deep shadow creases.
  * Authentic triple-bar welded carry handles (black powder-coated steel) with open spacing.
  * Angled pour spout / filler neck with brass cam-locking cap, retaining cotter pin, and right-hand breather screw.
  * High-visibility black hazard banner with amber "DIESEL" stencil lettering.
* **Agricultural Implement Icons (32x32 Overhaul)**:
  * **Cultivators (`wooden_cultivator`, `iron_cultivator`, `diamond_cultivator`)**:
    * Features 3-point hitch A-frame mounting bracket, structural toolbar frame, curved spring-steel C-shanks with coil spring mounts, and flared duckfoot sweep plow points.
    * Wood: Treated oak timber beam with forged dark iron tines.
    * Iron: Jane Moose green steel box beam with gold badge and polished steel chisel sweeps.
    * Diamond: Armored cyan frame with wide 3-row wings and faceted gemstone sweeps.
  * **Planters (`wooden_planter`, `iron_planter`, `diamond_planter`)**:
    * Authentic row-crop planter silhouette: hitch toolbar, tapered seed hoppers with latching lids, flexible ribbed seed drop tubes, double-disk furrow opener coulters, and trailing V-closing press wheels.
    * Wood: Single timber hopper box with iron band clamp.
    * Iron: Twin Jane Moose green hoppers with combine yellow lids.
    * Diamond: Triple hoppers with cyan inspection sight-glass windows and diamond coulters.
  * **Harvesters (`wooden_harvester`, `iron_harvester`, `diamond_harvester`)**:
    * Complete combine grain header silhouette: rotating 4-bat pickup reel with end star spiders, gathering cross-auger drum, outer crop divider snouts, and lower sickle cutterbar with rock guards and reciprocating triangular knife sections.
    * Wood: Oak timber reel bats and wooden collection pan with iron sickle knife.
    * Iron: Combine yellow 4-bat reel slats with green star spiders and sharp steel knives.
    * Diamond: Cyan diamond-reinforced reel bats and sparkling diamond-edged cutter blades.
* **Tool & Vehicle Icons**:
  * **Mechanic's Wrench (`wrench.png`)**: Heavy combination wrench with 15° angled open-end jaw, 12-point box ring, and green comfort grip.
  * **Jane Moose Tractor (`tractor.png`)**: John Deere 4020 silhouette with 16-facet chevron rear ag tires, yellow rims, green hood, dual headlights, operator seat, and chrome smokestack with rain flapper.

---

## 🚜 [v1.0.2] — Fermentation Barrel & Ethanol Biofuel Update
*Released on October 24, 2026*

### ✨ New Features & Gameplay
* **Fermentation Barrel Block**:
  * Added a dedicated 3D rounded oak barrel block with bulging middle bilge, dual riveted iron hoops, and a top fermentation airlock.
  * Interactive Custom GUI (`FermentationBarrelScreen`) with oak/brass styling, animated fermentation progress bar, 60-second countdown timer, and golden-amber Ethanol tank gauge ($8,000\text{ mB}$ capacity).
  * Converts **Wheat** into **Ethanol Biofuel** at exactly **1 Bucket ($1,000\text{ mB}$) per 1 real-life minute** ($1,200\text{ ticks}$).
  * Quick-draw support: right-click the barrel in the world with an empty bucket to instantly extract an Ethanol Bucket, or use the GUI input/output slots for automated bucket filling.
  * Full automation: implements `WorldlyContainer` so hoppers and pipes can feed wheat and buckets into the barrel.
  * Craftable in survival with planks, iron ingots, and a bucket.
* **Ethanol Bucket Item**:
  * Added `janemoose:ethanol_bucket` (+1,000 mB fuel, craftRemainder bucket), fully integrated with the `#c:ethanol` and `#c:fuels/ethanol` tags.
* **Dual-Fuel Engine (Refinery Mod Integration)**:
  * Tractor now accepts **Ethanol Biofuel** (`refinery:ethanol_bucket`, `janemoose:ethanol_bucket`, `#c:ethanol`, `#c:fuels/ethanol`) alongside Diesel (`refinery:diesel_bucket`, `janemoose:diesel_canister`, `#c:diesel`).
  * Complete closed-loop farming cycle: harvest crops with Jane Moose implements, ferment them into Ethanol, and power your tractor.

---

## 🚜 [v1.0.1] — Dynamic Headlights & Shader Polish Update
*Released on October 24, 2026*

### ✨ New Features & Gameplay
* **Dynamic Headlight Illumination**:
  * Front headlights now function as real high-beam illumination, casting a Level 15 light sphere across a 15-block radius in front of the tractor.
  * Emission origin is precisely aligned with the 3D model's dual grille headlights ($1.85$ blocks forward along heading vector, height $Y+0.85$).
  * Supports waterlogged environments (`LightBlock.WATERLOGGED`) and elevation changes so light never damages crops or gets stranded in solid terrain.
  * Automatic lifecycle cleanup clears temporary light blocks upon vehicle movement, turning lights off, unhitching, chunk unloading, or wrench retrieval.
* **Assignable Controls**:
  * Added `Toggle Headlights (On / Off)` keybind under the `Jane Moose Machinery` category in Minecraft's Controls menu. Default assigned key: `[L]`.
* **Dashboard Light Switch**:
  * Added a dedicated `Lights: ON / OFF` button to the tractor dashboard GUI (`TractorScreen`), operable both while driving and while standing adjacent to the vehicle.

### 🎨 Shader & Visual Enhancements
* **LabPBR 1.3 POM Shader Fix**:
  * Fixed text scrambling where "JANE MOOSE 4020" degraded into a distorted mess under Parallax Occlusion Mapping (POM) shaders (Iris, Complementary, SEUS, Oculus).
  * Clamped decal height map to `255` and normal vectors to flat tangent `(128, 128, 255)` in `tractor_n.png`.
* **Decal Alignment**:
  * Separated "JANE MOOSE" from "4020" with a 30+ pixel gap to prevent text overlap.
  * Corrected right hood side decal typo where "DIESEL" was stamped inside the model number.
* **Flicker-Free Geometry (Z-Fighting)**:
  * **Front Nose & Grille**: Removed conflicting nose cowl box to eliminate mesh fighting and unblock headlight lenses.
  * **Wheel Rims**: Inset diagonal rim crossbars by `0.02F` to eliminate coplanar outer end caps.
  * **Rear Chassis & Axle**: Separated frame rails from rear axle housing, extended platform floor deck to $Z=9.8F$, and offset hitch drawbar to $Z=10.05F$, removing 100% of rear green flickering.

### 🐛 Bug Fixes & Usability
* **Attachment Handheld Placement**:
  * Removed unintended hoe tilling behavior from handheld interaction on `CultivatorAttachmentItem`.
  * Right-clicking any block with an attachment now directly spawns the 3D implement entity.
* **Container Crash Fix**:
  * Resolved game crash when opening Seed Hopper / Grain Tank from the tractor dashboard.
  * Eliminated JVM synthetic inner classes (`AttachmentScreen$1.class`) that caused `ZipException invalid LOC header` and `NoClassDefFoundError`.
  * Synchronized slot count across network payloads.

---

## 🚜 [v1.0.0] — Initial Release Build
*Released on October 23, 2026*

* **Tractor Vehicle**:
  * Authentic John Deere 4020 inspired Jane Moose tractor with rideable physics and smooth steering.
  * 16-faceted round wheels with movement-locked wheel rotation.
  * 10,000 mB diesel fuel system with exhaust smoke particle emission.
  * Interactive dashboard GUI for refueling and monitoring implements.
* **3-Point Hitch Implements**:
  * Cultivators, Planters, and Harvesters across 3 tiers (Wood, Iron, Diamond).
  * Working widths: 1-block (Tier I), 2-block (Tier II), 3-block (Tier III).
  * Internal storage: 9, 18, and 27 slots for planter seed hoppers and harvester grain tanks.
  * Automatic speed throttling to 50% when implements are lowered and active.
* **2K Textures & PBR Maps**:
  * 2048x2048 photorealistic diffuse textures.
  * LabPBR 1.3 normal, height, roughness, and metallic maps.
