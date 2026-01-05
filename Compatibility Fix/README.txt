========================================
BACKROOMS-TERRALITH COMPATIBILITY FIX
========================================
Version: 1.0.0
Minecraft: 1.20.1
Loader: Forge

REQUIREMENTS:
-------------
- Java 17+
- Minecraft Forge 1.20.1 (47.x.x+)
- TerraBlender mod
- Faithful Backrooms mod
- Terralith mod (optional but fully compatible)

========================================
HOW TO BUILD (Step by Step)
========================================

1. Download Forge MDK:
   - Go to: https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html
   - Click "Mdk" to download
   - Extract the zip to a folder (e.g., "forge-mdk")

2. Copy these files FROM this folder INTO the extracted MDK folder:
   - build.gradle        (replace the existing one)
   - gradle.properties   (replace the existing one)
   - settings.gradle     (replace the existing one)
   - src/                (copy the entire folder)

3. Open a command prompt in the MDK folder and run:
   gradlew build

4. Find your compiled mod at:
   build/libs/backrooms_terralith_compat-1.0.0.jar

5. Copy the jar to your Minecraft mods folder:
   %APPDATA%\.minecraft\mods\

========================================
WHAT THIS MOD DOES
========================================
- Almond Forest biome spawns NATURALLY in the overworld
- Uses TerraBlender for proper biome injection (no conflicts!)
- Works alongside Terralith and other worldgen mods
- Trees and vegetation spawn correctly on the surface
- Sleep in the Almond Forest to enter the Backrooms!

ALMOND FOREST SPAWN CONDITIONS:
- Temperature: Moderate (like forests/plains)
- Humidity: Slightly dry areas
- Location: Inland (not coastal)
- Rarity: Uncommon but findable

COMMANDS:
---------
/locatebiome faithfulbackrooms:almond_forest

CREDITS:
--------
Compatibility fix by TrioxideMods
Original Backrooms mod by WhyAntique
TerraBlender by Glitchfiend
Terralith by Stardust Labs
