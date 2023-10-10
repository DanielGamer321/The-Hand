package com.danielgamer321.rotp_th.client.render.entity.renderer.stand;

import com.danielgamer321.rotp_th.RotpTheHandAddon;
import com.danielgamer321.rotp_th.client.render.entity.model.stand.TheHandModel;
import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.github.standobyte.jojo.client.render.entity.renderer.stand.StandEntityRenderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class TheHandRenderer extends StandEntityRenderer<TheHandEntity, TheHandModel> {
    
    public TheHandRenderer(EntityRendererManager renderManager) {
        super(renderManager, new TheHandModel(), new ResourceLocation(RotpTheHandAddon.MOD_ID, "textures/entity/stand/the_hand.png"), 0);
    }
}
