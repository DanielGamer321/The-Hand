package com.danielgamer321.rotp_th;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.danielgamer321.rotp_th.init.InitEntities;
import com.danielgamer321.rotp_th.init.InitSounds;
import com.danielgamer321.rotp_th.init.InitStands;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RotpTheHandAddon.MOD_ID)
public class RotpTheHandAddon {
    // The value here should match an entry in the META-INF/mods.toml file
    public static final String MOD_ID = "rotp_th";
    private static final Logger LOGGER = LogManager.getLogger();

    public RotpTheHandAddon() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RotpTheHandConfig.commonSpec);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        InitEntities.ENTITIES.register(modEventBus);
        InitSounds.SOUNDS.register(modEventBus);
        InitStands.ACTIONS.register(modEventBus);
        InitStands.STANDS.register(modEventBus);
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
