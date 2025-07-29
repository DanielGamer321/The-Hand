package com.danielgamer321.rotp_th.action.stand;

import com.danielgamer321.rotp_th.RotpTheHandConfig;
import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.danielgamer321.rotp_th.init.InitEffects;
import com.danielgamer321.rotp_th.init.InitStands;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.IHasStandPunch;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityHeavyAttack;
import com.github.standobyte.jojo.action.stand.punch.IPunch;
import com.github.standobyte.jojo.action.stand.punch.StandBlockPunch;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.entity.stand.StandStatFormulas;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.general.LazySupplier;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import com.github.standobyte.jojo.util.mod.JojoModUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TheHandErase extends StandEntityHeavyAttack implements IHasStandPunch {
    public static final StandPose ERASE_POSE = new StandPose("erase");
    private static final double TP_RANGE_OR_PULL_TRACKING_RANGE = 12;
    public TheHandErase(Builder builder) {
        super(builder);
    }

    @Override
    protected Action<IStandPower> replaceAction(IStandPower power, ActionTarget target) {
        return this;
    }

    @Override
    public void onTaskSet(World world, StandEntity standEntity, IStandPower standPower, Phase phase, StandEntityTask task, int ticks) {
        TheHandEntity theHand = (TheHandEntity) standEntity;
        if (standEntity.getUser() != null) {
            theHand.attractTarget(standEntity.getUser().isShiftKeyDown());
        }
        if (!world.isClientSide()) {
            theHand.setErase(true);
            theHand.somethingWasErased(true);
        }
        theHand.recoveryCount = 0;
    }

    @Override
    public StandEntityPunch punchEntity(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
        dmgSource.bypassArmor().bypassMagic();
        return new HeavyPunchInstance(stand, target, dmgSource)
                .damage(getEraseDamage(target))
                .addKnockback(0)
                .reduceKnockback(target instanceof StandEntity ? 0 : (float) stand.getAttackDamage() * 0.0075F)
                .disableBlocking(1.0F)
                .setStandInvulTime(10)
                .impactSound(null);
    }

    public static float getEraseDamage(Entity target) {
        float damage = 0;
        if (!(target instanceof LivingEntity) || !PercentDamage()) {
            damage = StandStatFormulas.getHeavyAttackDamage(16);
            return damage;
        }
        else {
            LivingEntity entity = (LivingEntity) target;
            if (entity.isAlive()) {
                float size = (float) entity.getBoundingBox().getSize();
                float eraseSpace = Math.max(size > 1.09 ? 1 - (size / 5) : 1 - (size - 1), 0.05F);
                damage = entity.getMaxHealth() * ((size > 1.5 ? 0.5F : 0.8F) * eraseSpace);
                return damage;
            }
            return damage;
        }
    }

    public static boolean PercentDamage() {
        return RotpTheHandConfig.getCommonConfigInstance(false).PercentDamage.get();
    }

    @Override
    public StandBlockPunch punchBlock(StandEntity stand, BlockPos pos, BlockState state, Direction face) {
        return new HeavyPunchBlockInstance(stand, pos, state, face)
                .impactSound(null);
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
        theHand.recoveryCount++;
    }

    @Override
    public int getStandWindupTicks(IStandPower standPower, StandEntity standEntity) {
        return StandStatFormulas.getHeavyAttackWindup(standEntity.getAttackSpeed() * 2.5, standEntity.getFinisherMeter());
    }

    @Override
    public int getStandRecoveryTicks(IStandPower standPower, StandEntity standEntity) {
        return 8;
    }

    @Override
    protected boolean standKeepsTarget(ActionTarget target) {
        return !(target.getType() == ActionTarget.TargetType.EMPTY);
    }

    @Override
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        if (!world.isClientSide()) {
            TheHandEntity theHand = (TheHandEntity) standEntity;
            LivingEntity user = theHand.getUser();
            if (!theHand.targetErased() && theHand.erasedTargets() <= 0 && user != null) {
                LivingEntity entity = standEntity.isManuallyControlled() ? standEntity : user;
                Teleport(world, user, theHand, entity);
            }
            theHand.attractTarget(false);
            theHand.setErase(false);
        }
    }

    public static void Teleport(World world, LivingEntity user, TheHandEntity theHand, LivingEntity entity) {
        RayTraceResult rayTrace = JojoModUtil.rayTrace(entity.getEyePosition(1.0F), entity.getLookAngle(), TP_RANGE_OR_PULL_TRACKING_RANGE,
                world, entity, e -> !(e.is(theHand) || e.is(user)), 0, 0);
        if (theHand.attractTarget()) {
            if (entity.isControlledByLocalInstance()) {
                if (rayTrace.getType() == RayTraceResult.Type.ENTITY) {
                    Entity targetEntity = ((EntityRayTraceResult) rayTrace).getEntity();
                    Vector3d tpDest = entity.getEyePosition(1.0F).add(entity.getLookAngle().scale(entity.getBbWidth()));
                    Vector3d tpVec = tpDest.subtract(targetEntity.position());
                    if (tpVec.lengthSqr() > TP_RANGE_OR_PULL_TRACKING_RANGE * TP_RANGE_OR_PULL_TRACKING_RANGE) {
                        tpVec = tpVec.normalize().scale(TP_RANGE_OR_PULL_TRACKING_RANGE);
                    }
                    Entity attractedEntity = targetEntity;
                    if (user.isShiftKeyDown() && theHand.recoveryCount == 8) {
                        if (targetEntity instanceof LivingEntity) {
                            LivingEntity livingTarget = ((LivingEntity) targetEntity);
                            ItemStack main = livingTarget.getItemBySlot(EquipmentSlotType.MAINHAND);
                            ItemStack off = livingTarget.getItemBySlot(EquipmentSlotType.OFFHAND);
                            if (!main.isEmpty() || !off.isEmpty()) {
                                ItemStack itemToAttract = main.isEmpty() ? off : main;
                                ItemEntity item = new ItemEntity(world, targetEntity.getX(), targetEntity.getEyeY() - (double)0.4F, targetEntity.getZ(), itemToAttract.copy());
                                item.getItem().setCount(1);
                                itemToAttract.shrink(1);
                                item.setPickUpDelay(40);
                                world.addFreshEntity(item);
                                attractedEntity = item;
                            }
                        }
                        else if (targetEntity instanceof ItemFrameEntity) {
                            ItemFrameEntity itemFrame = ((ItemFrameEntity) targetEntity);
                            if (!itemFrame.getItem().isEmpty()) {
                                ItemEntity item = new ItemEntity(world, targetEntity.getX(), targetEntity.getEyeY() - (double)0.4F, targetEntity.getZ(), itemFrame.getItem().copy());
                                itemFrame.setItem(ItemStack.EMPTY);
                                item.setPickUpDelay(40);
                                world.addFreshEntity(item);
                                attractedEntity = item;
                            }
                        }
                    }
                    if (attractedEntity.isPassenger()) {
                        attractedEntity.stopRiding();
                    }
                    if (attractedEntity instanceof LivingEntity) {
                        LivingEntity livingTarget = ((LivingEntity) attractedEntity);
                        if (livingTarget instanceof MobEntity && ((MobEntity)livingTarget).isAggressive() && theHand.recoveryCount < 8) {
                            MCUtil.loseTarget((MobEntity)livingTarget, user);
                        }
                        livingTarget.addEffect(new EffectInstance(InitEffects.SURPRISE.get(), 10, 0, false, false, true));
                        if (theHand.getFinisherMeter() < 0.45) {
                            theHand.addFinisherMeter(0.45F - theHand.getFinisherMeter(), StandEntity.FINISHER_NO_DECAY_TICKS);
                        }
                    }
                    Vector3d tpPos = attractedEntity.position().add(tpVec);
                    attractedEntity.teleportTo(tpPos.x, tpPos.y, tpPos.z);
                }
            }
        }
        else {
            if (entity.isPassenger()) {
                entity.stopRiding();
            }
            Vector3d tpPos = rayTrace.getLocation();
            entity.teleportTo(tpPos.x, tpPos.y, tpPos.z);
        }
    }

    @Override
    public int getHoldDurationToFire(IStandPower power) {
        return shortedHoldDuration(power, super.getHoldDurationToFire(power));
    }

    private int shortedHoldDuration(IStandPower power, int ticks) {
        if (power.isActive() && power.getStandManifestation() instanceof StandEntity) {
            StandEntity stand = (StandEntity) power.getStandManifestation();
            return ticks > 0 && power.getResolveLevel() >= 4 ? (int) (10 - (4 * stand.getFinisherMeter())) :
                    (int) (ticks * (7 * stand.getFinisherMeter()));

        }
        return ticks > 0 && power.getResolveLevel() >= 4 ? 10 : ticks;
    }

    @Override
    public boolean cancelHeldOnGettingAttacked(IStandPower power, DamageSource dmgSource, float dmgAmount) {
        return true;
    }



    public static class HeavyPunchInstance extends StandEntityPunch {

        public HeavyPunchInstance(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
            super(stand, target, dmgSource);
        }

        @Override
        protected boolean onAttack(StandEntity stand, Entity target, StandEntityDamageSource dmgSource, float damage) {
            return super.onAttack(stand, target, dmgSource, damage);
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
                    if (helmet.isDamageableItem() && damageToArmor(helmet, 0.1F) < helmet.getMaxDamage()) {
                        helmet.setDamageValue(damageToArmor(helmet, 0.1F));
                    }
                    else {
                        helmet.shrink(1);
                    }
                }
                if (!chestplace.isEmpty()) {
                    if (chestplace.isDamageableItem() && damageToArmor(chestplace, 0.15F) < chestplace.getMaxDamage()) {
                        chestplace.setDamageValue(damageToArmor(chestplace, 0.15F));
                    }
                    else {
                        chestplace.shrink(1);
                    }
                }
                if (!leggings.isEmpty()) {
                    if (leggings.isDamageableItem() && damageToArmor(leggings, 0.1F) < leggings.getMaxDamage()) {
                        leggings.setDamageValue(damageToArmor(leggings, 0.1F));
                    }
                    else {
                        leggings.shrink(1);
                    }
                }
                if (!boots.isEmpty()) {
                    if (boots.isDamageableItem() && damageToArmor(boots, 0.05F) < boots.getMaxDamage()) {
                        boots.setDamageValue(damageToArmor(boots, 0.05F));
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

    public static class HeavyPunchBlockInstance extends StandBlockPunch {

        public HeavyPunchBlockInstance(StandEntity stand, BlockPos targetPos, BlockState blockState, Direction face) {
            super(stand, targetPos, blockState, face);
        }

        @Override
        public boolean doHit(StandEntityTask task) {
            if (stand.level.isClientSide()) return false;
            super.doHit(task);

            return targetHit;
        }

        @Override
        public boolean playImpactSound() {
            return true;
        }

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
