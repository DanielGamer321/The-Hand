package com.danielgamer321.rotp_th.entity.stand.stands;

import com.danielgamer321.rotp_th.action.stand.TheHandErasureBarrage;
import com.danielgamer321.rotp_th.capability.entity.EntityUtilCapProvider;
import com.danielgamer321.rotp_th.init.InitEffects;
import com.danielgamer321.rotp_th.init.InitStands;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.IHasStandPunch;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
import com.github.standobyte.jojo.entity.RoadRollerEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.entity.stand.StandStatFormulas;
import com.github.standobyte.jojo.init.ModEntityTypes;
import com.github.standobyte.jojo.util.general.MathUtil;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import com.github.standobyte.jojo.util.mod.JojoModUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;


import static com.danielgamer321.rotp_th.action.stand.TheHandErase.getEraseDamage;
import static com.danielgamer321.rotp_th.util.AddonInteractionUtil.isAquaNecklace;

public class TheHandEntity extends StandEntity {
    private static final DataParameter<Boolean> IS_ERASING = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ERASED_TARGETS = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> TARGET_ERASED = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ATTRACT_TARGET = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);
    public int recoveryCount = 0;
    public float preparation = 0;
    
    public TheHandEntity(StandEntityType<TheHandEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(IS_ERASING, false);
        entityData.define(ERASED_TARGETS, 0);
        entityData.define(TARGET_ERASED, false);
        entityData.define(ATTRACT_TARGET, false);
    }

    public boolean isErasing() {
        return entityData.get(IS_ERASING);
    }

    public void setErase(boolean erase) {
        entityData.set(IS_ERASING, erase);
    }

    public boolean attractTarget() {
        return entityData.get(ATTRACT_TARGET);
    }

    public void attractTarget(boolean attract) {
        entityData.set(ATTRACT_TARGET, attract);
    }

    public int erasedTargets() {
        return entityData.get(ERASED_TARGETS);
    }

    public void somethingWasErased(int erase) {
        entityData.set(ERASED_TARGETS, erase);
    }

    public boolean targetErased() {
        return entityData.get(TARGET_ERASED);
    }

    public void somethingWasErased(boolean erase) {
        entityData.set(TARGET_ERASED, erase);
    }

    @Override
    public boolean attackEntity(Supplier<Boolean> doAttack, StandEntityPunch punch, StandEntityTask task) {
        return attackOrErase(doAttack, punch, task);
    }

    private boolean attackOrErase(Supplier<Boolean> doAttack, StandEntityPunch punch, StandEntityTask task) {
        if (isErasing() && canErase(punch.target)) {
            return eraseEntity(punch.target);
        }
        else {
            return super.attackEntity(doAttack, punch, task);
        }
    }

    @Override
    public boolean attackTarget(ActionTarget target, IHasStandPunch punch, StandEntityTask task) {
        if (isErasing()) {
            somethingWasErased(0);
            getEntitiesToRemove().forEach(removed -> eraseEntity(removed));
        }

        return super.attackTarget(target, punch, task);
    }

    public List<Entity> getEntitiesToRemove() {
        return level.getEntitiesOfClass(Entity.class, getBoundingBox().inflate(getAttributeValue(ForgeMod.REACH_DISTANCE.get())),
                entity -> entity.isAlive() && canErase(entity) && (getLookAngle().dot(entity.getDeltaMovement().reverse().normalize())
                        >= MathHelper.cos((float) (30.0 + MathHelper.clamp(getPrecision(), 0, 16) * 30.0 / 16.0) * MathUtil.DEG_TO_RAD) ||
                        getLookAngle().dot(entity.getDeltaMovement().reverse().normalize()) < MathHelper.cos((float)
                                (30.0 + MathHelper.clamp(getPrecision(), 0, 16) * 30.0 / 16.0) * MathUtil.DEG_TO_RAD)));
    }

    private boolean canErase(Entity target) {
        return (target instanceof BoatEntity || target instanceof AbstractMinecartEntity ||
                target instanceof TNTEntity || target instanceof FallingBlockEntity ||
                target instanceof EyeOfEnderEntity || target instanceof ArmorStandEntity ||
                target instanceof ProjectileEntity || target instanceof EnderCrystalEntity ||
                target instanceof ItemFrameEntity || (isAquaNecklace(target) &&
                !target.isPickable())) && !isTheUserVehicle(target) && !target.isInvulnerable();
    }

    private boolean isTheUserVehicle(Entity target) {
        LivingEntity passenger = null;
        if (target.isVehicle()) {
            for(Entity passengers : target.getPassengers()) {
                if (passengers instanceof LivingEntity) {
                    passenger = (LivingEntity) passengers;
                }
            }
        }
        return passenger != null && passenger == getUser();
    }

    private boolean eraseEntity(Entity target) {
        if (isAquaNecklace(target)) {
            float damageAmount = this.getCurrentTaskAction() == InitStands.THE_HAND_ERASE.get() ?
                    getEraseDamage(target, this) : TheHandErasureBarrage.getEraseDamage(target, this) * this.barrageHits;
            LivingEntity user = ((StandEntity)target).getUser();
            StandEntityDamageSource damage = new StandEntityDamageSource("stand", this, getUserPower() != null ? getUserPower() : null);
            damage.bypassArmor().isBypassMagic();
            DamageUtil.hurtThroughInvulTicks(user != null ? user : target, damage.setKnockbackReduction(0), damageAmount);
            eraseHealth(this, target, damageAmount);
            somethingWasErased(1);
            return true;
        }
        else if (target.getType() != ModEntityTypes.SPACE_RIPPER_STINGY_EYES.get()) {
            eraseProjectile(target, getLookAngle());
            somethingWasErased(1);
            return true;
        }
        return false;
    }

    public static void eraseHealth(StandEntity stand, Entity target, float damage) {
        if (!stand.level.isClientSide()) {
            float erased = target.getCapability(EntityUtilCapProvider.CAPABILITY).map(cap -> cap.getErased()).orElse(0F);
            float dam;
            if (target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;
                if (livingTarget instanceof StandEntity ) {
                    StandEntity standTarget = (StandEntity) livingTarget;
                    livingTarget = standTarget.getUser() != null ? standTarget.getUser() : livingTarget;
                }
                damage = damage < livingTarget.getAbsorptionAmount() ? 0 : damage - livingTarget.getAbsorptionAmount();
                dam = livingTarget.hasEffect(InitEffects.ERASED.get()) ? damage + erased : damage;
                livingTarget.getCapability(EntityUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setErased(dam));
                if (damage > 0) {
                    livingTarget.addEffect(new EffectInstance(InitEffects.ERASED.get(), 200, 0, false, false, true));
                }
            }
            else if (target instanceof RoadRollerEntity) {
                dam = damage + erased;
                if (((RoadRollerEntity) target).getMaxHealth() < erased + dam) {
                    target.remove();
                }
                target.getCapability(EntityUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setErased(dam));
            }
        }
    }

    public void eraseProjectile(Entity target, @Nullable Vector3d eraseVec) {
        target.setDeltaMovement(eraseVec != null ?
                eraseVec.scale(Math.sqrt(target.getDeltaMovement().lengthSqr() / eraseVec.lengthSqr()))
                : target.getDeltaMovement().reverse());
        target.remove();
    }

    @Override
    protected boolean breakBlock(BlockPos blockPos, BlockState blockState, boolean dropLootTableItems, @Nullable List<ItemStack> createdDrops) {
        if (isErasing()) {
            if (level.isClientSide() || !JojoModUtil.canEntityDestroy((ServerWorld) level, blockPos, blockState, this) || blockState.isAir(level, blockPos)) {
                return false;
            }

            if (level.isClientSide() || blockState.isAir(level, blockPos)) {
                return false;
            }

            if (canBreakBlock(blockPos, blockState)) {
                boolean dropItem = false;
                if (level.destroyBlock(blockPos, dropItem, this)) {
                    blockState.getBlock().destroy(level, blockPos, blockState);
                    return true;
                }
            }
            return false;
        }
        else {
            return super.breakBlock(blockPos, blockState, dropLootTableItems, createdDrops);
        }
    }

    @Override
    public boolean canBreakBlock(float blockHardness, int blockHarvestLevel) {
        if (isErasing()) {
            return StandStatFormulas.isBlockBreakable(20, blockHardness, blockHarvestLevel);
        }
        return super.canBreakBlock(blockHardness, blockHarvestLevel);
    }
}
