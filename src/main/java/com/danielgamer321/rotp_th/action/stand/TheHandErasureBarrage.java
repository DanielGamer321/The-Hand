package com.danielgamer321.rotp_th.action.stand;

import com.danielgamer321.rotp_th.RotpTheHandConfig;
import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityMeleeBarrage;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.entity.stand.StandStatFormulas;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public class TheHandErasureBarrage extends StandEntityMeleeBarrage {
    public static final StandPose ERASURE_BARRAGE_POSE = new StandPose("ERASURE_BARRAGE");
    public TheHandErasureBarrage(Builder builder) {
        super(builder);
    }

    @Override
    public void onTaskSet(World world, StandEntity standEntity, IStandPower standPower, Phase phase, StandEntityTask task, int ticks) {
        super.onTaskSet(world, standEntity, standPower, phase, task, ticks);
        if (!world.isClientSide()) {
            TheHandEntity thehand = (TheHandEntity) standEntity;
            thehand.setErase(true);
            thehand.somethingWasErased(true);
        }
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        super.standTickPerform(world, standEntity, userPower, task);
        TheHandEntity theHand = (TheHandEntity) userPower.getStandManifestation();
        List<ProjectileEntity> check = world.getEntitiesOfClass(ProjectileEntity.class, theHand.getBoundingBox().inflate(theHand.getAttributeValue(ForgeMod.REACH_DISTANCE.get())),
                entity -> entity.isAlive() && !entity.isPickable());
        if (task.getTarget().getType() == ActionTarget.TargetType.EMPTY && check.isEmpty()) {
            theHand.somethingWasErased(false);
        }
        else {
            theHand.somethingWasErased(true);
        }
    }

    @Override
    public EraseEntityHit punchEntity(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
        EraseEntityHit erase = new EraseEntityHit(stand, target, dmgSource).eraseHits(stand, target, stand.barrageHits);
        dmgSource.bypassArmor().bypassMagic();
        return erase;
    }

    private static float getEraseDamage(Entity target, StandEntity stand) {
        float damage = 0;
        if (!(target instanceof LivingEntity) || !PercentDamage()) {
            damage = StandStatFormulas.getBarrageHitDamage(16, stand.getPrecision());
            return damage;
        }
        else {
            LivingEntity entity = (LivingEntity) target;
            if (entity.isAlive()) {
                float size = (entity.getBbHeight() + entity.getBbWidth()) / 2.4F;
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
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        super.onTaskStopped(world, standEntity, standPower, task, newAction);
        if (!world.isClientSide()) {
            TheHandEntity thehand = (TheHandEntity) standEntity;
            LivingEntity user = thehand.getUser();
            if (!thehand.targetErased() && user != null) {
                LivingEntity entity = standEntity.isManuallyControlled() ? standEntity : user;
                TheHandErase.Teleport(world, user, standEntity, entity);
            }
            thehand.setErase(false);
        }
    }

    @Override
    public int getHoldDurationMax(IStandPower standPower) {
        return StandStatFormulas.getBarrageMaxDuration(((StandEntity) standPower.getStandManifestation()).getDurability()*0.3);
    }

    public static class EraseEntityHit extends BarrageEntityPunch {
        private int barrageHits = 0;
        public EraseEntityHit(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
            super(stand, target, dmgSource);
            this
            .damage(getEraseDamage(target, stand))
            .addFinisher(-0.005F)
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
//            target.setDeltaMovement(target.getDeltaMovement().multiply(1, 0, 1));
            return hit;
        }

        @Override
        protected void afterAttack(StandEntity stand, Entity target, StandEntityDamageSource dmgSource, StandEntityTask task, boolean hurt, boolean killed) {
            if (hurt && dmgSource.getBarrageHitsCount() > 0) {
                addFinisher *= dmgSource.getBarrageHitsCount();
            }
            super.afterAttack(stand, target, dmgSource, task, hurt, killed);
        }
    }

    @Override
    public int getCooldownAdditional(IStandPower power, int ticksHeld) {
        int cooldown = super.getCooldownAdditional(power, ticksHeld*2);
        return ticksHeld < 5 ? 24 : cooldownFromHoldDuration(cooldown, power, ticksHeld);
    }
}
