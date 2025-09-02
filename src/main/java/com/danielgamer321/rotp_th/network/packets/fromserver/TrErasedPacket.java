package com.danielgamer321.rotp_th.network.packets.fromserver;

import com.danielgamer321.rotp_th.capability.entity.EntityUtilCapProvider;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrErasedPacket {
    private final int entityId;
    private final float Erased;

    public TrErasedPacket(int entityId, float erased) {
        this.entityId = entityId;
        this.Erased = erased;
    }
    
    
    
    public static class Handler implements IModPacketHandler<TrErasedPacket> {

        @Override
        public void encode(TrErasedPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeFloat(msg.Erased);
        }

        @Override
        public TrErasedPacket decode(PacketBuffer buf) {
            return new TrErasedPacket(buf.readInt(), buf.readFloat());
        }

        @Override
        public void handle(TrErasedPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity != null) {
                entity.getCapability(EntityUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setErased(msg.Erased));
            }
        }

        @Override
        public Class<TrErasedPacket> getPacketClass() {
            return TrErasedPacket.class;
        }
    }

}
