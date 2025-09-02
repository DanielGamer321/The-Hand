package com.danielgamer321.rotp_th.capability.entity;

import com.danielgamer321.rotp_th.network.PacketManager;
import com.danielgamer321.rotp_th.network.packets.fromserver.TrErasedPacket;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

public class EntityUtilCap {
    private final Entity entity;
    private float erased;
    
    public EntityUtilCap(Entity entity) {
        this.entity = entity;
    }

    public void setErased(float erased) {
        erased = Math.max(erased, 0);
        if (this.erased != erased) {
            this.erased = erased;
            if (!entity.level.isClientSide()) {
                PacketManager.sendToClientsTrackingAndSelf(new TrErasedPacket(entity.getId(), erased), entity);
            }
        }
        this.erased = erased;
    }

    public float getErased() {
        return erased;
    }

    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("Erased", erased);
        return nbt;
    }

    public void fromNBT(CompoundNBT nbt) {
        this.erased = nbt.getFloat("Erased");
    }
}
