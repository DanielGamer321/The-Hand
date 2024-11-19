package com.danielgamer321.rotp_th.entity.stand.stands;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.IHasStandPunch;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.ModEntityTypes;
import com.github.standobyte.jojo.util.general.MathUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class TheHandEntity extends StandEntity {
    public static final AttributeModifier ERASE_POWER_DESTRUCTION_BOOST = new AttributeModifier(
            UUID.fromString("84331a3b-73f1-4461-b240-6d688897e3f4"), "Destructive power boost in erasure attacks", 4.34, AttributeModifier.Operation.MULTIPLY_BASE);

    private static final DataParameter<Boolean> IS_ERASING = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> TARGET_ERASED = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);
    
    public TheHandEntity(StandEntityType<TheHandEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(IS_ERASING, false);
        entityData.define(TARGET_ERASED, false);
    }

    public boolean isErasing() {
        return entityData.get(IS_ERASING);
    }

    public void setErase(boolean erase) {
        entityData.set(IS_ERASING, erase);
        updateModifier(getAttribute(Attributes.ATTACK_DAMAGE), ERASE_POWER_DESTRUCTION_BOOST, erase);
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
            return eraseProjectile(punch.target);
        }
        else {
            return super.attackEntity(doAttack, punch, task);
        }
    }

    @Override
    public boolean attackTarget(ActionTarget target, IHasStandPunch punch, StandEntityTask task) {
        if (isErasing()) {
            level.getEntitiesOfClass(Entity.class, getBoundingBox().inflate(getAttributeValue(ForgeMod.REACH_DISTANCE.get())),
                    entity -> entity.isAlive()).forEach(removed -> {
                if (canErase(removed)) {
                    if (this.getLookAngle().dot(removed.getDeltaMovement().reverse().normalize())
                            >= MathHelper.cos((float) (30.0 + MathHelper.clamp(getPrecision(), 0, 16) * 30.0 / 16.0) * MathUtil.DEG_TO_RAD)) {
                        eraseProjectile(removed);
                    }
                }
            });
        }

        return super.attackTarget(target, punch, task);
    }

    private boolean canErase(Entity target) {
        return (target instanceof BoatEntity || target instanceof AbstractMinecartEntity ||
                target instanceof TNTEntity || target instanceof FallingBlockEntity ||
                target instanceof EyeOfEnderEntity || target instanceof ArmorStandEntity ||
                target instanceof ProjectileEntity) && !isTheUserVehicle(target);
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

    private boolean eraseProjectile(Entity target) {
        if (target.getType() != ModEntityTypes.SPACE_RIPPER_STINGY_EYES.get()) {
            eraseProjectile(target, getLookAngle());
            return true;
        }
        return false;
    }

    public void eraseProjectile(Entity target, @Nullable Vector3d eraseVec) {
        target.setDeltaMovement(eraseVec != null ?
                eraseVec.scale(Math.sqrt(target.getDeltaMovement().lengthSqr() / eraseVec.lengthSqr()))
                : target.getDeltaMovement().reverse());
        target.remove();
        somethingWasErased(true);
    }

    @Override
    protected boolean breakBlock(BlockPos blockPos, BlockState blockState, boolean dropLootTableItems, @Nullable List<ItemStack> createdDrops) {
        if (isErasing()) {
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
}
