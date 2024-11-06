package com.danielgamer321.rotp_th;

import com.danielgamer321.rotp_th.network.packets.fromserver.CommonConfigPacket;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.network.packets.fromserver.ResetSyncedCommonConfigPacket;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = RotpTheHandAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class RotpTheHandConfig {
    
    public static class Common {
        private boolean loaded = false;
        public final ForgeConfigSpec.BooleanValue PercentDamage;
        
        private Common(ForgeConfigSpec.Builder builder) {
            this(builder, null);
        }
        
        private Common(ForgeConfigSpec.Builder builder, @Nullable String mainPath) {
            if (mainPath != null) {
                builder.push(mainPath);
            }
            
            builder.push("Erase Config");
            PercentDamage = builder
                        .translation("rotp_th.config.PercentDamage")
                        .define("PercentDamage", true);
            builder.pop();
            
            if (mainPath != null) {
                builder.pop();
            }
        }
        
        public boolean isConfigLoaded() {
            return loaded;
        }

        private void onLoadOrReload() {
            loaded = true;
        }
        
        

        public static class SyncedValues {
            private final boolean PercentDamage;
            
            public SyncedValues(PacketBuffer buf) {
                byte[] flags = buf.readByteArray();
                PercentDamage =                  (flags[0] & 2) > 0;
//                _ =                      (flags[0] & 128) > 0;
            }

            public void writeToBuf(PacketBuffer buf) {
                byte[] flags = new byte[] {0, 0};
                if (PercentDamage)                   flags[0] |= 2;
//                if (_)                       flags[0] |= 128;
                buf.writeByteArray(flags);
            }

            private SyncedValues(Common config) {
                PercentDamage = config.PercentDamage.get();
            }
            
            public void changeConfigValues() {
                COMMON_SYNCED_TO_CLIENT.PercentDamage.set(PercentDamage);
            }

            public static void resetConfig() {
                COMMON_SYNCED_TO_CLIENT.PercentDamage.clearCache();
            }

            
            
            public static void syncWithClient(ServerPlayerEntity player) {
                PacketManager.sendToClient(new CommonConfigPacket(new SyncedValues(COMMON_FROM_FILE)), player);
            }
            
            public static void onPlayerLogout(ServerPlayerEntity player) {
                PacketManager.sendToClient(new ResetSyncedCommonConfigPacket(), player);
            }
        }
    }
    
    private static boolean isElementNonNegativeFloat(Object num, boolean moreThanZero) {
        if (num instanceof Double) {
            Double numDouble = (Double) num;
            return (numDouble > 0 || !moreThanZero && numDouble == 0) && Float.isFinite(numDouble.floatValue());
        }
        return false;
    }
    
    
    
    public static class Client {
        
        private Client(ForgeConfigSpec.Builder builder) {
        }
    }


    static final ForgeConfigSpec commonSpec;
    private static final Common COMMON_FROM_FILE;
    private static final Common COMMON_SYNCED_TO_CLIENT;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON_FROM_FILE = specPair.getLeft();

        // how tf do the configs work?
        final Pair<Common, ForgeConfigSpec> syncedSpecPair = new ForgeConfigSpec.Builder().configure(builder -> new Common(builder, "synced"));
        CommentedConfig config = CommentedConfig.of(InMemoryCommentedFormat.defaultInstance());
        ForgeConfigSpec syncedSpec = syncedSpecPair.getRight();
        syncedSpec.correct(config);
        syncedSpec.setConfig(config);
        COMMON_SYNCED_TO_CLIENT = syncedSpecPair.getLeft();
    }
    
    public static Common getCommonConfigInstance(boolean isClientSide) {
        return isClientSide && !ClientUtil.isLocalServer() ? COMMON_SYNCED_TO_CLIENT : COMMON_FROM_FILE;
    }

    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;
    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }
    
    @SubscribeEvent
    public static void onConfigLoad(ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (RotpTheHandAddon.MOD_ID.equals(config.getModId()) && config.getType() == ModConfig.Type.COMMON) {
            COMMON_FROM_FILE.onLoadOrReload();
        }
    }
    
    @SubscribeEvent
    public static void onConfigReload(ModConfig.Reloading event) {
        ModConfig config = event.getConfig();
        if (RotpTheHandAddon.MOD_ID.equals(config.getModId()) && config.getType() == ModConfig.Type.COMMON) {
            // FIXME sync the config to all players on the server
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.getPlayerList().getPlayers().forEach(player -> {
                    Common.SyncedValues.syncWithClient(player);
                });
            }
        }
    }
}
