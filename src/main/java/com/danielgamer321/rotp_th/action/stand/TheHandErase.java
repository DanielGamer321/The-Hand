package com.danielgamer321.rotp_th.action.stand;

import com.danielgamer321.rotp_th.RotpTheHandConfig;
import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityHeavyAttack;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
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
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TheHandErase extends StandEntityHeavyAttack {
    public static final StandPose ERASE_POSE = new StandPose("ERASE");
    private static final double TP_RANGE = 12;
    private static final double PULL_TRACKING_RANGE = 12;
    public TheHandErase(Builder builder) {
        super(builder);
    }

    @Override
    protected Action<IStandPower> replaceAction(IStandPower power, ActionTarget target) {
        return this;
    }

    @Override
    public void onTaskSet(World world, StandEntity standEntity, IStandPower standPower, Phase phase, StandEntityTask task, int ticks) {
        if (!world.isClientSide()) {
            TheHandEntity thehand = (TheHandEntity) standEntity;
            thehand.setErase(true);
        }
    }

    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        super.standPerform(world, standEntity, userPower, task);
        LivingEntity user = userPower.getUser();
        LivingEntity entity = standEntity.isManuallyControlled() ? standEntity : user;
        if (task.getTarget().getType() == ActionTarget.TargetType.EMPTY){
            if (user.isShiftKeyDown()) {
                if (userPower.getResolveLevel() >= 3) {
                    standEntity.addFinisherMeter(0.45F, 0);
                }
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
            else {
                RayTraceResult rayTrace = JojoModUtil.rayTrace(entity.getEyePosition(1.0F), entity.getLookAngle(), TP_RANGE,
                        world, entity, e -> !(e.is(standEntity) || e.is(user)), 0, 0);
                Vector3d tpPos = rayTrace.getLocation();
                entity.teleportTo(tpPos.x, tpPos.y, tpPos.z);
            }
        }
    }

    @Override
    public StandEntityPunch punchEntity(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
        dmgSource.bypassArmor().bypassMagic();
        return super.punchEntity(stand, target, dmgSource)
                .damage(getEraseDamage(target, stand))
                .addKnockback(0);
    }

    private static float getEraseDamage(Entity target, StandEntity stand) {
        float damage = 0;
        double strength = stand.getAttackDamage();
        if (!(target instanceof LivingEntity) || !PercentDamage()) {
            damage = StandStatFormulas.getHeavyAttackDamage(strength);
            return damage;
        }
        else {
            LivingEntity entity = (LivingEntity) target;
            if (entity.isAlive() && entity.getMaxHealth() >= 20) {
                damage = entity.getMaxHealth() * 0.8F;
                return damage;
            }
            else if (entity.getMaxHealth() < 20) {
                damage = StandStatFormulas.getHeavyAttackDamage(strength);
                return damage;
            }
            return damage;
        }
    }

    public static boolean PercentDamage() {
        return RotpTheHandConfig.getCommonConfigInstance(false).PercentDamage.get();
    }

    @Override
    public int getStandWindupTicks(IStandPower standPower, StandEntity standEntity) {
        return StandStatFormulas.getHeavyAttackWindup(standEntity.getAttackSpeed() * 1.75, standEntity.getFinisherMeter());
    }

    @Override
    protected boolean standKeepsTarget(ActionTarget target) {
        return !(target.getType() == ActionTarget.TargetType.EMPTY);
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
