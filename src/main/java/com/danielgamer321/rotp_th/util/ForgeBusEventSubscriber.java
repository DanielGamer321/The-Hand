package com.danielgamer321.rotp_th.util;


import com.danielgamer321.rotp_th.RotpTheHandAddon;
import com.danielgamer321.rotp_th.capability.entity.*;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = RotpTheHandAddon.MOD_ID)
public class ForgeBusEventSubscriber {
    private static final ResourceLocation ENTITY_UTIL_CAP = new ResourceLocation(RotpTheHandAddon.MOD_ID, "entity_util");

    
    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        event.addCapability(ENTITY_UTIL_CAP, new EntityUtilCapProvider(entity));
    }
    
    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(EntityUtilCap.class, new EntityUtilCapStorage(), () -> new EntityUtilCap(null));
    }
}
