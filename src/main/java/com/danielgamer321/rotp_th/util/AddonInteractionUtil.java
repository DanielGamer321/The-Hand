package com.danielgamer321.rotp_th.util;

import com.danielgamer321.rotp_th.RotpTheHandAddon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = RotpTheHandAddon.MOD_ID)
public class AddonInteractionUtil {
    private static final ResourceLocation AQUA_NECKLACE_ID = new ResourceLocation("jojo", "aqua_necklace");
    public static boolean isAquaNecklace(Entity entity) {
        if (entity == null) return false;

        EntityType<?> type = entity.getType();
        if (type == null) return false;
        ResourceLocation typeId = type.getRegistryName();
        return AQUA_NECKLACE_ID.equals(typeId);
    }
}
