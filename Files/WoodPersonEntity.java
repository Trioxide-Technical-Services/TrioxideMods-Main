package com.thewoodlands.entity;

import com.thewoodlands.dimension.DimensionTeleporter;
import com.thewoodlands.dimension.ModDimensions;
import com.thewoodlands.event.WoodlandsPlayerTracker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class WoodPersonEntity extends Monster {

    private static final int TELEPORT_COOLDOWN_TICKS = 60;
    private int teleportCooldown = 0;

    public WoodPersonEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setSilent(true); // No sound — completely silent
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.38)       // Fast — faster than sprinting player
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0)         // Doesn't hurt — it TAKES you
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (teleportCooldown > 0) teleportCooldown--;

        if (!this.level().isClientSide && teleportCooldown == 0) {
            checkForPlayerContact();
        }

        // Transformed players are slower — they're still "them"
        if (isTransformed()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.22);
        }
    }

    /**
     * Wood People only chase after the 15-minute buildup is complete.
     * Transformed players (former humans) chase regardless but slower.
     * This is checked every tick by suppressing the target if buildup isn't done.
     */
    @Override
    public boolean isAggressive() {
        if (isTransformed()) return true; // Transformed always chase

        // Regular Wood People: only active after buildup
        var nearestPlayer = this.level().getNearestPlayer(this, 64);
        if (nearestPlayer instanceof ServerPlayer sp) {
            if (ModDimensions.WOODLANDS_1.equals(this.level().dimension())) {
                return WoodlandsPlayerTracker.isBuildupComplete(sp.getUUID());
            }
        }
        return true; // In Layer 2, always aggressive
    }

    private void checkForPlayerContact() {
        ResourceKey<Level> dim = this.level().dimension();
        if (!ModDimensions.isWoodlandsDimension(dim)) return;

        // Regular Wood People respect the buildup timer in Layer 1
        if (ModDimensions.WOODLANDS_1.equals(dim) && !isTransformed()) {
            var nearestPlayer = this.level().getNearestPlayer(this, 2);
            if (nearestPlayer instanceof ServerPlayer sp
                    && !WoodlandsPlayerTracker.isBuildupComplete(sp.getUUID())) {
                return; // Not yet
            }
        }

        AABB touchbox = this.getBoundingBox().inflate(0.5);
        List<Player> nearby = this.level().getEntitiesOfClass(Player.class, touchbox);

        for (Player player : nearby) {
            if (player instanceof ServerPlayer sp
                    && !sp.isCreative() && !sp.isSpectator()) {
                DimensionTeleporter.sendPlayerDeeper(sp);
                teleportCooldown = TELEPORT_COOLDOWN_TICKS;
                break;
            }
        }
    }

    /**
     * Returns true if this Wood Person is a transformed player (former human).
     * They're slower, have a nametag, and are slightly less aggressive.
     */
    public boolean isTransformed() {
        return this.getPersistentData().getBoolean("IsTransformed");
    }

    // ---- Completely silent ----

    @Nullable @Override protected SoundEvent getAmbientSound() { return null; }
    @Nullable @Override protected SoundEvent getHurtSound(DamageSource s) { return null; }
    @Nullable @Override protected SoundEvent getDeathSound() { return SoundEvents.WOOD_BREAK; }
    @Override protected SoundEvent getStepSound() { return null; }

    @Override public boolean removeWhenFarAway(double dist) { return false; }
    @Override public boolean fireImmune() { return true; }
}
