package com.thewoodlands.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks two things per player:
 *
 * 1. BUILDUP TIMER (Layer 1)
 *    Players enter Layer 1 and nothing happens for 15 minutes.
 *    After that, Wood People get their AI turned on and start hunting.
 *    The atmosphere should feel wrong before anything actually chases you.
 *
 * 2. MADNESS TIMER (Layer 2)
 *    The moment you arrive in Layer 2, madness begins accumulating.
 *    After enough time (~10 mins) you fully transform into a Wood Person.
 *
 *    Stage 0  (0–2 min):   Subtle darkness, faint nausea
 *    Stage 1  (2–5 min):   Blindness flashes, mining fatigue
 *    Stage 2  (5–8 min):   Near-constant blindness, slowness
 *    Stage 3  (8–10 min):  Full collapse — screen gone, can't move
 *    Stage 4  (10+ min):   Transformation — player becomes a Wood Person
 */
public class WoodlandsPlayerTracker {

    // ---- Buildup (Layer 1) ----
    private static final Map<UUID, Integer> buildupTicks = new HashMap<>();
    public static final int BUILDUP_THRESHOLD = 20 * 60 * 15; // 15 minutes

    // ---- Madness (Layer 2) ----
    private static final Map<UUID, Integer> madnessTicks = new HashMap<>();
    private static final int STAGE_1 = 20 * 60 * 2;   // 2 min
    private static final int STAGE_2 = 20 * 60 * 5;   // 5 min
    private static final int STAGE_3 = 20 * 60 * 8;   // 8 min
    public  static final int TRANSFORM = 20 * 60 * 10; // 10 min → become Wood Person

    // ---- Buildup API ----

    public static void tickBuildup(UUID id) {
        buildupTicks.merge(id, 1, Integer::sum);
    }

    public static boolean isBuildupComplete(UUID id) {
        return buildupTicks.getOrDefault(id, 0) >= BUILDUP_THRESHOLD;
    }

    public static int getBuildupTicks(UUID id) {
        return buildupTicks.getOrDefault(id, 0);
    }

    // ---- Madness API ----

    public static void tickMadness(ServerPlayer player) {
        int ticks = madnessTicks.merge(player.getUUID(), 1, Integer::sum);
        applyMadnessEffects(player, ticks);
        sendStageMessages(player, ticks);
    }

    /** Directly add madness ticks — used by Spider Boss hits. */
    public static void addMadness(UUID id, int amount) {
        madnessTicks.merge(id, amount, Integer::sum);
    }

    public static int getMadnessTicks(UUID id) {
        return madnessTicks.getOrDefault(id, 0);
    }

    public static boolean shouldTransform(UUID id) {
        return madnessTicks.getOrDefault(id, 0) >= TRANSFORM;
    }

    // ---- Cleanup ----

    public static void resetBuildup(UUID id) {
        buildupTicks.remove(id);
    }

    public static void resetMadness(UUID id) {
        madnessTicks.remove(id);
    }

    // ---- Private helpers ----

    private static void applyMadnessEffects(ServerPlayer player, int ticks) {
        if (ticks < STAGE_1) {
            // Stage 0: Something feels wrong
            applyEffect(player, MobEffects.DARKNESS, 60, 0);
            if (ticks % (20 * 30) == 0) {
                applyEffect(player, MobEffects.CONFUSION, 80, 0);
            }

        } else if (ticks < STAGE_2) {
            // Stage 1: Getting worse
            applyEffect(player, MobEffects.DARKNESS, 80, 0);
            applyEffect(player, MobEffects.DIG_SLOWDOWN, 60, 1);
            if (ticks % (20 * 12) == 0) {
                applyEffect(player, MobEffects.BLINDNESS, 40, 0);
                applyEffect(player, MobEffects.CONFUSION, 120, 0);
            }

        } else if (ticks < STAGE_3) {
            // Stage 2: Seriously wrong
            applyEffect(player, MobEffects.DARKNESS, 80, 1);
            applyEffect(player, MobEffects.MOVEMENT_SLOWDOWN, 60, 1);
            applyEffect(player, MobEffects.DIG_SLOWDOWN, 60, 2);
            if (ticks % (20 * 6) == 0) {
                applyEffect(player, MobEffects.BLINDNESS, 60, 0);
                applyEffect(player, MobEffects.CONFUSION, 200, 0);
            }

        } else if (ticks < TRANSFORM) {
            // Stage 3: Complete collapse
            applyEffect(player, MobEffects.DARKNESS, 80, 2);
            applyEffect(player, MobEffects.MOVEMENT_SLOWDOWN, 60, 3);
            applyEffect(player, MobEffects.WEAKNESS, 60, 2);
            applyEffect(player, MobEffects.DIG_SLOWDOWN, 60, 3);
            if (ticks % (20 * 3) == 0) {
                applyEffect(player, MobEffects.BLINDNESS, 100, 1);
                applyEffect(player, MobEffects.CONFUSION, 300, 0);
            }
        }
        // Stage 4 (TRANSFORM): handled by ModEventHandler — player becomes Wood Person
    }

    private static void sendStageMessages(ServerPlayer player, int ticks) {
        if (ticks == STAGE_1) {
            msg(player, "Something is wrong with your mind.");
        } else if (ticks == STAGE_2) {
            msg(player, "You can't remember how you got here.");
        } else if (ticks == STAGE_3) {
            msg(player, "The wood is inside you now.");
        } else if (ticks == TRANSFORM - 20 * 30) {
            // 30 second warning before transformation
            msg(player, "You are becoming one of them.");
        }
    }

    private static void msg(ServerPlayer player, String text) {
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal(text)
                        .withStyle(net.minecraft.ChatFormatting.DARK_RED,
                                   net.minecraft.ChatFormatting.ITALIC)
        );
    }

    private static void applyEffect(ServerPlayer player, net.minecraft.world.effect.MobEffect effect,
                                    int duration, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false));
    }
}
