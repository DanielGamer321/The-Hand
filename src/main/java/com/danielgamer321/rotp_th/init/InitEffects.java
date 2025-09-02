package com.danielgamer321.rotp_th.init;

import com.danielgamer321.rotp_th.RotpTheHandAddon;
import com.danielgamer321.rotp_th.potion.SurpriseEffect;
import com.github.standobyte.jojo.potion.StatusEffect;
import com.github.standobyte.jojo.potion.StunEffect;

import com.google.common.collect.ImmutableSet;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

@EventBusSubscriber(modid = RotpTheHandAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class InitEffects {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, RotpTheHandAddon.MOD_ID);

    public static final RegistryObject<Effect> ERASED = EFFECTS.register("erased",
            () -> new StatusEffect(EffectType.HARMFUL, 0xCAF6F4));

    public static final RegistryObject<StunEffect> SURPRISE = EFFECTS.register("surprise",
            () -> new SurpriseEffect(0xDD0F0F).setUncurable());

    private static Set<Effect> TRACKED_EFFECTS;
    @SubscribeEvent(priority = EventPriority.LOW)
    public static final void afterEffectsRegister(RegistryEvent.Register<Effect> event) {
        TRACKED_EFFECTS = ImmutableSet.of(
                ERASED.get(),
                SURPRISE.get()
        );
    }

    public static boolean isEffectTracked(Effect effect) {
        return TRACKED_EFFECTS.contains(effect);
    }
}
