package io.github.kituin.chatimage.network;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.neoforged.neoforge.network.PacketDistributor;
public final class PlatformNetworking {
    public static void sendToServer(CustomPacketPayload packet) {
        Minecraft.getInstance().execute(()->{
            var connection=Minecraft.getInstance().getConnection();
            if(connection!=null && connection.hasChannel(packet.type())) PacketDistributor.sendToServer(packet);
        });
    }
    public static void sendToPlayer(ServerPlayer player,CustomPacketPayload packet) {
        if(player==null)return;
        player.level().getServer().execute(()->{
            if(player.connection.hasChannel(packet.type())) PacketDistributor.sendToPlayer(player,packet);
        });
    }
}
