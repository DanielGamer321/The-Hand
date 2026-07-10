package com.danielgamer321.rotp_th.capability.entity;

import com.danielgamer321.rotp_th.network.PacketManager;
import com.danielgamer321.rotp_th.network.packets.fromserver.TrErasedPacket;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

public class EntityUtilCap {
    private final Entity entity;
    private float erased;
    private int surprised;
    
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

    public void setSurprised(int times) {
        times = Math.max(times, 0);
        if (this.surprised != times) {
            this.surprised = times;
            if (!entity.level.isClientSide()) {
                PacketManager.sendToClientsTrackingAndSelf(new TrErasedPacket(entity.getId(), times), entity);
            }
        }
        this.surprised = times;
    }

    public int sometimesSurprised() {
        return surprised;
    }

    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("Erased", erased);
        nbt.putInt("Surprised", surprised);
        return nbt;
    }

    public void fromNBT(CompoundNBT nbt) {
        this.erased = nbt.getFloat("Erased");
        this.surprised = nbt.getInt("Surprised");
    }
}
