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
   - Terralith mod (optional)

WHAT THIS FIXES:
----------------
- Almond Forest biome now spawns NATURALLY in the overworld
- Trees and vegetation spawn correctly on the surface
- Fixes the issue where biome shows but terrain is inside walls
- Biome spawns in temperate, slightly dry inland areas
- Also provides a dedicated dimension for guaranteed access

HOW IT WORKS:
-------------
1. Adds Almond Forest to the overworld's multi-noise biome source
2. Overrides the biome with proper feature placement (trees, grass, flowers)
3. Uses WORLD_SURFACE_WG heightmap so trees spawn on actual ground
4. Adds surface rules for correct grass/dirt terrain generation
5. Creates biome modifiers to ensure vegetation features work
6. Provides a backup dedicated dimension with flat forest terrain

ALMOND FOREST SPAWN CONDITIONS:
-------------------------------
The Almond Forest spawns naturally in the overworld with these parameters:
- Temperature: Moderate (like forests/plains)
- Humidity: Low to moderate (drier areas)
- Continentalness: Inland areas (not coastal)
- Terrain: Rolling hills to flat areas

Look for it near regular forests and plains!

IMPORTANT - TERRALITH COMPATIBILITY:
------------------------------------
If using Terralith, this mod's overworld.json may conflict with
Terralith's worldgen. To ensure compatibility:

OPTION 1: Use this mod WITHOUT Terralith for vanilla + Almond Forest

OPTION 2: Use the dedicated dimension instead:
  /execute in backrooms_terralith_compat:almond_forest run tp @s 0 64 0

OPTION 3: For full Terralith + Almond Forest support, you may need
  TerraBlender mod for proper biome injection.

COMMANDS:
---------
Teleport to dedicated Almond Forest dimension:
  /execute in backrooms_terralith_compat:almond_forest run tp @s 0 64 0

Locate Almond Forest in overworld:
  /locatebiome faithfulbackrooms:almond_forest

CREDITS:
--------
Compatibility fix by TrioxideMods
Original Backrooms mod by WhyAntique
Terralith by Stardust Labs
