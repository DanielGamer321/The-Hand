package com.danielgamer321.rotp_th.util;

import com.danielgamer321.rotp_th.RotpTheHandAddon;
import com.danielgamer321.rotp_th.capability.entity.EntityUtilCapProvider;
import com.danielgamer321.rotp_th.init.InitEffects;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Map;

@EventBusSubscriber(modid = RotpTheHandAddon.MOD_ID)
public class GameplayEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        EntityStandType.giveEffectSharedWithStand(event.getEntityLiving(), event.getPotionEffect());

        Entity entity = event.getEntity();
        EffectInstance effectInstance = event.getPotionEffect();
        if (!entity.level.isClientSide()) {
            if (InitEffects.isEffectTracked(effectInstance.getEffect())) {
                ((ServerChunkProvider) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity,
                        new SPlayEntityEffectPacket(entity.getId(), effectInstance));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void releaseAi(LivingConversionEvent.Post event) {
        if (event.getOutcome() instanceof MobEntity) {
            LivingEntity pre = event.getEntityLiving();
            MobEntity converted = (MobEntity) event.getOutcome();
            if (converted.isNoAi() && pre.hasEffect(InitEffects.SURPRISE.get()) &&
                    !converted.hasEffect(InitEffects.SURPRISE.get())) {
                converted.setNoAi(false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void trackedPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        EntityStandType.removeEffectSharedWithStand(event.getEntityLiving(), event.getPotion());

        Entity entity = event.getEntity();
        if (!entity.level.isClientSide() && event.getPotionEffect() != null && InitEffects.isEffectTracked(event.getPotionEffect().getEffect())) {
            ((ServerChunkProvider) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity,
                    new SRemoveEntityEffectPacket(entity.getId(), event.getPotion()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void trackedPotionExpired(PotionEvent.PotionExpiryEvent event) {
        EntityStandType.removeEffectSharedWithStand(event.getEntityLiving(), event.getPotionEffect().getEffect());

        Entity entity = event.getEntity();
        if (!entity.level.isClientSide() && InitEffects.isEffectTracked(event.getPotionEffect().getEffect())) {
            ((ServerChunkProvider) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity,
                    new SRemoveEntityEffectPacket(entity.getId(), event.getPotionEffect().getEffect()));
        }
    }

    @SubscribeEvent
    public static void syncTrackedEffects(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            LivingEntity tracked = (LivingEntity) event.getTarget();
            for (Map.Entry<Effect, EffectInstance> effectEntry : tracked.getActiveEffectsMap().entrySet()) {
                if (InitEffects.isEffectTracked(effectEntry.getKey())) {
                    player.connection.send(new SPlayEntityEffectPacket(tracked.getId(), effectEntry.getValue()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        float heal = event.getAmount();
        float erased = entity.getCapability(EntityUtilCapProvider.CAPABILITY).map(cap -> cap.getErased()).orElse(0F);
        if (entity.hasEffect(InitEffects.ERASED.get())) {
            if (entity.getHealth() >= entity.getMaxHealth() - erased) {
                event.setCanceled(true);
            }
            else if (entity.getHealth() + heal > entity.getMaxHealth() - erased) {
                event.setAmount(heal - ((entity.getHealth() + heal) - (entity.getMaxHealth() - erased)));
            }
        }
    }
}
