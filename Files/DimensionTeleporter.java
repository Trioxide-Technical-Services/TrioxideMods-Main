package com.thewoodlands.dimension;

import com.thewoodlands.TheWoodlands;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class DimensionTeleporter {

    /**
     * Called when a Wood Person touches a player in Layer 1.
     * Spawns the lore book at their exact position (for the surviving player to find),
     * broadcasts the message, and sends them to Layer 2.
     */
    public static void sendPlayerDeeper(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ResourceKey<Level> currentDim = player.level().dimension();
        int currentLayer = ModDimensions.getLayer(currentDim);

        // Already at Layer 2 — Spider Boss handles further effects, no teleporting
        if (currentLayer >= 2) return;

        ServerLevel currentLevel = (ServerLevel) player.level();
        BlockPos takenPos = player.blockPosition();

        // Spawn the book at the exact spot they were standing
        // The surviving player will find this while hiding from Wood People
        spawnLoreBook(currentLevel, takenPos, player.getName().getString());

        broadcastTaken(server, player.getName().getString());

        ServerLevel layer2 = server.getLevel(ModDimensions.WOODLANDS_2);
        if (layer2 == null) return;
        teleportToLevel(player, layer2);
    }

    /**
     * Called when a surviving player in Layer 1 lingers on the lore book.
     * Sends them to Layer 2 — same place as their friend.
     */
    public static void pullPlayerIntoLayer2(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerLevel layer2 = server.getLevel(ModDimensions.WOODLANDS_2);
        if (layer2 == null) return;

        broadcastTaken(server, player.getName().getString());
        teleportToLevel(player, layer2);
    }

    /**
     * Called when a player first steps through the wooden portal.
     * Sends them to Layer 1. Wood People won't chase for 15 minutes.
     */
    public static void enterViaPortal(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerLevel layer1 = server.getLevel(ModDimensions.WOODLANDS_1);
        if (layer1 == null) return;

        teleportToLevel(player, layer1);
        TheWoodlands.LOGGER.info("Player {} entered the woodlands via portal", player.getName().getString());
    }

    /**
     * Called when a surviving player lingers on the lore book too long.
     * Sends them to Layer 1 (where their friend originally went).
     */
    public static void pullPlayerIn(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerLevel layer1 = server.getLevel(ModDimensions.WOODLANDS_1);
        if (layer1 == null) return;

        broadcastTaken(server, player.getName().getString());
        teleportToLevel(player, layer1);
    }

    /**
     * Spawns the lore book "Go meet Them_" at the given position.
     * This is a separate mechanic — dropped by some other trigger,
     * not by a player getting touched.
     */
    public static void spawnLoreBook(ServerLevel level, BlockPos pos, String authorName) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("Go meet Them_"))
        ));

        CompoundTag tag = book.getOrCreateTag();
        tag.put("pages", pages);
        tag.putString("title", "Go meet Them");
        tag.putString("author", authorName);
        tag.putBoolean("resolved", true);
        book.setTag(tag);

        ItemEntity bookEntity = new ItemEntity(
                level,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                book
        );
        bookEntity.setPickUpDelay(0);
        level.addFreshEntity(bookEntity);
    }

    public static void tryEscape(ServerPlayer player) {
        ServerLevel overworld = player.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        player.teleportTo(overworld,
                player.getX(), 64, player.getZ(),
                player.getYRot(), player.getXRot());

        player.sendSystemMessage(Component.literal("You escaped the woodlands. For now.")
                .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.ITALIC));
    }

    // ---- Private helpers ----

    private static void teleportToLevel(ServerPlayer player, ServerLevel targetLevel) {
        double x = player.getX();
        double z = player.getZ();
        double y = targetLevel.getMaxBuildHeight() - 10;

        for (int scanY = (int) y; scanY > targetLevel.getMinBuildHeight(); scanY--) {
            if (!targetLevel.getBlockState(new BlockPos((int) x, scanY, (int) z)).isAir()) {
                y = scanY + 1;
                break;
            }
        }

        player.teleportTo(targetLevel, x, y, z, player.getYRot(), player.getXRot());
    }

    // "[username] won't come back" — just the name, no suffixes
    private static void broadcastTaken(MinecraftServer server, String username) {
        Component message = Component.literal(username + " won't come back")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);

        for (ServerPlayer online : server.getPlayerList().getPlayers()) {
            online.sendSystemMessage(message);
        }
    }
}
