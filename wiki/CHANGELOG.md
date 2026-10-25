# 📜 Version Changelog & Release History

Welcome to the **Jane Moose 4020** version archive. This page documents all major releases, feature additions, shader improvements, and bug fixes.

---

## 🚜 [v1.0.1] — Dynamic Headlights & Shader Polish Update
*Released on October 24, 2026*

### ✨ New Features & Gameplay
* **Ethanol & Biofuel Integration (Refinery Mod Compatibility)**:
  * Tractor now accepts **Ethanol Biofuel** (`refinery:ethanol_bucket`, `#c:ethanol`, `#c:fuels/ethanol`) alongside Diesel (`refinery:diesel_bucket`, `janemoose:diesel_canister`, `#c:diesel`).
  * Enables a complete farm-to-fuel gameplay loop: harvest crops with Jane Moose implements, ferment them into Ethanol in the Refinery mod's Industrial Fermenter, and refuel the tractor (+1,000 mB per bucket, returning the empty bucket).
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
