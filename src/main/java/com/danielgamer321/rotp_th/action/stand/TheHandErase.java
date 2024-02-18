package com.danielgamer321.rotp_th.action.stand;

import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.danielgamer321.rotp_th.init.InitSounds;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.ActionTarget.TargetType;
import com.github.standobyte.jojo.action.stand.IHasStandPunch;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityActionModifier;
import com.github.standobyte.jojo.action.stand.StandEntityHeavyAttack;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.entity.stand.StandStatFormulas;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class TheHandErase extends StandEntityAction implements IHasStandPunch {
    public static final StandPose ERASE_POSE = new StandPose("ERASE");
    private static final double TP_RANGE = 12;
    private static final double PULL_TRACKING_RANGE = 12;
    private final Supplier<StandEntityActionModifier> recoveryAction;
    private final Supplier<SoundEvent> swingSound;

    public TheHandErase(TheHandErase.Builder builder) {
        super(builder);
        this.recoveryAction = builder.recoveryAction;
        this.swingSound = builder.swingSound;
    }

    public com.github.standobyte.jojo.action.stand.punch.StandMissedPunch yaa;
    
    @Nullable
    protected StandEntityActionModifier getRecoveryFollowup(IStandPower standPower, StandEntity standEntity) {
        return recoveryAction.get();
    }
    
    @Override
    protected ActionConditionResult checkStandConditions(StandEntity stand, IStandPower power, ActionTarget target) {
        return !stand.canAttackMelee() ? ActionConditionResult.NEGATIVE : super.checkStandConditions(stand, power, target);
    }
    
    public void onClick(World world, LivingEntity user, IStandPower power) {
        super.onClick(world, user, power);
        if (power.isActive() && power.getStandManifestation() instanceof StandEntity) {
            ((StandEntity) power.getStandManifestation()).setHeavyPunchFinisher();
        }
    }

    @Override
    public void onTaskSet(World world, StandEntity standEntity, IStandPower standPower, Phase phase, StandEntityTask task, int ticks) {
        standEntity.alternateHands();
        if (!world.isClientSide()) {
            TheHandEntity thehand = (TheHandEntity) standEntity;
            thehand.setErase(true);
        }
    }
    
    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        standEntity.punch(task, this, task.getTarget());
        LivingEntity user = userPower.getUser();
        LivingEntity entity = standEntity.isManuallyControlled() ? standEntity : user;
        if (task.getTarget().getType() == TargetType.EMPTY){
            if (user.isShiftKeyDown()) {
                if (userPower.getResolveLevel() >= 3) {
                    standEntity.addFinisherMeter(0.45F, 0);
                if (entity.isControlledByLocalInstance()) {
                    RayTraceResult rayTrace = JojoModUtil.rayTrace(entity.getEyePosition(1.0F), entity.getLookAngle(), PULL_TRACKING_RANGE,
                            world, entity, e -> !(e.is(standEntity) || e.is(user)), 0, 0);
                    if (rayTrace.getType() == RayTraceResult.Type.ENTITY) {
                        Entity targetEntity = ((EntityRayTraceResult) rayTrace).getEntity();
                        Vector3d tpDest = entity.getEyePosition(1.0F).add(entity.getLookAngle().scale(entity.getBbWidth()));
                        Vector3d tpVec = tpDest.subtract(targetEntity.position());
                        if (tpVec.lengthSqr() > TP_RANGE * TP_RANGE) {
                            tpVec = tpVec.normalize().scale(TP_RANGE);
                        }
                        Vector3d tpPos = targetEntity.position().add(tpVec);
                        targetEntity.teleportTo(tpPos.x, tpPos.y, tpPos.z);
                        }
                    }
                }
            }
            else {
                RayTraceResult rayTrace = JojoModUtil.rayTrace(entity.getEyePosition(1.0F), entity.getLookAngle(), TP_RANGE,
                        world, entity, e -> !(e.is(standEntity) || e.is(user)), 0, 0);
                Vector3d tpPos = rayTrace.getLocation();
                entity.teleportTo(tpPos.x, tpPos.y, tpPos.z);
            }
        }
    }

    protected Vector3d getEntityTargetTeleportPos(Entity user, Entity target) {
        double distance = target.getBbWidth() + user.getBbWidth();
        return user.distanceToSqr(target) > distance * distance ? target.position().subtract(user.getLookAngle().scale(distance)) : user.position();
    }
    
    @Override
    public StandEntityPunch punchEntity(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
        StandEntityPunch punch = IHasStandPunch.super.punchEntity(stand, target, dmgSource);
        dmgSource.bypassArmor().bypassMagic();
        punch.damage(getEraseDamage(target, stand));
        punch.addKnockback(0);
        return punch;
    }

    private static float getEraseDamage(Entity target, StandEntity stand) {
        float damage = 0;
        if (!(target instanceof LivingEntity)) {
            damage = StandStatFormulas.getBarrageHitDamage(stand.getAttackDamage(), stand.getPrecision());
            return damage;
        }
        else {
            LivingEntity entity = (LivingEntity) target;
            if (entity.isAlive() && entity.getMaxHealth() >= 20) {
                damage = entity.getMaxHealth() * 0.8F;
                return damage;
            }
            else if (entity.getMaxHealth() < 20) {
                damage = StandStatFormulas.getBarrageHitDamage(stand.getAttackDamage(), stand.getPrecision());
                return damage;
            }
            return damage;
        }
    }

    @Override
    public SoundEvent getPunchSwingSound() {
        return swingSound.get();
    }
    
    @Override
    public void standTickWindup(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        IHasStandPunch.playPunchSwingSound(task, Phase.WINDUP, 3, this, standEntity);
    }
    
    @Override
    public void clPlayPunchSwingSound(StandEntity standEntity, SoundEvent sound) {
        standEntity.playSound(sound, 1.0F, 0.65F + standEntity.getRandom().nextFloat() * 0.2F, ClientUtil.getClientPlayer());
    }
    
    @Override
    public int getStandWindupTicks(IStandPower standPower, StandEntity standEntity) {
        return StandStatFormulas.getHeavyAttackWindup(standEntity.getAttackSpeed(), standEntity.getFinisherMeter());
    }

    @Override
    public int getStandRecoveryTicks(IStandPower standPower, StandEntity standEntity) {
        return StandStatFormulas.getHeavyAttackRecovery(standEntity.getAttackSpeed());
    }
    
    @Override
    protected boolean standKeepsTarget(ActionTarget target) {
        return !(target.getType() == TargetType.EMPTY);
    }
    
    @Override
    public boolean noFinisherDecay() {
        return true;
    }


    
    public static class Builder extends AbstractBuilder<TheHandErase.Builder> {
        private Supplier<StandEntityActionModifier> recoveryAction = () -> null;
        private Supplier<SoundEvent> swingSound = InitSounds.THE_HAND_ERASE;
        
        public Builder() {
            standPose(TheHandErase.ERASE_POSE).staminaCost(150F)
            .standOffsetFront();
        }
        
        public Builder swingSound(Supplier<SoundEvent> swingSound) {
            this.swingSound = swingSound != null ? swingSound : () -> null;
            return getThis();
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }

    public static class EraseInstance extends StandEntityHeavyAttack.HeavyPunchInstance {

        public EraseInstance(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
            super(stand, target, dmgSource);
        }

        @Override
        protected void afterAttack(StandEntity stand, Entity target, StandEntityDamageSource dmgSource, StandEntityTask task, boolean hurt, boolean killed) {
            if (!stand.level.isClientSide() && target instanceof StandEntity && hurt && !killed) {
                StandEntity standTarget = (StandEntity) target;
                if (standTarget.getCurrentTask().isPresent() && standTarget.getCurrentTaskAction().stopOnHeavyAttack(this)) {
                    standTarget.stopTaskWithRecovery();
                }
            }
            super.afterAttack(stand, target, dmgSource, task, hurt, killed);
        }
    }

    @Override
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        if (!world.isClientSide()) {
            TheHandEntity thehand = (TheHandEntity) standEntity;
            thehand.setErase(false);
        }
    }

    @Override
    public int getHoldDurationToFire(IStandPower power) {
        return shortedHoldDuration(power, super.getHoldDurationToFire(power));
    }

    private int shortedHoldDuration(IStandPower power, int ticks) {
        return ticks > 0 && power.getResolveLevel() >= 4 ? 10 : ticks;
    }

    @Override
    public boolean cancelHeldOnGettingAttacked(IStandPower power, DamageSource dmgSource, float dmgAmount) {
        return true;
    }
}
