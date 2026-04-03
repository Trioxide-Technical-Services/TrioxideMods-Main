package com.thewoodlands.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Generates the infinite wooden labyrinth seen in the StudioAllka videos.
 *
 * Structure layout (viewed from above):
 * - Flat oak plank floor at Y=64
 * - Ceiling of dark oak planks at Y=90
 * - Massive structural pillars every 12 blocks on a grid
 * - Horizontal beams connecting pillars at ceiling level
 * - Wooden cross/grave decorations scattered on the floor
 * - Occasional open vertical shafts dropping to lower floors (multi-level)
 * - Fire patches in Layer 3
 *
 * The "layer" parameter controls darkness and fire frequency.
 */
public class WoodlandsChunkGenerator extends ChunkGenerator {

    public static final Codec<WoodlandsChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    Codec.INT.fieldOf("layer").orElse(1).forGetter(g -> g.layer)
            ).apply(instance, WoodlandsChunkGenerator::new)
    );

    private final int layer;

    // ---- Constants ----
    private static final int FLOOR_Y = 64;
    private static final int CEILING_Y = 90;
    private static final int ROOM_HEIGHT = CEILING_Y - FLOOR_Y; // 26 blocks tall
    private static final int PILLAR_SPACING = 12;               // Grid every 12 blocks
    private static final int PILLAR_SIZE = 2;                   // 2x2 pillars
    private static final int LOWER_FLOOR_Y = 38;               // Second floor below (Layer 2+)
    private static final int LOWER_CEILING_Y = 62;

    public WoodlandsChunkGenerator(BiomeSource biomeSource, int layer) {
        super(biomeSource);
        this.layer = layer;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender,
                                                         RandomState randomState, StructureManager structureManager,
                                                         ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> {
            generateChunk(chunk);
            return chunk;
        }, executor);
    }

    private void generateChunk(ChunkAccess chunk) {
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        Random rand = new Random(chunkX * 341873128712L + chunkZ * 132897987541L);

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int worldX = chunkX * 16 + localX;
                int worldZ = chunkZ * 16 + localZ;

                generateColumn(chunk, localX, localZ, worldX, worldZ, rand);
            }
        }

        // Place wooden cross/grave decorations
        placeCrossDecorations(chunk, chunkX, chunkZ, rand);

        // Layer 3: scatter fire
        if (layer >= 3) {
            placeFirePatches(chunk, chunkX, chunkZ, rand);
        }
    }

    private void generateColumn(ChunkAccess chunk, int lx, int lz, int wx, int wz, Random rand) {
        boolean isPillar = isOnPillarGrid(wx, wz);
        boolean isBeam = isOnBeamGrid(wx, wz);

        // ---- Bedrock base (nothing gets through) ----
        setBlock(chunk, lx, 0, lz, Blocks.BEDROCK.defaultBlockState());

        // ---- Fill solid below lower floor ----
        for (int y = 1; y < LOWER_FLOOR_Y; y++) {
            setBlock(chunk, lx, y, lz, Blocks.DARK_OAK_PLANKS.defaultBlockState());
        }

        // ---- Lower floor (Layer 2+) ----
        if (layer >= 2) {
            setBlock(chunk, lx, LOWER_FLOOR_Y, lz, Blocks.DARK_OAK_PLANKS.defaultBlockState());
            // Lower ceiling = upper floor base
            for (int y = LOWER_CEILING_Y; y < FLOOR_Y; y++) {
                if (isPillar || y == LOWER_CEILING_Y || y == FLOOR_Y - 1) {
                    setBlock(chunk, lx, y, lz, Blocks.DARK_OAK_PLANKS.defaultBlockState());
                } else {
                    setBlock(chunk, lx, y, lz, Blocks.AIR.defaultBlockState());
                }
            }
            // Lower room air
            for (int y = LOWER_FLOOR_Y + 1; y < LOWER_CEILING_Y; y++) {
                if (isPillar) {
                    setBlock(chunk, lx, y, lz, Blocks.DARK_OAK_LOG.defaultBlockState());
                } else {
                    setBlock(chunk, lx, y, lz, Blocks.AIR.defaultBlockState());
                }
            }
        } else {
            // Layer 1: solid fill below main floor
            for (int y = 1; y < FLOOR_Y; y++) {
                setBlock(chunk, lx, y, lz, Blocks.DARK_OAK_PLANKS.defaultBlockState());
            }
        }

        // ---- Main floor ----
        // Mix of oak and dark oak planks for visual texture, like in screenshots
        BlockState floorBlock = ((wx + wz) % 7 == 0) ?
                Blocks.OAK_PLANKS.defaultBlockState() :
                Blocks.DARK_OAK_PLANKS.defaultBlockState();
        setBlock(chunk, lx, FLOOR_Y, lz, floorBlock);

        // ---- Room air ----
        for (int y = FLOOR_Y + 1; y < CEILING_Y; y++) {
            if (isPillar) {
                // Massive structural pillars - dark oak log
                setBlock(chunk, lx, y, lz, Blocks.DARK_OAK_LOG.defaultBlockState());
            } else if (isBeam && (y == CEILING_Y - 1 || y == FLOOR_Y + 1)) {
                // Horizontal beams at top and bottom of room
                setBlock(chunk, lx, y, lz, Blocks.DARK_OAK_PLANKS.defaultBlockState());
            } else {
                setBlock(chunk, lx, y, lz, Blocks.AIR.defaultBlockState());
            }
        }

        // ---- Ceiling ----
        setBlock(chunk, lx, CEILING_Y, lz, Blocks.DARK_OAK_PLANKS.defaultBlockState());
        // Second ceiling layer for thickness
        setBlock(chunk, lx, CEILING_Y + 1, lz, Blocks.DARK_OAK_PLANKS.defaultBlockState());

        // ---- Above ceiling: next floor up (infinite layering effect) ----
        for (int y = CEILING_Y + 2; y < CEILING_Y + 4; y++) {
            setBlock(chunk, lx, y, lz, Blocks.AIR.defaultBlockState());
        }
        // Upper floor surface
        setBlock(chunk, lx, CEILING_Y + 4, lz, Blocks.OAK_PLANKS.defaultBlockState());
        // Upper pillars continue upward
        for (int y = CEILING_Y + 5; y <= CEILING_Y + 20; y++) {
            if (isPillar) {
                setBlock(chunk, lx, y, lz, Blocks.DARK_OAK_LOG.defaultBlockState());
            }
        }
        // Upper ceiling
        for (int y = CEILING_Y + 20; y <= CEILING_Y + 22; y++) {
            setBlock(chunk, lx, y, lz, Blocks.DARK_OAK_PLANKS.defaultBlockState());
        }
    }

    /**
     * Is this position on a 2x2 pillar grid intersection?
     */
    private boolean isOnPillarGrid(int wx, int wz) {
        int modX = Math.floorMod(wx, PILLAR_SPACING);
        int modZ = Math.floorMod(wz, PILLAR_SPACING);
        return modX < PILLAR_SIZE && modZ < PILLAR_SIZE;
    }

    /**
     * Is this position on a beam running between pillars?
     */
    private boolean isOnBeamGrid(int wx, int wz) {
        int modX = Math.floorMod(wx, PILLAR_SPACING);
        int modZ = Math.floorMod(wz, PILLAR_SPACING);
        // Beams run along the first column/row of the grid
        return (modX == 0 || modX == 1) || (modZ == 0 || modZ == 1);
    }

    /**
     * Place wooden cross grave markers - exactly like seen in screenshots (images 3 & 4).
     * Cross shape: 1 tall block with 1 wide block through it.
     */
    private void placeCrossDecorations(ChunkAccess chunk, int chunkX, int chunkZ, Random rand) {
        // Place 3-6 crosses per chunk, avoiding pillar positions
        int count = 3 + rand.nextInt(4);
        for (int i = 0; i < count; i++) {
            int lx = 2 + rand.nextInt(12);
            int lz = 2 + rand.nextInt(12);
            int wx = chunkX * 16 + lx;
            int wz = chunkZ * 16 + lz;

            if (isOnPillarGrid(wx, wz)) continue;

            int baseY = FLOOR_Y + 1;

            // Vertical post (3 tall)
            setBlock(chunk, lx, baseY, lz, Blocks.OAK_FENCE.defaultBlockState());
            setBlock(chunk, lx, baseY + 1, lz, Blocks.OAK_FENCE.defaultBlockState());
            setBlock(chunk, lx, baseY + 2, lz, Blocks.OAK_PLANKS.defaultBlockState());

            // Horizontal crossbar at top
            if (lx > 0 && lx < 15) {
                setBlock(chunk, lx - 1, baseY + 2, lz, Blocks.OAK_PLANKS.defaultBlockState());
                setBlock(chunk, lx + 1, baseY + 2, lz, Blocks.OAK_PLANKS.defaultBlockState());
            }
        }
    }

    /**
     * Scatter fire patches for Layer 3 (The Rot) - like image 6.
     */
    private void placeFirePatches(ChunkAccess chunk, int chunkX, int chunkZ, Random rand) {
        int fireCount = 2 + rand.nextInt(5);
        for (int i = 0; i < fireCount; i++) {
            int lx = rand.nextInt(16);
            int lz = rand.nextInt(16);
            int wx = chunkX * 16 + lx;
            int wz = chunkZ * 16 + lz;

            if (isOnPillarGrid(wx, wz)) continue;

            // Place fire on the floor
            setBlock(chunk, lx, FLOOR_Y + 1, lz, Blocks.FIRE.defaultBlockState());

            // Sometimes a bigger fire patch
            if (rand.nextInt(3) == 0 && lx > 0 && lx < 15 && lz > 0 && lz < 15) {
                setBlock(chunk, lx + 1, FLOOR_Y + 1, lz, Blocks.FIRE.defaultBlockState());
                setBlock(chunk, lx, FLOOR_Y + 1, lz + 1, Blocks.FIRE.defaultBlockState());
                // Stack some burning oak blocks to create the burning pile effect
                setBlock(chunk, lx, FLOOR_Y + 1, lz, Blocks.OAK_PLANKS.defaultBlockState());
                setBlock(chunk, lx, FLOOR_Y + 2, lz, Blocks.FIRE.defaultBlockState());
            }
        }
    }

    private void setBlock(ChunkAccess chunk, int x, int y, int z, BlockState state) {
        chunk.setBlockState(new BlockPos(x, y, z), state, false);
    }

    // ---- Required overrides ----

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState,
                              BiomeManager biomeManager, StructureManager structureManager,
                              ChunkAccess chunk, GenerationStep.Carving step) {
        // No carvers - the labyrinth structure IS the world
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager,
                              RandomState randomState, ChunkAccess chunk) {
        // Surface is handled in fillFromNoise
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // Mob spawning handled normally by the biome system
    }

    @Override
    public int getGenDepth() {
        return 256;
    }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(Executor executor, RandomState randomState,
                                                         Blender blender, StructureManager structureManager,
                                                         ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> {
            chunk.fillBiomesFromNoise(this.getBiomeSource()::getNoiseBiome, randomState.sampler());
            return chunk;
        }, executor);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        // No additional decoration needed - structure is complete from fillFromNoise
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {
        return FLOOR_Y + 1;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
        BlockState[] states = new BlockState[level.getHeight()];
        for (int i = 0; i < states.length; i++) {
            states[i] = Blocks.AIR.defaultBlockState();
        }
        states[FLOOR_Y] = Blocks.DARK_OAK_PLANKS.defaultBlockState();
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
        info.add("The Woodlands Layer " + layer);
    }
}
