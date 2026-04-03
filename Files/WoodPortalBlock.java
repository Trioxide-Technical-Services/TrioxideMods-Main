package com.thewoodlands.block;

import com.thewoodlands.TheWoodlands;
import com.thewoodlands.dimension.DimensionTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoodPortalBlock extends Block {

    // The portal is a thin slab-like block — you walk through it
    protected static final VoxelShape SHAPE_X = Block.box(6, 0, 0, 10, 16, 16);
    protected static final VoxelShape SHAPE_Z = Block.box(0, 0, 6, 16, 16, 10);

    public WoodPortalBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .noCollission()          // Walk through it
                .strength(-1.0f)         // Indestructible like nether portal
                .lightLevel(s -> 3)      // Faint amber glow
                .noLootTable()
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE_Z; // Default — proper axis handling would need a blockstate property
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;
        if (player.isCreative() || player.isSpectator()) return;

        // Only teleport from overworld
        if (!level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) return;

        // Cooldown so they don't get teleported every tick while inside
        if (player.portalCooldown > 0) return;
        player.portalCooldown = 80; // 4 second cooldown

        DimensionTeleporter.enterViaPortal(player);
    }

    /**
     * Called when flint and steel is used on a wood plank frame.
     * Checks for a valid frame and fills it with portal blocks.
     *
     * Valid frame: minimum 4x5 (inner 2x3) of oak or dark oak planks,
     * same shape as a nether portal frame.
     */
    public static boolean tryCreatePortal(Level level, BlockPos pos, Direction.Axis axis) {
        // Find the bottom-left of a valid frame
        BlockPos frameOrigin = findFrameOrigin(level, pos, axis);
        if (frameOrigin == null) return false;

        fillPortal(level, frameOrigin, axis);
        TheWoodlands.LOGGER.info("Woodlands portal created at {}", frameOrigin);
        return true;
    }

    private static BlockPos findFrameOrigin(Level level, BlockPos ignitePos, Direction.Axis axis) {
        // Scan outward from ignition point to find a valid plank frame
        // Minimum inner size: 2 wide x 3 tall (like smallest nether portal)
        for (int searchY = ignitePos.getY(); searchY >= ignitePos.getY() - 4; searchY--) {
            for (int searchOffset = -2; searchOffset <= 2; searchOffset++) {
                BlockPos candidate;
                if (axis == Direction.Axis.X) {
                    candidate = new BlockPos(ignitePos.getX() + searchOffset, searchY, ignitePos.getZ());
                } else {
                    candidate = new BlockPos(ignitePos.getX(), searchY, ignitePos.getZ() + searchOffset);
                }

                if (isValidFrame(level, candidate, axis)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private static boolean isValidFrame(Level level, BlockPos bottomLeft, Direction.Axis axis) {
        // Check a 4-wide x 5-tall frame (inner 2x3) of wood planks
        int width = 4;
        int height = 5;

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                BlockPos check;
                if (axis == Direction.Axis.X) {
                    check = bottomLeft.offset(w, h, 0);
                } else {
                    check = bottomLeft.offset(0, h, w);
                }

                boolean isFrame = w == 0 || w == width - 1 || h == 0 || h == height - 1;
                BlockState bs = level.getBlockState(check);

                if (isFrame) {
                    // Frame must be wood planks
                    if (!isWoodPlank(bs)) return false;
                } else {
                    // Interior must be air
                    if (!bs.isAir()) return false;
                }
            }
        }
        return true;
    }

    private static void fillPortal(Level level, BlockPos bottomLeft, Direction.Axis axis) {
        // Fill interior 2x3 with portal blocks
        WoodPortalBlock portalBlock = ModBlocks.WOOD_PORTAL.get();
        for (int w = 1; w <= 2; w++) {
            for (int h = 1; h <= 3; h++) {
                BlockPos fill;
                if (axis == Direction.Axis.X) {
                    fill = bottomLeft.offset(w, h, 0);
                } else {
                    fill = bottomLeft.offset(0, h, w);
                }
                level.setBlock(fill, portalBlock.defaultBlockState(), 3);
            }
        }
    }

    private static boolean isWoodPlank(BlockState state) {
        Block b = state.getBlock();
        return b == Blocks.OAK_PLANKS
                || b == Blocks.DARK_OAK_PLANKS
                || b == Blocks.SPRUCE_PLANKS
                || b == Blocks.BIRCH_PLANKS
                || b == Blocks.JUNGLE_PLANKS
                || b == Blocks.ACACIA_PLANKS;
    }
}
