package com.thewoodlands.event;

import com.thewoodlands.block.ModBlocks;
import com.thewoodlands.block.WoodPortalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "thewoodlands", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PortalEvents {

    @SubscribeEvent
    public static void onFlintAndSteelOnPlank(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        // Must be holding flint and steel
        if (!event.getEntity().getItemInHand(event.getHand()).is(Items.FLINT_AND_STEEL)) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState clicked = level.getBlockState(pos);

        // Must be clicking a wood plank
        if (!isWoodPlank(clicked)) return;

        // Try both axes
        boolean success = WoodPortalBlock.tryCreatePortal(level, pos, Direction.Axis.X)
                       || WoodPortalBlock.tryCreatePortal(level, pos, Direction.Axis.Z);

        if (success) {
            // Play a creepy wood creak sound instead of the normal lighter sound
            level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 0.5f);
            level.playSound(null, pos, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5f, 0.3f);
            event.setCanceled(true); // Don't place normal fire
        }
    }

    private static boolean isWoodPlank(BlockState state) {
        var b = state.getBlock();
        return b == Blocks.OAK_PLANKS
                || b == Blocks.DARK_OAK_PLANKS
                || b == Blocks.SPRUCE_PLANKS
                || b == Blocks.BIRCH_PLANKS
                || b == Blocks.JUNGLE_PLANKS
                || b == Blocks.ACACIA_PLANKS;
    }
}
