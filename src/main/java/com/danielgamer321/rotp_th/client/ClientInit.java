package com.danielgamer321.rotp_th.client;

import com.danielgamer321.rotp_th.RotpTheHandAddon;
import com.danielgamer321.rotp_th.client.render.entity.renderer.stand.*;
import com.danielgamer321.rotp_th.init.AddonStands;
import com.danielgamer321.rotp_th.init.InitEntities;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = RotpTheHandAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {
    
    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(AddonStands.THE_HAND.getEntityType(), TheHandRenderer::new);
    }
}
