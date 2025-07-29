package com.danielgamer321.rotp_th.potion;

import com.github.standobyte.jojo.potion.StunEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SurpriseEffect extends StunEffect {

    public SurpriseEffect(int liquidColor) {
        super(liquidColor);
    }

    @Override
    public boolean isApplicable(LivingEntity entity) {
        return !(entity instanceof PlayerEntity);
    }
}
