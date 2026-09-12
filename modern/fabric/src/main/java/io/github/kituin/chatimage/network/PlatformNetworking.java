package io.github.kituin.chatimage.network;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
public final class PlatformNetworking {
    public static void sendToServer(CustomPacketPayload packet) {
        Minecraft.getInstance().execute(() -> {
            if (ClientPlayNetworking.canSend(packet.type())) ClientPlayNetworking.send(packet);
        });
    }
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        if (player == null) return;
        player.level().getServer().execute(() -> {
            if (ServerPlayNetworking.canSend(player, packet.type())) ServerPlayNetworking.send(player, packet);
        });
    }
}
