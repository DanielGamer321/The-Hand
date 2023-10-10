package com.danielgamer321.rotp_th.entity.stand.stands;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.CrazyDiamondRestoreTerrain;
import com.github.standobyte.jojo.action.stand.IHasStandPunch;
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

public class TheHandEntity extends StandEntity {
    public static final AttributeModifier ERASE_POWER_DESTRUCTION_BOOST = new AttributeModifier(
            UUID.fromString("84331a3b-73f1-4461-b240-6d688897e3f4"), "Destructive power boost in erasure attacks", 0.35, AttributeModifier.Operation.MULTIPLY_BASE);

    private static final AttributeModifier ERASURE_BARRAGE_SPEED_DECREASE = new AttributeModifier(
            UUID.fromString("c3e4ddb0-daa9-4cbb-acb9-dbc7eecad3f1"), "Speed decrease in erasure barrage", -0.5, AttributeModifier.Operation.MULTIPLY_BASE);

    private static final DataParameter<Boolean> HAS_HEAD = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);

    private static final DataParameter<Boolean> HAS_ERASE = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);

    private static final DataParameter<Boolean> HAS_ERASURE_BARRAGE = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);

    private static final DataParameter<Boolean> HAS_HEAD2 = EntityDataManager.defineId(TheHandEntity.class, DataSerializers.BOOLEAN);
    
    public TheHandEntity(StandEntityType<TheHandEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HAS_HEAD, true);
        entityData.define(HAS_ERASE, false);
        entityData.define(HAS_ERASURE_BARRAGE, false);
        entityData.define(HAS_HEAD2, false);
    }

    public boolean hasErase() {
        return entityData.get(HAS_ERASE);
    }

    public void setErase(boolean erase) {
        entityData.set(HAS_ERASE, erase);
        updateModifier(getAttribute(Attributes.ATTACK_DAMAGE), ERASE_POWER_DESTRUCTION_BOOST, erase);
    }

    public boolean hasErasureBarrage() {
        return entityData.get(HAS_ERASURE_BARRAGE);
    }

    public void setErasureBarrage(boolean barrage) {
        entityData.set(HAS_ERASURE_BARRAGE, barrage);
        updateModifier(getAttribute(Attributes.ATTACK_SPEED), ERASURE_BARRAGE_SPEED_DECREASE, barrage);
    }

    public boolean hashead() {
        return entityData.get(HAS_HEAD);
    }

    public void sethead(boolean head) {
        entityData.set(HAS_HEAD, head);
    }

    public boolean hashead2() {
        return entityData.get(HAS_HEAD2);
    }

    public void sethead2(boolean head2) {
        entityData.set(HAS_HEAD2, head2);
    }

    @Override
    public boolean attackTarget(ActionTarget target, IHasStandPunch punch, StandEntityTask task) {
        if (this.hasErasureBarrage() == true) {
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

    public static void eraseProjectile(Entity projectile, @Nullable Vector3d deflectVec) {
        projectile.setDeltaMovement(deflectVec != null ?
                deflectVec.scale(Math.sqrt(projectile.getDeltaMovement().lengthSqr() / deflectVec.lengthSqr()))
                : projectile.getDeltaMovement().reverse());
        projectile.remove();
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
