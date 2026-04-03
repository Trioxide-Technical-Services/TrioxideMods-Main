package com.thewoodlands.event;

import com.thewoodlands.dimension.DimensionTeleporter;
import com.thewoodlands.dimension.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "thewoodlands", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LoreBookEvents {

    // 5 seconds of holding the book open before getting pulled in
    private static final int LINGER_TICKS = 20 * 5;

    @SubscribeEvent
    public static void onRightClickBook(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Only triggers inside Layer 1 — that's where the book spawns
        if (!ModDimensions.WOODLANDS_1.equals(player.level().dimension())) return;

        ItemStack held = event.getItemStack();
        if (!held.is(Items.WRITTEN_BOOK)) return;

        CompoundTag tag = held.getTag();
        if (tag == null || !tag.getString("title").equals("Go meet Them")) return;

        // Start the linger countdown
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean("thewoodlands:pendingPull")) {
            data.putBoolean("thewoodlands:pendingPull", true);
            data.putInt("thewoodlands:pullCountdown", LINGER_TICKS);
        }
    }

    /**
     * Called every tick from ModEventHandler.
     * If player keeps the book open long enough they get sent to Layer 2.
     * Putting the book away cancels it.
     */
    public static void checkPendingPull(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean("thewoodlands:pendingPull")) return;

        // Must still be in Layer 1
        if (!ModDimensions.WOODLANDS_1.equals(player.level().dimension())) {
            data.remove("thewoodlands:pendingPull");
            data.remove("thewoodlands:pullCountdown");
            return;
        }

        // Cancel if they put the book away
        ItemStack held = player.getMainHandItem();
        boolean stillHolding = held.is(Items.WRITTEN_BOOK)
                && held.hasTag()
                && held.getTag().getString("title").equals("Go meet Them");

        if (!stillHolding) {
            data.remove("thewoodlands:pendingPull");
            data.remove("thewoodlands:pullCountdown");
            return;
        }

        int countdown = data.getInt("thewoodlands:pullCountdown") - 1;
        if (countdown > 0) {
            data.putInt("thewoodlands:pullCountdown", countdown);
            return;
        }

        // They lingered — send them to Layer 2 (same as where their friend went)
        data.remove("thewoodlands:pendingPull");
        data.remove("thewoodlands:pullCountdown");
        player.closeContainer();
        DimensionTeleporter.pullPlayerIntoLayer2(player);
    }
}
