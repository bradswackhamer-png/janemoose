# ⛽ Fuel & Diesel System

The **Jane Moose Model 4020** features a realistic dual-fuel internal combustion engine capable of running on both refined **Diesel** and sustainable agricultural **Ethanol Biofuel**.

---

## 🛢️ Fuel Types & Capacities

| Fuel Item | Namespace / Tag | Value Provided | Notes |
| :--- | :--- | :--- | :--- |
| **Diesel Canister** | `janemoose:diesel_canister` | $+1,000\text{ mB}$ | Realistic 32x32 pressed-steel yellow Jerry Can |
| **Diesel Bucket** | `refinery:diesel_bucket`, `#c:diesel` | $+1,000\text{ mB}$ | Returns empty bucket |
| **Ethanol Bucket** | `janemoose:ethanol_bucket`, `#c:ethanol` | $+1,000\text{ mB}$ | Produced in Fermentation Barrel |
| **Ethanol Fuels** | `#c:fuels/ethanol` | $+1,000\text{ mB}$ | Cross-mod compatible |

* **Total Fuel Tank Capacity**: $10,000\text{ mB}$ (10 Full Buckets or Canisters).
* **Fuel Consumption**: Engine consumes fuel proportionally to load — idling consumes minimal fuel, driving at transport speed consumes normal fuel, and operating heavy implements consumes increased fuel.

---

## ⛽ Realistic Jerry Can (Diesel Canister)

As of **v1.0.3**, the Diesel Canister features a 32x32 detailed pixel art texture modeled after real-life 20-liter NATO / industrial steel Jerry cans:
* **Industrial Yellow Finish**: Classic heavy equipment safety color.
* **Embossed "X" Stiffener Ribs**: Stamped into the side panels to prevent fuel vapor deformation.
* **Top Triple-Handle**: 3 welded tubular steel bars allowing one-person or two-person carrying.
* **Cam-Lock Spout**: Angled filler neck with brass cam-locking lever and air breather vent.
* **DIESEL Stencil**: High-contrast black hazard banner across the chest.

### Crafting Recipe
Crafted in survival with iron ingots and coal:
* Top: Iron Ingot
* Middle: Iron Ingot, Coal, Iron Ingot
* Bottom: Iron Ingot

---

## 🌾 Fermentation Barrel & Biofuel Loop

The **Fermentation Barrel** allows you to produce your own tractor fuel directly on your farm:
1. Place the barrel on your farm.
2. Insert **Wheat** into the input slot (or pipe it in via hoppers).
3. The barrel converts $1\text{ Wheat} \rightarrow 1,000\text{ mB of Ethanol}$ over 60 real-life seconds ($1,200\text{ ticks}$).
4. Right-click the barrel with an empty bucket to instantly draw an **Ethanol Bucket**, or use the internal tank storage ($8,000\text{ mB}$ capacity).
5. Refuel your Jane Moose tractor and keep farming indefinitely!
