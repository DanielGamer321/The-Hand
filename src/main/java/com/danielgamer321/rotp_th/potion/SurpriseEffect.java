package com.danielgamer321.rotp_th.potion;

import com.github.standobyte.jojo.potion.StunEffect;
import com.github.standobyte.jojo.util.mc.reflection.CommonReflection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SurpriseEffect extends StunEffect {

    public SurpriseEffect(int liquidColor) {
        super(liquidColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.abilities.flying = false;
        }
        if (resetsDeltaMovement()) {
            entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
        }
        if (entity instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) entity;
            CommonReflection.setCreeperSwell(creeper, -1);
        }
    }

    @Override
    public boolean isApplicable(LivingEntity entity) {
        return !(entity instanceof PlayerEntity);
    }
}
