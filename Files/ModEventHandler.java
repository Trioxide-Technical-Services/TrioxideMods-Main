package com.thewoodlands.event;

import com.thewoodlands.dimension.ModDimensions;
import com.thewoodlands.entity.ModEntities;
import com.thewoodlands.entity.WoodPersonEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "thewoodlands", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        // Check pending book pull every tick
        LoreBookEvents.checkPendingPull(player);

        var dim = player.level().dimension();

        if (ModDimensions.WOODLANDS_1.equals(dim)) {
            tickLayer1(player);

        } else if (ModDimensions.WOODLANDS_2.equals(dim)) {
            tickLayer2(player);

        } else {
            // Back in overworld — reset everything
            if (WoodlandsPlayerTracker.getBuildupTicks(player.getUUID()) > 0) {
                WoodlandsPlayerTracker.resetBuildup(player.getUUID());
            }
            if (WoodlandsPlayerTracker.getMadnessTicks(player.getUUID()) > 0) {
                WoodlandsPlayerTracker.resetMadness(player.getUUID());
                player.sendSystemMessage(Component.literal("You escaped the woodlands. For now.")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
    }

    private static void tickLayer1(ServerPlayer player) {
        WoodlandsPlayerTracker.tickBuildup(player.getUUID());

        // Ambient darkness — the dimension always feels wrong
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false));

        // At exactly 15 minutes: warn the player things are changing
        int ticks = WoodlandsPlayerTracker.getBuildupTicks(player.getUUID());
        if (ticks == WoodlandsPlayerTracker.BUILDUP_THRESHOLD) {
            player.sendSystemMessage(
                    Component.literal("You are not alone.")
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
            );
        }

        // Wood People AI is gated by isBuildupComplete() — checked in WoodPersonEntity
    }

    private static void tickLayer2(ServerPlayer player) {
        // Tick madness — applies effects and sends stage messages internally
        WoodlandsPlayerTracker.tickMadness(player);

        // Check if transformation threshold is reached
        if (WoodlandsPlayerTracker.shouldTransform(player.getUUID())) {
            transformIntoWoodPerson(player);
        }
    }

    /**
     * The player has been in Layer 2 too long.
     * They become a Wood Person — literally replaced.
     * They respawn in the overworld (vanilla respawn handles this).
     */
    public static void transformIntoWoodPerson(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();

        // Final message before transformation
        player.sendSystemMessage(
                Component.literal("You are one of them now.")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
        );

        // Broadcast to all players
        Component broadcast = Component.literal(
                player.getName().getString() + " has become one of the wood.")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
        for (ServerPlayer online : player.getServer().getPlayerList().getPlayers()) {
            online.sendSystemMessage(broadcast);
        }

        // Spawn the Wood Person replacement at player's position
        // This one is "them" — slower, has their nametag, has cape if they had one
        WoodPersonEntity woodPerson = ModEntities.WOOD_PERSON.get().create(level);
        if (woodPerson != null) {
            woodPerson.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0);

            // Tag it as a transformed player
            woodPerson.getPersistentData().putString("OriginalPlayer", player.getName().getString());
            woodPerson.getPersistentData().putUUID("OriginalUUID", player.getUUID());
            woodPerson.getPersistentData().putBoolean("IsTransformed", true);

            // Cape check
            var chestplate = player.getInventory().armor.get(2);
            boolean hasCape = !chestplate.isEmpty()
                    && chestplate.is(net.minecraft.world.item.Items.ELYTRA);
            woodPerson.getPersistentData().putBoolean("HasCape", hasCape);

            // Give it their display name so it shows above their head
            woodPerson.setCustomName(Component.literal(player.getName().getString())
                    .withStyle(ChatFormatting.DARK_GRAY));
            woodPerson.setCustomNameVisible(true);

            level.addFreshEntity(woodPerson);
        }

        // Reset madness so we don't keep transforming
        WoodlandsPlayerTracker.resetMadness(player.getUUID());

        // Kill the player — they respawn in overworld via vanilla respawn
        player.getInventory().dropAll();
        player.kill();
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Leaving Layer 2 resets madness (if they somehow escape)
        if (ModDimensions.WOODLANDS_2.equals(event.getFrom())
                && !ModDimensions.WOODLANDS_2.equals(event.getTo())) {
            WoodlandsPlayerTracker.resetMadness(player.getUUID());
        }

        // Leaving Layer 1 resets buildup
        if (ModDimensions.WOODLANDS_1.equals(event.getFrom())
                && !ModDimensions.WOODLANDS_1.equals(event.getTo())) {
            WoodlandsPlayerTracker.resetBuildup(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        WoodlandsPlayerTracker.resetBuildup(event.getEntity().getUUID());
        WoodlandsPlayerTracker.resetMadness(event.getEntity().getUUID());
    }
}
