# Jane Moose 🚜

> *"Nothing runs like a moose!"*

A classic John Deere-inspired farm tractor mod for **Minecraft 26.3** on **NeoForge 26.3.0.48**.

---

## 🦌 Features

- **Compact Size**: Built with a tight footprint (strictly within **2x2x2** blocks, approx 1.8m × 1.8m × 1.8m), allowing it to easily navigate farms, barn gates, and narrow crop rows.
- **Diesel Powered**:
  - Internal 10,000 mB fuel tank.
  - Fully compatible with diesel fluids/items from other mods via `#c:diesel`, `#c:fuels/diesel`, and `#forge:diesel` item/fluid tags.
  - Comes with a standalone **Diesel Canister** (+1,000 mB) and diesel crafting recipe so you can play immediately even before your dedicated fuel mod is loaded!
  - Active exhaust smoke particles emit while the diesel engine is running.
- **3-Point Rear Hitch System**:
  - Accepts attachments hitched to the rear.
  - Shift-right-click with an attachment to hitch it.
  - Sneak-interact with empty hands or wrench to unhitch.
  - Hitched attachments are rendered directly onto the tractor's rear 3-point hitch.
- **Attachments & Tiers**:
  1. **Cultivators** (Tills soil into farmland):
     - **Wood Cultivator**: 1x1 width (single row).
     - **Iron Cultivator**: 1x2 width (double row).
     - **Diamond Cultivator**: 1x3 width (triple row).
  2. **Harvesters** (Reaps mature crops, drops harvest, and auto-replants at age 0):
     - **Wood Harvester**: 1x1 width.
     - **Iron Harvester**: 1x2 width.
     - **Diamond Harvester**: 1x3 width.
  3. **Planters** (Draws seeds from rider inventory and plants onto farmland):
     - **Wood Planter**: 1x1 width.
     - **Iron Planter**: 1x2 width.
     - **Diamond Planter**: 1x3 width.
     - Compatible with Wheat, Carrots, Potatoes, Beetroots, Melons, Pumpkins, and Torchflowers!
- **Jane Moose Mechanics Wrench**:
  - Right-click the tractor to inspect current fuel levels and attached implements.
  - Sneak-right-click to safely pick up the tractor into item form.

---

## 🛠️ Controls & Usage

| Action | Control |
|---|---|
| **Mount Tractor** | Right-click tractor with empty hand |
| **Dismount** | Sneak (Left Shift) |
| **Drive / Steer** | Standard movement controls (`W`/`A`/`S`/`D` and mouse look) |
| **Refuel** | Right-click tractor with Diesel Canister or diesel bucket |
| **Hitch Attachment** | Right-click rear hitch or tractor while holding an attachment |
| **Detach Attachment** | Sneak-right-click tractor with empty hand |
| **Check Fuel / Status** | Right-click tractor with Jane Moose Wrench |
| **Pack Up Tractor** | Sneak-right-click tractor with Jane Moose Wrench |

---

## ⚙️ Building from Source

Requirements:
- Java 21+ Development Kit (JDK 21 or 25)
- Internet connection for initial dependency resolution

Run:
```bash
./gradlew build
```
The compiled mod JAR will be located in `build/libs/janemoose-1.0.0.jar`.
