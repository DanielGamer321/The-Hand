package com.danielgamer321.rotp_th.action.stand;

import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.ActionTarget.TargetType;
import com.github.standobyte.jojo.action.stand.IHasStandPunch;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityHeavyAttack.HeavyPunchInstance;
import com.github.standobyte.jojo.action.stand.StandEntityMeleeBarrage;
import com.github.standobyte.jojo.action.stand.punch.IPunch;
import com.github.standobyte.jojo.action.stand.punch.StandBlockPunch;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
import com.github.standobyte.jojo.action.stand.punch.StandMissedPunch;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.entity.stand.*;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.network.packets.fromserver.TrBarrageHitSoundPacket;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandInstance.StandPart;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class TheHandErasureBarrage extends StandEntityAction implements IHasStandPunch {
    public static final StandPose ERASURE_BARRAGE_POSE = new StandPose("ERASURE_BARRAGE");
    protected final Supplier<SoundEvent> hitSound;
    protected final Supplier<SoundEvent> swingSound;

    public TheHandErasureBarrage(TheHandErasureBarrage.Builder builder) {
        super(builder);
        this.hitSound = builder.hitSound;
        this.swingSound = builder.swingSound;
    }

    @Override
    protected ActionConditionResult checkStandConditions(StandEntity stand, IStandPower power, ActionTarget target) {
        return !stand.canAttackMelee() ? ActionConditionResult.NEGATIVE : super.checkStandConditions(stand, power, target);
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
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        int hitsThisTick = 0;
        int hitsPerSecond = StandStatFormulas.getBarrageHitsPerSecond(standEntity.getAttackSpeed()*0.9);
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
    public void phaseTransition(World world, StandEntity standEntity, IStandPower standPower, 
            Phase from, Phase to, StandEntityTask task, int ticks) {
        if (world.isClientSide()) {
            standEntity.getBarrageHitSoundsHandler().setIsBarraging(to == Phase.PERFORM);
        }
    }
    
    @Override
    public BarrageEntityPunch punchEntity(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
        BarrageEntityPunch punch = new BarrageEntityPunch(stand, target, dmgSource).barrageHits(stand, target,stand.barrageHits);
        dmgSource.bypassArmor().bypassMagic();
        punch.impactSound(hitSound);
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
                damage = entity.getMaxHealth() * 0.0115F;
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
    public StandBlockPunch punchBlock(StandEntity stand, BlockPos pos, BlockState state) {
        return IHasStandPunch.super.punchBlock(stand, pos, state).impactSound(hitSound);
    }
    
    @Override
    public StandMissedPunch punchMissed(StandEntity stand) {
        return IHasStandPunch.super.punchMissed(stand).swingSound(hitSound);
    }
    
    public SoundEvent getHitSound() {
        return hitSound == null ? null : hitSound.get();
    }
    
    protected void clTtickSwingSound(int tick, StandEntity standEntity) {
        SoundEvent swingSound = getPunchSwingSound();
        if (swingSound != null) {
            standEntity.playSound(swingSound, 0.25F, 
                    1.8F - (float) standEntity.getAttackDamage() * 0.05F + standEntity.getRandom().nextFloat() * 0.2F, 
                    ClientUtil.getClientPlayer());
        }
    }
    
    @Override
    public SoundEvent getPunchSwingSound() {
        return swingSound.get();
    }
    
    @Override
    public void playPunchImpactSound(IPunch punch, TargetType punchType, boolean canPlay, boolean playAlways) {
        StandEntity stand = punch.getStand();
        if (!stand.level.isClientSide()) {
            SoundEvent sound = punch.getImpactSound();
            Vector3d pos = punch.getImpactSoundPos();
            PacketManager.sendToClientsTracking(
                    sound != null && pos != null && canPlay && (playAlways || punch.playImpactSound()) ? 
                            new TrBarrageHitSoundPacket(stand.getId(), sound, pos)
                            : TrBarrageHitSoundPacket.noSound(stand.getId()), 
            stand);
        }
    }

    @Override
    public StandRelativeOffset getOffsetFromUser(IStandPower standPower, StandEntity standEntity, StandEntityTask task) {
        if (standEntity.isArmsOnlyMode()) {
            return super.getOffsetFromUser(standPower, standEntity, task);
        }
        double minOffset = 0.5;
        double maxOffset = minOffset + standEntity.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.5 * standEntity.getStaminaCondition();
        return offsetToTarget(standPower, standEntity, task, 
                minOffset, maxOffset, 
                () -> ActionTarget.fromRayTraceResult(JojoModUtil.rayTrace(standPower.getUser(), maxOffset, standEntity::canHarm, 0.25, 0)))
                .orElse(StandRelativeOffset.withXRot(0, Math.min(maxOffset, standEntity.getMaxEffectiveRange())));
    }
    
    @Override
    protected boolean isCancelable(IStandPower standPower, StandEntity standEntity, @Nullable StandEntityAction newAction, Phase phase) {
        if (standEntity.barrageClashOpponent().isPresent()) {
            return true;
        }
        if (phase == Phase.RECOVERY) {
            return newAction != null && newAction.canFollowUpBarrage();
        }
        else {
            return super.isCancelable(standPower, standEntity, newAction, phase);
        }
    }
    
    @Override
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        if (!world.isClientSide() && newAction != this) {
            standEntity.barrageClashStopped();
        }
        if (world.isClientSide()) {
            standEntity.getBarrageHitSoundsHandler().setIsBarraging(false);
        }
        TheHandEntity thehand = (TheHandEntity) standEntity;
        thehand.setErase(false);
    }

    @Override
    public boolean cancelHeldOnGettingAttacked(IStandPower power, DamageSource dmgSource, float dmgAmount) {
        return dmgAmount >= 4F && "healthLink".equals(dmgSource.msgId);
    }
    
    @Override
    public boolean stopOnHeavyAttack(HeavyPunchInstance punch) {
        return true;
    }
    
    @Override
    protected ActionConditionResult checkTarget(ActionTarget target, LivingEntity user, IStandPower power) {
        return ActionConditionResult.POSITIVE;
    }
    
    @Override
    public ActionConditionResult checkStandTarget(ActionTarget target, StandEntity standEntity, IStandPower standPower) {
        if (target.getType() == TargetType.ENTITY) {
            return ActionConditionResult.noMessage(standEntity.barrageClashOpponent().map(otherStand -> {
                return otherStand == target.getEntity();
            }).orElse(false));
        }
        return ActionConditionResult.NEGATIVE;
    }
    
    @Override
    public boolean noFinisherDecay() {
        return true;
    }
    
    @Override
    public int getHoldDurationMax(IStandPower standPower) {
        LivingEntity user = standPower.getUser();
        if (user != null && user.hasEffect(ModStatusEffects.RESOLVE.get())) {
            return Integer.MAX_VALUE;
        }
        if (standPower.getStandManifestation() instanceof StandEntity) {
            return StandStatFormulas.getBarrageMaxDuration(((StandEntity) standPower.getStandManifestation()).getDurability()*0.3);
        }
        return 0;
    }
    
    @Override
    public int getStandRecoveryTicks(IStandPower standPower, StandEntity standEntity) {
        return standEntity.isArmsOnlyMode() ? 0 : StandStatFormulas.getBarrageRecovery(standEntity.getSpeed());
    }
    
    @Override
    public boolean isFreeRecovery(IStandPower standPower, StandEntity standEntity) {
        LivingEntity user = standPower.getUser();
        return user != null && user.hasEffect(ModStatusEffects.RESOLVE.get());
    }
    
    
    
    public static class Builder extends AbstractBuilder<TheHandErasureBarrage.Builder> {
        private Supplier<SoundEvent> hitSound = ModSounds.STAND_PUNCH_LIGHT;
        private Supplier<SoundEvent> swingSound = ModSounds.STAND_PUNCH_BARRAGE_SWING;
        
        public Builder() {
            super();
            standPose(TheHandErasureBarrage.ERASURE_BARRAGE_POSE)
            .standAutoSummonMode(AutoSummonMode.ARMS).holdType().staminaCostTick(20F)
            .standUserWalkSpeed(0.15F).standOffsetFront()
            .partsRequired(StandPart.ARMS);
        }
        
        public Builder barrageHitSound(Supplier<SoundEvent> barrageHitSound) {
            this.hitSound = barrageHitSound != null ? barrageHitSound : () -> null;
            return getThis();
        }
        
        public Builder barrageSwingSound(Supplier<SoundEvent> barrageSwingSound) {
            this.swingSound = barrageSwingSound != null ? barrageSwingSound : () -> null;
            return getThis();
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
    }
    
    

    public static class BarrageEntityPunch extends StandEntityPunch {
        private int barrageHits = 0;

        public BarrageEntityPunch(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
            super(stand, target, dmgSource);
            this
            .damage(getEraseDamage(target, stand))
            .addFinisher(-0.005F)
            .reduceKnockback((float) stand.getAttackDamage() * 0.0075F);
        }
        
        public BarrageEntityPunch barrageHits(StandEntity stand, Entity target, int hits) {
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
        return cooldownFromHoldDuration(cooldown, power, ticksHeld);
    }
}
