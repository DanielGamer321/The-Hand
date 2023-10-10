package com.danielgamer321.rotp_th.init;

import java.util.function.Supplier;

import com.danielgamer321.rotp_th.RotpTheHandAddon;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.util.mc.OstSoundList;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RotpTheHandAddon.MOD_ID);
    
    public static final RegistryObject<SoundEvent> OKUYASU_THE_HAND = SOUNDS.register("okuyasu_the_hand",
            () -> new SoundEvent(new ResourceLocation(RotpTheHandAddon.MOD_ID, "okuyasu_the_hand")));

    public static final RegistryObject<SoundEvent> THE_HAND_SUMMON = SOUNDS.register("the_hand_summon",
            () -> new SoundEvent(new ResourceLocation(RotpTheHandAddon.MOD_ID, "the_hand_summon")));
    
    public static final Supplier<SoundEvent> THE_HAND_UNSUMMON = ModSounds.STAND_UNSUMMON_DEFAULT;
    
    public static final RegistryObject<SoundEvent> THE_HAND_PUNCH_LIGHT = SOUNDS.register("the_hand_punch_light",
            () -> new SoundEvent(new ResourceLocation(RotpTheHandAddon.MOD_ID, "the_hand_punch_light")));
    
    public static final RegistryObject<SoundEvent> THE_HAND_PUNCH_HEAVY = SOUNDS.register("the_hand_punch_heavy",
            () -> new SoundEvent(new ResourceLocation(RotpTheHandAddon.MOD_ID, "the_hand_punch_heavy")));

    public static final RegistryObject<SoundEvent> THE_HAND_KICK_HEAVY = SOUNDS.register("the_hand_kick_heavy",
            () -> new SoundEvent(new ResourceLocation(RotpTheHandAddon.MOD_ID, "the_hand_kick_heavy")));
    
    public static final Supplier<SoundEvent> THE_HAND_BARRAGE = THE_HAND_PUNCH_LIGHT;
    
    public static final RegistryObject<SoundEvent> THE_HAND_ERASE = SOUNDS.register("the_hand_erase",
            () -> new SoundEvent(new ResourceLocation(RotpTheHandAddon.MOD_ID, "the_hand_erase")));

    public static final RegistryObject<SoundEvent> THE_HAND_ERASURE_BARRAGE = SOUNDS.register("the_hand_erasure_barrage",
            () -> new SoundEvent(new ResourceLocation(RotpTheHandAddon.MOD_ID, "the_hand_erasure_barrage")));
    
    static final OstSoundList THE_HAND_OST = new OstSoundList(new ResourceLocation(RotpTheHandAddon.MOD_ID, "the_hand_ost"), SOUNDS);

}
