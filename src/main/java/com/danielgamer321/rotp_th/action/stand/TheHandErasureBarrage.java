package com.danielgamer321.rotp_th.action.stand;

import com.danielgamer321.rotp_th.RotpTheHandConfig;
import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.danielgamer321.rotp_th.init.InitStands;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityLightAttack;
import com.github.standobyte.jojo.action.stand.StandEntityMeleeBarrage;
import com.github.standobyte.jojo.action.stand.punch.IPunch;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.entity.stand.StandStatFormulas;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.general.LazySupplier;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TheHandErasureBarrage extends StandEntityMeleeBarrage {
    public static final StandPose ERASURE_BARRAGE_POSE = new StandPose("erasureBarrage");
    public TheHandErasureBarrage(Builder builder) {
        super(builder);
    }

    @Override
    public void onTaskSet(World world, StandEntity standEntity, IStandPower standPower, Phase phase, StandEntityTask task, int ticks) {
        super.onTaskSet(world, standEntity, standPower, phase, task, ticks);
        TheHandEntity theHand = (TheHandEntity) standEntity;
        if (standEntity.getUser() != null) {
            theHand.attractTarget(standEntity.getUser().isShiftKeyDown());
        }
        if (theHand.swingingArm != Hand.OFF_HAND) {
            theHand.alternateHands();
        }
        if (!world.isClientSide()) {
            theHand.setErase(true);
            theHand.somethingWasErased(true);
        }
        theHand.recoveryCount = 0;
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        int hitsThisTick = 0;
        double speed = standEntity.getAttackSpeed();
        int hitsPerSecond = StandStatFormulas.getBarrageHitsPerSecond(speed);
        Entity prevOpponent = standEntity.barrageClashOpponent().orElse(null);
        if (prevOpponent instanceof StandEntity) {
            StandEntity prevStandOpponent = (StandEntity) prevOpponent;
            Entity opponetsOppenent = prevStandOpponent.barrageClashOpponent().orElse(null);
            if (opponetsOppenent != null && opponetsOppenent == standEntity) {
                speed = speed * 0.834;
                double enemySpeed = prevStandOpponent.getAttackSpeed();
                if (enemySpeed > speed) {
                    speed = speed * ((speed / enemySpeed) / enemySpeed);
                    hitsPerSecond = StandStatFormulas.getBarrageHitsPerSecond(speed);
                }
            }
        }
        int extraTickSwings = hitsPerSecond / 20;
        for (int i = 0; i < extraTickSwings; i++) {
            hitsThisTick++;
        }
        hitsPerSecond -= extraTickSwings * 20;

        if (standEntity.barrageHandler.popDelayedHit()) {
            hitsThisTick++;
        }
        else if (hitsPerSecond > 0) {
            double ticksInterval = 20D / hitsPerSecond;
            int intTicksInterval = (int) ticksInterval;
            if ((getStandActionTicks(userPower, standEntity) - task.getTick() + standEntity.barrageHandler.getHitsDelayed()) % intTicksInterval == 0) {
                if (!world.isClientSide()) {
                    double delayProb = ticksInterval - intTicksInterval;
                    if (standEntity.getRandom().nextDouble() < delayProb) {
                        standEntity.barrageHandler.delayHit();
                    }
                    else {
                        hitsThisTick++;
                    }
                }
            }
        }
        int barrageHits = hitsThisTick;
        standEntity.setBarrageHitsThisTick(barrageHits);
        if (barrageHits > 0) {
            standEntity.punch(task, this, task.getTarget());
            if (world.isClientSide()) {
                clTtickSwingSound(task.getTick(), standEntity);
            }
        }
    }

    @Override
    public EraseEntityHit punchEntity(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
        EraseEntityHit erase = new EraseEntityHit(stand, target, dmgSource).eraseHits(stand, target, stand.barrageHits);
        dmgSource.bypassArmor().bypassMagic();
        return erase;
    }

    public static float getEraseDamage(Entity target, StandEntity stand) {
        float damage = 0;
        if (!(target instanceof LivingEntity) || !PercentDamage()) {
            damage = StandStatFormulas.getBarrageHitDamage(16, stand.getPrecision());
            return damage;
        }
        else {
            LivingEntity entity = (LivingEntity) target;
            if (entity.isAlive()) {
                float size = (float) entity.getBoundingBox().getSize();
                float eraseSpace = Math.max(size > 1.09 ? 1 - (size / 5) : 1 - (size - 1), 0.05F);
                damage = entity.getMaxHealth() * ((size > 1.5 ? 0.0071875F : 0.0115F) * eraseSpace);
                return damage;
            }
            return damage;
        }
    }

    public static boolean PercentDamage() {
        return RotpTheHandConfig.getCommonConfigInstance(false).PercentDamage.get();
    }

    @Override
    public void standTickRecovery(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        IPunch punch = standEntity.getLastPunch();
        TheHandEntity theHand = (TheHandEntity) standEntity;
        if (punch != null) {
            theHand.somethingWasErased(punch.getType() != ActionTarget.TargetType.EMPTY);
        }
        else {
            theHand.somethingWasErased(false);
        }
        if (theHand.isErasing()) {
            theHand.setErase(false);
        }
        theHand.recoveryCount++;
    }

    @Override
    public int getStandRecoveryTicks(IStandPower standPower, StandEntity standEntity) {
        return 8;
    }

    @Override
    protected boolean isCancelable(IStandPower standPower, StandEntity standEntity, @Nullable StandEntityAction newAction, Phase phase) {
        if (phase == Phase.RECOVERY) {
            return newAction != null && (newAction.canFollowUpBarrage() || newAction instanceof StandEntityLightAttack);
        }
        else {
            return super.isCancelable(standPower, standEntity, newAction, phase);
        }
    }

    @Override
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        super.onTaskStopped(world, standEntity, standPower, task, newAction);
        if (!world.isClientSide()) {
            TheHandEntity theHand = (TheHandEntity) standEntity;
            LivingEntity user = theHand.getUser();
            if (!theHand.targetErased() && theHand.erasedTargets() <= 0 && user != null) {
                LivingEntity entity = standEntity.isManuallyControlled() ? standEntity : user;
                TheHandErase.Teleport(world, user, theHand, entity);
            }
            theHand.attractTarget(false);
            theHand.setErase(false);
        }
    }

    @Override
    public int getHoldDurationMax(IStandPower standPower) {
        if (standPower.getStandManifestation() instanceof StandEntity) {
            StandEntity standEntity = (StandEntity) standPower.getStandManifestation();
            return StandStatFormulas.getBarrageMaxDuration(standEntity.getDurability() * 0.3);
        }
        return 20;
    }

    public static class EraseEntityHit extends BarrageEntityPunch {
        private int barrageHits = 0;
        public EraseEntityHit(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
            super(stand, target, dmgSource);
            this
            .damage(getEraseDamage(target, stand))
            .addFinisher(0.0F)
            .disableBlocking(1.0F)
            .reduceKnockback(target instanceof StandEntity ? 0 : (float) stand.getAttackDamage() * 0.0075F);
        }

        public EraseEntityHit eraseHits(StandEntity stand, Entity target, int hits) {
            this.barrageHits = hits;
            damage(getEraseDamage(target, stand) * hits);
            return this;
        }

        @Override
        public boolean doHit(StandEntityTask task) {
            if (stand.level.isClientSide()) return false;
            if (barrageHits > 0) {
                dmgSource.setBarrageHitsCount(barrageHits);
            }
            boolean hit = super.doHit(task);
            return hit;
        }

        @Override
        protected boolean doAttack(StandEntity stand, Entity target, StandEntityDamageSource dmgSource, float damage) {
            TheHandEntity.eraseHealth(stand, target, damage);
            return super.doAttack(stand, target, dmgSource, damage);
        }

        @Override
        protected void afterAttack(StandEntity stand, Entity target, StandEntityDamageSource dmgSource, StandEntityTask task, boolean hurt, boolean killed) {
            if (!stand.level.isClientSide() && target instanceof LivingEntity && hurt) {
                LivingEntity livingTarget = (LivingEntity) target;
                ItemStack helmet = livingTarget.getItemBySlot(EquipmentSlotType.HEAD);
                ItemStack chestplace = livingTarget.getItemBySlot(EquipmentSlotType.CHEST);
                ItemStack leggings = livingTarget.getItemBySlot(EquipmentSlotType.LEGS);
                ItemStack boots = livingTarget.getItemBySlot(EquipmentSlotType.FEET);
                if (!helmet.isEmpty()) {
                    if (helmet.isDamageableItem() && damageToArmor(helmet, 0.02F) < helmet.getMaxDamage()) {
                        helmet.setDamageValue(damageToArmor(helmet, 0.02F));
                    }
                    else {
                        helmet.shrink(1);
                    }
                }
                if (!chestplace.isEmpty()) {
                    if (chestplace.isDamageableItem() && damageToArmor(chestplace, 0.02F) < chestplace.getMaxDamage()) {
                        chestplace.setDamageValue(damageToArmor(chestplace, 0.02F));
                    }
                    else {
                        chestplace.shrink(1);
                    }
                }
                if (!leggings.isEmpty()) {
                    if (leggings.isDamageableItem() && damageToArmor(leggings, 0.02F) < leggings.getMaxDamage()) {
                        leggings.setDamageValue(damageToArmor(leggings, 0.02F));
                    }
                    else {
                        leggings.shrink(1);
                    }
                }
                if (!boots.isEmpty()) {
                    if (boots.isDamageableItem() && damageToArmor(boots, 0.02F) < boots.getMaxDamage()) {
                        boots.setDamageValue(damageToArmor(boots, 0.02F));
                    }
                    else {
                        boots.shrink(1);
                    }
                }
            }
            super.afterAttack(stand, target, dmgSource, task, hurt, killed);
        }

        public int damageToArmor(ItemStack armor, float multiplier) {
            return (int) (armor.getDamageValue() + (armor.getMaxDamage() * multiplier));
        }
    }

    @Override
    public int getCooldownAdditional(IStandPower power, int ticksHeld) {
        int cooldown = super.getCooldownAdditional(power, ticksHeld * 2);
        return ticksHeld < 5 ? 24 : cooldownFromHoldDuration(cooldown, power, ticksHeld);
    }

    private final LazySupplier<ResourceLocation> altTex =
            new LazySupplier<>(() -> makeIconVariant(this, "_alt"));
    @Override
    public ResourceLocation getIconTexturePath(@Nullable IStandPower power) {
        if (power != null && attract(power)) {
            return altTex.get();
        }
        else {
            return super.getIconTexturePath(power);
        }
    }

    private boolean attract(IStandPower power) {
        if (power.isActive() && power.getStandManifestation() instanceof StandEntity) {
            TheHandEntity theHand = (TheHandEntity) power.getStandManifestation();
            return (power.getUser().isShiftKeyDown() && (theHand.getCurrentTaskAction() != InitStands.THE_HAND_ERASE.get() &&
                    theHand.getCurrentTaskAction() != InitStands.THE_HAND_ERASURE_BARRAGE.get())) || theHand.attractTarget();
        }
        return power.getUser().isShiftKeyDown();
    }



    @Deprecated
    private ResourceLocation altTexPath;
    @Deprecated
    @Override
    public ResourceLocation getTexture(IStandPower power) {
        ResourceLocation resLoc = getRegistryName();
        if (attract(power)) {
            if (altTexPath == null) {
                altTexPath = new ResourceLocation(resLoc.getNamespace(), resLoc.getPath() + "_alt");
            }
            resLoc = altTexPath;
        }
        return resLoc;
    }

    @Deprecated
    @Override
    public Stream<ResourceLocation> getTexLocationstoLoad() {
        ResourceLocation resLoc = getRegistryName();
        return Stream.of(resLoc, new ResourceLocation(resLoc.getNamespace(), resLoc.getPath() + "_alt"));
    }
}
