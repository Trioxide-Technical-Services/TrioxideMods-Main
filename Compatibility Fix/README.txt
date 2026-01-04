========================================
BACKROOMS-TERRALITH COMPATIBILITY FIX
========================================
Version: 1.0.0
Minecraft: 1.20.1
Loader: Forge

INSTALLATION:
-------------
1. Place this entire folder into your mods directory as a folder mod
   OR zip the contents and rename to .jar

2. Make sure you have both installed:
   - Faithful Backrooms mod
   - Terralith mod (optional but recommended)

WHAT THIS FIXES:
----------------
- Almond Forest biome now spawns with proper terrain generation
- Trees and vegetation spawn correctly on the surface
- Fixes the issue where biome shows but terrain is inside walls
- Creates a dedicated dimension for Almond Forest accessible via
  /execute in backrooms_terralith_compat:almond_forest run tp @s ~ ~ ~

HOW IT WORKS:
-------------
1. Overrides the Almond Forest biome with proper feature placement
2. Adds surface rules for correct grass/dirt terrain generation
3. Creates biome modifiers to add vegetation features
4. Removes conflicting Terralith surface features from the biome
5. Provides a dedicated dimension with flat forest terrain

ACCESSING THE ALMOND FOREST:
----------------------------
Use the command:
/execute in backrooms_terralith_compat:almond_forest run tp @s 0 64 0

Or create a custom portal/teleporter in your world.

CREDITS:
--------
Compatibility fix by TrioxideMods
Original Backrooms mod by WhyAntique
Terralith by Stardust Labs
