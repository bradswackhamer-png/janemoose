# 🚜 Jane Moose — Diesel Tractor, Bucket Truck & Road Paver Mod

**"Nothing runs like a moose!"**

Jane Moose brings authentic, heavy-duty agricultural, utility, and road construction machinery to Minecraft with the vintage **Jane Moose 4020 Diesel Tractor**, modular 3-point hitch implement attachments, the **Jane Moose GKZ-14 Aerial Bucket / Platform Truck**, and heavy **Road Pavers** (Standard 3x1 and 5x1 Highway Diesel Road Paver). Featuring real-life vehicle mechanics, road paving systems, 3-speed transmissions, articulating aerial work platforms, realistic diesel & ethanol biofuel fueling, interactive custom GUIs, high-definition 2K textures, and full LabPBR Parallax Occlusion Mapping (POM) shader support!

---

## 🌟 Key Features

### 🚜 Jane Moose 4020 Diesel Tractor
* **Iconic Vintage Styling**: Modeled after classic American agricultural iron — featuring 16-faceted round wheels, front suitcase counterweights, graphite exhaust smokestack with rain flapper cap, cyclone pre-cleaner dome, contoured vinyl operator seat, ROPS canopy sunshade, and authentic yellow-striped green livery.
* **Realistic Vehicle Physics**: Smooth acceleration, realistic turning radii, hill climbing, and full driver seating.
* **Dual-Fuel Engine System (Diesel & Ethanol)**: Runs on refined diesel fuel canisters as well as **Ethanol Biofuel** (`refinery:ethanol_bucket`, `janemoose:ethanol_bucket`, `#c:ethanol`, `#c:fuels/ethanol`) from the **Refinery** mod! Harvest your crops, ferment them into ethanol, and power your fleet in a complete farm-to-fuel loop.
* **Working 3-Point Hitch**: Back up to any Jane Moose implement to hitch it directly to your tractor's drawbar.

---

### 🚛 Jane Moose GKZ-14 Aerial Bucket / Platform Truck
* **Authentic Cab-Over-Engine Aerial Work Truck**: Features a white COE cab with tinted windshield, side mirrors, front bumper with fog lights, flatbed deck with red-and-white reflective stripes, underbody electrical cabinet, 4 automatic hydraulic outrigger stabilizer legs with amber beacons, rotating turret pedestal, 2-stage articulated boom + inner telescoping fly-boom, and a self-leveling enclosed aerial bucket.
* **3-Speed Transmission (`1 m/s`, `2 m/s`, `3 m/s`)**: Shift smoothly between **Gear 1 (1 m/s)**, **Gear 2 (2 m/s)**, and **Gear 3 (3 m/s)** using `[G]` or the Dashboard GUI.
* **Sit in the Bucket / Platform**:
  * Right-click the bucket/platform directly or press `[B]` while riding to sit inside the aerial bucket/platform to build, mine, use items, operate the boom (`[R]` / `[V]`), and drive.
  * Dismounting (`Left Shift`) while in the bucket places you on the solid walkable aerial platform floor (`JaneMooseBucketPlatformEntity`) in mid-air!
* **Dedicated Raise & Lower Boom Hotkeys**: Separate assignable hotkeys for **Raise Boom** (`[R]`) and **Lower Boom** (`[V]`).

---

### 🛣️ Heavy Road Pavers (Solo Construction Machinery)
* **Jane Moose Road Paver (`janemoose:road_paver`)**:
  * Industrial yellow heavy crawler paver that works solo without rails, minecarts, or train couplers.
  * **3x1 Roadbed Paving**: Lays down a 3-block wide road under its rear leveling screed (1.0 m/s in Work Mode, 2.0 m/s in Transport Mode).
  * **3-Slot Pattern Filter GUI**: 3 filter slots (Left, Center, Right) to specify paving blocks, 27 bulk material slots, and furnace fuel combustion engine.
  * **Autonomous Cruise & Steering**: WASD driving or automated cruise with Cardinal Lock (N, S, W, E, or Free).
  * **Work Light & Ghost Alignment Grid**: Front illumination and 3x3 holographic preview grid.
* **Jane Moose 5x1 Diesel Road Paver (`janemoose:diesel_road_paver`)**:
  * Second version finished in signature Jane Moose John Deere green livery.
  * **Diesel & Biofuel Powered**: Powered by Jane Moose's 10,000 mB diesel system (refuel via Diesel Canisters, Ethanol Buckets, or GUI slot).
  * **5x1 Wide Highway Paving Swath**: Extended heavy screed plate paving 5 blocks wide in a single pass.
  * **5-Slot Filter GUI**: 5 dedicated filter block slots (Far-Left, Mid-Left, Center, Mid-Right, Far-Right) for multi-lane road design and lane-marking blocks, 27 material storage slots, and live fuel gauge.
  * **5x3 Ghost Hologram Alignment Grid**: 15 holographic outline projection boxes indicating exact placement ahead of the paver.

---

### 🌾 Authentic Agricultural Attachments
All attachments come in **Wooden**, **Iron**, and **Diamond** tiers, with expanding working widths (1-block, 2-block, and 3-block swaths):

1. **Spring-Tooth Cultivator**:
   * Tills soil and dirt into fertile farmland with anti-trample protection.
   * Toggle between **Transport (Raised)** and **Working (Lowered)** positions.
2. **Multi-Row Crop Planter**:
   * Authentic row units with parallel linkage, tapered poly seed hoppers, double-disk furrow openers, depth gauge wheels, seed drop tubes, and V-closing press wheels.
   * Built-in seed inventory — load wheat, carrot, potato, or beetroot seeds to sow complete fields automatically.
3. **Combine Grain Harvester**:
   * Authentic combine grain header featuring double-knife sickle cutterbar, forged rock guards, rotating 4-bat pickup reel, and spiral flighting gathering auger.
   * Slices mature crops, replants them, and gathers grain into its internal hopper tank with viewing window.

---

### 🎨 2K Ultra-HD Textures & LabPBR / POM Support
* **Photorealistic Textures**: Stamped sheet metal, cast-iron frame rails, ribbed tires, and chrome gauge bezels.
* **True 3D Depth (Parallax Occlusion Mapping)**: Includes tangent normal maps (`_n.png`) and PBR specular maps (`_s.png`) formatted to the **LabPBR** standard. Compatible with shader packs like *Complementary*, *Iris*, *Oculus*, and *SEUS* — tire lugs, bolts, hood louvers, and grille honeycomb pop out with real 3D depth!

---

## 🎮 Controls & Keybindings
All vehicle and attachment actions are fully rebindable in the standard Minecraft **Options > Controls > Key Binds** menu under the **Jane Moose Machinery** category:

| Action | Default Key | Context | Description |
| :--- | :---: | :---: | :--- |
| **Drive / Steer** | `W / A / S / D` | Riding | Throttle forward/reverse, steer left/right. |
| **Dismount** | `Left Shift` | Riding | Safely dismount the vehicle (onto platform if in bucket). |
| **Toggle Headlights** | `[L]` | Riding / GUI | Turn high-beam torch headlights **ON / OFF**. |
| **Lift / Lower Implement** | `[H]` or `Space` | Riding Tractor | Toggle tractor implement between **Transport** & **Working** mode. |
| **Toggle Implement Power** | `[X]` | Riding Tractor | Engage / disengage tractor PTO implement mechanism. |
| **Raise Bucket Truck Boom** | `[R]` | Riding Truck | Raise the Bucket Truck's articulated aerial boom. |
| **Lower Bucket Truck Boom** | `[V]` | Riding Truck | Lower the Bucket Truck's articulated aerial boom. |
| **Cycle Bucket Truck Speed** | `[G]` | Riding / GUI | Shift Bucket Truck speed: **1 m/s**, **2 m/s**, **3 m/s**. |
| **Switch Cab / Bucket Seat** | `[B]` | Riding / GUI | Switch between the Driver Cab seat and Aerial Bucket seat. |
| **Open Vehicle Dashboard** | `[C]` | Riding / Stand | Access fuel gauge, stats, rename box, boom controls & toggles. |
| **Open Implement GUI** | Dashboard Button | In GUI | Open Planter seed hopper or Harvester grain tank. |
| **Place Implement** | `Right-Click` | Handheld | Deploy 3D implement entity into world. |
| **Hitch Implement** | Reverse near | Driving | Tractor auto-hitches when backing up to an implement. |
| **Unhitch Implement** | `Shift + Right-Click` | Outside | Detach hitched implement using an empty hand or wrench. |
| **Pack Up Vehicle** | `Shift + Right-Click` | Outside (Wrench) | Retrieve tractor, bucket truck, and attachments into item form. |

---

## 📢 Recent Updates (v1.2.1)
- **Ethanol Biofuel Support for 5x1 Road Paver**:
  - The 5x1 Green Diesel Road Paver now fully accepts **Ethanol Buckets** (`janemoose:ethanol_bucket`, `refinery:ethanol_bucket`, `#c:ethanol`, `#c:fuels/ethanol`) in both in-world right-click fueling and the Dashboard fuel slot.
  - Added an alternative crafting recipe allowing the 5x1 Paver to be crafted using an Ethanol Bucket in place of a Diesel Canister.
  - Idle GUI Refueling: Placing fuel containers in Dashboard Slot 5 immediately and automatically drains them into the fuel tank even while standing still.
  - Polished GUI tooltips and fuel gauge headers to clearly indicate dual Diesel & Ethanol biofuel compatibility.

## 📢 Previous Updates (v1.2.0)
- **Heavy Road Paver (`janemoose:road_paver`)**:
  - Adapted from Track and Trains as a solo road-building vehicle with 3x1 paving, 3 pattern filter slots, 27 material storage slots, coal combustion, Cardinal Lock, work lights, and 3x3 ghost hologram.
- **5x1 Diesel Road Paver (`janemoose:diesel_road_paver`)**:
  - Second road paver version featuring John Deere green livery, 10,000 mB diesel fuel system, extended 5x1 paving screed, 5 filter slots GUI, and 5x3 alignment hologram.
- **Controls & Hotkeys**: Integrated with `[C]` Dashboard GUI, `[L]` Work Light, and `[X]` Work Mode toggles.

## 📢 Previous Updates (v1.1.1)
- **Ethanol Bucket 3D Pixel Art Overhaul (`ethanol_bucket.png`)**:
  - Upgraded the 16x16 placeholder icon to a handcrafted 32x32 3D 8-bit pixel art heavy-duty galvanized utility bucket with an arched steel wire bail, wooden grip, dual corrugation ribs, side mounting ears, and stamped central biofuel droplet emblem.
  - Matches the realistic 3D isometric pixel art aesthetic established by the Jerry Can (`diesel_canister.png`), Bucket Truck (`bucket_truck.png`), and implement icons.
- **Clear Ethanol Fluid Correction**:
  - Corrected the coloration of Ethanol Biofuel from inaccurate orange/amber to its realistic clear spirit appearance.
  - Rendered with crystal-clear pale ice-aqua liquid tones and bright specular glints both in the bucket item texture and inside the Fermentation Barrel screen (`FermentationBarrelScreen`) tank gauge.

## 📢 Previous Updates (v1.1.0)
- **Jane Moose GKZ-14 Aerial Bucket / Platform Truck (`janemoose:bucket_truck`)**:
  - Added a rideable cab-over-engine aerial work platform / bucket truck with 4 automatic hydraulic outriggers, rotating turret, 2-stage articulated boom + telescoping fly-boom, and self-leveling enclosed aerial bucket.
  - **3-Speed Transmission**: Selectable **1 m/s**, **2 m/s**, and **3 m/s** speeds (`[G]` keybind or GUI button).
  - **Dedicated Boom Hotkeys & Dual Seats**: Separate hotkeys for **Raise Boom** (`[R]`) and **Lower Boom** (`[V]`), plus right-clicking the bucket or pressing `[B]` while riding to sit in the Aerial Bucket (`JaneMooseBucketPlatformEntity`) and perform actions at height.

## 📢 Previous Updates (v1.0.3)
- **Realistic Jerry Can & Implement Icons Overhaul**:
  - **Realistic Jerry Can (`diesel_canister.png`)**: Replaced flat placeholder texture with a 32x32 pressed-steel Industrial Yellow Jerry Can with stamped 3D "X" ribs, triple carry handles, brass cam-lock spout, and DIESEL hazard stencil.
  - **32x32 Implements Overhaul**: Cultivators (A-frame hitch, C-shanks, duckfoot sweeps), Planters (seed hoppers, drop tubes, furrow opener coulters, packer wheels), and Harvesters (4-bat rotating reels, auger drum, divider snouts, sickle cutterbars) across Wood, Iron, and Diamond tiers.
  - **Wrench & Tractor Icons**: Upgraded 32x32 chrome-vanadium mechanic's wrench and John Deere 4020 tractor profile icons.

## 📢 Previous Updates (v1.0.2)
- **Fermentation Barrel Block**: 3D rounded oak barrel block that slowly ferments 1 Wheat into 1 Bucket (1,000 mB) of Ethanol per 1 real-life minute.
- **Ethanol Biofuel & Buckets**: Added `Ethanol Bucket` (+1,000 mB) and full integration with the **Refinery** mod (`#c:ethanol` tags).
- **Dual-Fuel Engine**: Power your Jane Moose tractor with both Diesel and Ethanol biofuel.
- **Dynamic Headlights (v1.0.1)**: Working high-beam front headlights (Level 15 light) that light up fields at night (hotkey: [L]).
- **POM Shader Fix (v1.0.1)**: Fixed text scrambling on "JANE MOOSE 4020" when using Parallax Occlusion Mapping shaders.
- **Flicker-Free Geometry (v1.0.1)**: Eliminated Z-fighting on grille, headlights, wheel rims, and rear chassis.

---

## 📦 Requirements & Installation
* **Minecraft Version**: `26.3`
* **Mod Loader**: [NeoForge](https://neoforged.net/) (`26.3.0.48-beta` or newer)
* **Java**: Java 21+

---

## 📜 License
Published under the **MIT License**. Free to include in modpacks!
