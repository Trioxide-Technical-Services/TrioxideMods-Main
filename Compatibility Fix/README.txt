========================================
BACKROOMS-TERRALITH COMPATIBILITY FIX
========================================
Version: 1.0.0
Minecraft: 1.20.1
Loader: Forge

REQUIREMENTS:
-------------
- Minecraft Forge 1.20.1 (47.x.x+)
- TerraBlender mod (required)
- Faithful Backrooms mod (required)
- Terralith mod (optional but fully compatible)

INSTALLATION:
-------------
1. Build the mod using: ./gradlew build
2. Find the jar in build/libs/
3. Place the jar in your mods folder

Or for development:
1. Run: ./gradlew genEclipseRuns (or genIntellijRuns)
2. Import the project into your IDE

WHAT THIS FIXES:
----------------
- Almond Forest biome now spawns NATURALLY in the overworld
- Uses TerraBlender for proper biome injection (no conflicts!)
- Works alongside Terralith and other worldgen mods
- Trees and vegetation spawn correctly on the surface
- Fixes the issue where biome shows but terrain is inside walls
- Also provides a dedicated dimension for guaranteed access

HOW IT WORKS:
-------------
1. Uses TerraBlender's Region API to inject Almond Forest into overworld
2. Creates custom surface rules for proper grass/dirt terrain
3. Biome modifiers add trees, grass, and flowers
4. Fixed placed features use correct heightmap placement
5. No overworld.json conflicts with other mods!

ALMOND FOREST SPAWN CONDITIONS:
-------------------------------
The Almond Forest spawns naturally in the overworld with these parameters:
- Temperature: 0.2 to 0.5 (moderate, like forests/plains)
- Humidity: 0.0 to 0.35 (slightly dry areas)
- Continentalness: 0.3 to 1.0 (inland, not coastal)
- Erosion: -0.375 to 0.45 (rolling hills to flat)
- Region Weight: 5 (relatively rare but findable)

Look for it near regular forests and plains!

COMMANDS:
---------
Locate Almond Forest in overworld:
  /locatebiome faithfulbackrooms:almond_forest

Teleport to dedicated Almond Forest dimension:
  /execute in backrooms_terralith_compat:almond_forest run tp @s 0 64 0

BUILDING FROM SOURCE:
---------------------
Requirements:
- Java 17+
- Gradle 8.x

Commands:
  ./gradlew build        - Build the mod jar
  ./gradlew runClient    - Run Minecraft client with mod

The built jar will be in: build/libs/backrooms-terralith-compat-1.0.0.jar

CREDITS:
--------
Compatibility fix by TrioxideMods
Original Backrooms mod by WhyAntique
TerraBlender by Glitchfiend
Terralith by Stardust Labs
