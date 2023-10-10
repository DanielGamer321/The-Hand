package com.danielgamer321.rotp_th.action.stand;

import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityMeleeBarrage;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.entity.stand.StandStatFormulas;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TheHandErasureBarrage extends StandEntityMeleeBarrage {
    public static final StandPose ERASURE_BARRAGE_POSE = new StandPose("ERASURE_BARRAGE");

    public TheHandErasureBarrage(StandEntityMeleeBarrage.Builder builder) {
        super(builder);
    }

    @Override
    public void onTaskSet(World world, StandEntity standEntity, IStandPower standPower, Phase phase, StandEntityTask task, int ticks) {
        standEntity.alternateHands();
        if (!world.isClientSide()) {
            TheHandEntity thehand = (TheHandEntity) standEntity;
            thehand.setErase(true);
            thehand.setErasureBarrage(true);
        }
    }

    @Override
    public BarrageEntityPunch punchEntity(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
        BarrageEntityPunch stabBarrage = super.punchEntity(stand, target, dmgSource);
        dmgSource.bypassArmor().bypassMagic();
        stabBarrage.damage(StandStatFormulas.getBarrageHitDamage(stand.getAttackDamage()/1.25, stand.getPrecision()*25));
        stabBarrage.addFinisher(0.0F);
        stabBarrage.reduceKnockback((float) stand.getAttackDamage() * 0.0075F);
        return stabBarrage;
    }

    @Override
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        if (!world.isClientSide()) {
            TheHandEntity thehand = (TheHandEntity) standEntity;
            thehand.setErase(false);
            thehand.setErasureBarrage(false);
        }
    }

    @Override
    public int getCooldownAdditional(IStandPower power, int ticksHeld) {
        int cooldown = super.getCooldownAdditional(power, ticksHeld);
        return cooldownFromHoldDuration(cooldown, power, ticksHeld);
    }
}
