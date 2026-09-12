package io.github.kituin.chatimage.network;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
public final class PlatformNetworking {
    private static Channel<CustomPacketPayload> channel;
    public static void register() {
        channel=ChannelBuilder.named("chatimage:main").networkProtocolVersion(1).optional().payloadChannel().play()
            .serverbound().addMain(FileChannelPacket.ID,FileChannelPacket.CODEC,
                (packet,context)->ChatImagePacket.serverFileChannelReceived(packet,context.getSender()))
            .bidirectional().addMain(FileInfoChannelPacket.ID,FileInfoChannelPacket.CODEC,(packet,context)->{
                if(context.getSender()!=null) ChatImagePacket.serverGetFileChannelReceived(packet,context.getSender());
                else ChatImagePacket.clientGetFileChannelReceived(packet);
            })
            .clientbound().addMain(DownloadFileChannelPacket.ID,DownloadFileChannelPacket.CODEC,
                (packet,context)->ChatImagePacket.clientDownloadFileChannelReceived(packet)).build();
    }
    public static void sendToServer(CustomPacketPayload packet) {
        Minecraft.getInstance().execute(()->{
            var listener=Minecraft.getInstance().getConnection();
            if(listener!=null && channel.isRemotePresent(listener.getConnection())) channel.send(packet,listener.getConnection());
        });
    }
    public static void sendToPlayer(ServerPlayer player,CustomPacketPayload packet) {
        if(player==null)return;
        player.level().getServer().execute(()->channel.send(packet,PacketDistributor.PLAYER.with(player)));
    }
}
