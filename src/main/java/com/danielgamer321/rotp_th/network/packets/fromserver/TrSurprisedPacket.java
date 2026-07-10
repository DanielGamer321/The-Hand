package com.danielgamer321.rotp_th.network.packets.fromserver;

import com.danielgamer321.rotp_th.capability.entity.EntityUtilCapProvider;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrSurprisedPacket {
    private final int entityId;
    private final int Surprised;

    public TrSurprisedPacket(int entityId, int times) {
        this.entityId = entityId;
        this.Surprised = times;
    }
    
    
    
    public static class Handler implements IModPacketHandler<TrSurprisedPacket> {

        @Override
        public void encode(TrSurprisedPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeInt(msg.Surprised);
        }

        @Override
        public TrSurprisedPacket decode(PacketBuffer buf) {
            return new TrSurprisedPacket(buf.readInt(), buf.readInt());
        }

        @Override
        public void handle(TrSurprisedPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity != null) {
                entity.getCapability(EntityUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setSurprised(msg.Surprised));
            }
        }

        @Override
        public Class<TrSurprisedPacket> getPacketClass() {
            return TrSurprisedPacket.class;
        }
    }

}
