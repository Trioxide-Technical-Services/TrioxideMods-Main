package com.thewoodlands.entity;

import com.thewoodlands.event.ModEventHandler;
import com.thewoodlands.event.WoodlandsPlayerTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class SpiderBossEntity extends Monster {

    // How many ticks of madness to add on each hit (20 ticks = 1 second)
    // A hit adds ~90 seconds worth of madness — very punishing
    private static final int MADNESS_ON_HIT = 20 * 90;

    public SpiderBossEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setSilent(false); // Unlike Wood People, this thing makes noise
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)       // Very hard to kill
                .add(Attributes.MOVEMENT_SPEED, 0.32)    // Fast but not as fast as Wood People
                .add(Attributes.FOLLOW_RANGE, 80.0)      // Sees you from very far
                .add(Attributes.ATTACK_DAMAGE, 8.0)      // High damage — 4 hearts per hit
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ARMOR, 10.0);            // Tanky
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 24.0f));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);

        if (hit && target instanceof ServerPlayer player
                && !player.isCreative() && !player.isSpectator()) {

            // Accelerate madness significantly on each hit
            WoodlandsPlayerTracker.addMadness(player.getUUID(), MADNESS_ON_HIT);

            // Check if the hit pushed them over the transformation threshold
            if (WoodlandsPlayerTracker.shouldTransform(player.getUUID())) {
                ModEventHandler.transformIntoWoodPerson(player);
            }
        }

        return hit;
    }

    /**
     * If the Spider Boss outright kills a player → instant Wood Person transformation.
     * No waiting, no countdown — gone immediately.
     */
    @Override
    public void kill() {
        // This is called when the boss itself is killed, not the player
        super.kill();
    }

    // We intercept player death via the LivingDeathEvent in SpiderBossEvents instead,
    // since doHurtTarget doesn't always fire on the killing blow.

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        // A deep, wrong creaking sound
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOOD_BREAK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOOD_FALL;
    }

    @Override
    protected SoundEvent getStepSound() {
        // Slow heavy footsteps — you can hear it coming
        return SoundEvents.SPIDER_STEP;
    }

    @Override
    public boolean removeWhenFarAway(double dist) { return false; }

    @Override
    public boolean fireImmune() { return true; }
}
