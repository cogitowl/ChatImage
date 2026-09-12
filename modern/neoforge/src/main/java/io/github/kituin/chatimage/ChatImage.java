package io.github.kituin.chatimage;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.server.level.ServerPlayer;
import io.github.kituin.ChatImageCode.ChatImageCodeInstance;
import io.github.kituin.chatimage.integration.ChatImageLogger;
import io.github.kituin.chatimage.network.*;
@Mod("chatimage")
public final class ChatImage {
    public static final Logger LOGGER=LogUtils.getLogger();
    public ChatImage(IEventBus bus) {
        ChatImageCodeInstance.LOGGER=new ChatImageLogger();
        bus.addListener(ChatImage::registerPayloads);
    }
    private static void registerPayloads(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
        var registrar=event.registrar("1").optional();
        registrar.playToServer(FileChannelPacket.ID, FileChannelPacket.CODEC,
            (packet,context)->ChatImagePacket.serverFileChannelReceived(packet,(ServerPlayer)context.player()));
        registrar.playBidirectional(FileInfoChannelPacket.ID, FileInfoChannelPacket.CODEC,
            (packet,context)->ChatImagePacket.serverGetFileChannelReceived(packet,(ServerPlayer)context.player()),
            (packet,context)->ChatImagePacket.clientGetFileChannelReceived(packet));
        registrar.playToClient(DownloadFileChannelPacket.ID, DownloadFileChannelPacket.CODEC,
            (packet,context)->ChatImagePacket.clientDownloadFileChannelReceived(packet));
    }
}
