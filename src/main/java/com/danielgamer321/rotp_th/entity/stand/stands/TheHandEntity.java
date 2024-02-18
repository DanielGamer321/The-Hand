package com.danielgamer321.rotp_th.entity.stand.stands;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.CrazyDiamondRestoreTerrain;
import com.github.standobyte.jojo.action.stand.IHasStandPunch;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
import com.github.standobyte.jojo.entity.damaging.projectile.ModdedProjectileEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.ModEntityTypes;
import com.github.standobyte.jojo.util.general.MathUtil;
import com.github.standobyte.jojo.util.mod.JojoModUtil;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class TheHandEntity extends StandEntity {
    public static final AttributeModifier ERASE_POWER_DESTRUCTION_BOOST = new AttributeModifier(
            UUID.fromString("84331a3b-73f1-4461-b240-6d688897e3f4"), "Destructive power boost in erasure attacks", 0.35, AttributeModifier.Operation.MULTIPLY_BASE);

     private static final DataParameter<Boolean> HAS_ERASE = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);
    
    public TheHandEntity(StandEntityType<TheHandEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HAS_ERASE, false);
    }

    public boolean hasErase() {
        return entityData.get(HAS_ERASE);
    }

    public void setErase(boolean erase) {
        entityData.set(HAS_ERASE, erase);
        updateModifier(getAttribute(Attributes.ATTACK_DAMAGE), ERASE_POWER_DESTRUCTION_BOOST, erase);
    }

    @Override
    public boolean attackEntity(Supplier<Boolean> doAttack, StandEntityPunch punch, StandEntityTask task) {
        return attackOrErase(doAttack, punch, task);
    }

    private boolean attackOrErase(Supplier<Boolean> doAttack, StandEntityPunch punch, StandEntityTask task) {
        if (hasErase() && punch.target instanceof ProjectileEntity) {
            return eraseProjectile(punch.target);
        }
        else {
            return super.attackEntity(doAttack, punch, task);
        }
    }

    @Override
    public boolean attackTarget(ActionTarget target, IHasStandPunch punch, StandEntityTask task) {
        if (hasErase()) {
            level.getEntitiesOfClass(ProjectileEntity.class, getBoundingBox().inflate(getAttributeValue(ForgeMod.REACH_DISTANCE.get())),
                    entity -> entity.isAlive() && !entity.isPickable()).forEach(projectile -> {
                if (this.getLookAngle().dot(projectile.getDeltaMovement().reverse().normalize())
                        >= MathHelper.cos((float) (30.0 + MathHelper.clamp(getPrecision(), 0, 16) * 30.0 / 16.0) * MathUtil.DEG_TO_RAD)) {
                    eraseProjectile(projectile);
                }
            });
        }

        return super.attackTarget(target, punch, task);
    }

    private boolean eraseProjectile(Entity projectile) {
        if (projectile.getType() != ModEntityTypes.SPACE_RIPPER_STINGY_EYES.get()) {
            eraseProjectile(projectile, getLookAngle());
            return true;
        }
        return false;
    }

    public static void eraseProjectile(Entity projectile, @Nullable Vector3d eraseVec) {
        projectile.setDeltaMovement(eraseVec != null ?
                eraseVec.scale(Math.sqrt(projectile.getDeltaMovement().lengthSqr() / eraseVec.lengthSqr()))
                : projectile.getDeltaMovement().reverse());
        projectile.remove();
        if (projectile instanceof ModdedProjectileEntity) {
            ((ModdedProjectileEntity) projectile).remove();
        }
    }

    protected boolean breakBlock(BlockPos blockPos, BlockState blockState, boolean dropLootTableItems, @Nullable List<ItemStack> createdDrops) {
        if (level.isClientSide() || !JojoModUtil.canEntityDestroy((ServerWorld) level, blockPos, blockState, this)) {
            return false;
        }

        if (canBreakBlock(blockPos, blockState)) {
            LivingEntity user = getUser();
            PlayerEntity playerUser = user instanceof PlayerEntity ? (PlayerEntity) user : null;
            if (!this.hasErase() == true) {
                boolean dropItem = dropLootTableItems;
                if (playerUser != null) {
                    blockState.getBlock().playerWillDestroy(level, blockPos, blockState, playerUser);
                    dropItem &= !playerUser.abilities.instabuild;
                }
                if (!dropItem) {
                    CrazyDiamondRestoreTerrain.rememberBrokenBlock(level, blockPos, blockState,
                            Optional.ofNullable(level.getBlockEntity(blockPos)),
                            createdDrops != null ? createdDrops : Collections.emptyList());
                }
                if (level.destroyBlock(blockPos, dropItem, this)) {
                    blockState.getBlock().destroy(level, blockPos, blockState);
                    return true;
                }
                else {
                    SoundType soundType = blockState.getSoundType(level, blockPos, this);
                    level.playSound(null, blockPos, soundType.getHitSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 8.0F, soundType.getPitch() * 0.5F);
                }
            }
            else {
                boolean dropItem = false;
                if (level.destroyBlock(blockPos, dropItem, this)) {
                    blockState.getBlock().destroy(level, blockPos, blockState);
                    return true;
                }
            }
        }
        return false;
    }
}
