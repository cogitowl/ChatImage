package io.github.kituin.chatimage;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import io.github.kituin.ChatImageCode.ChatImageCodeInstance;
import io.github.kituin.chatimage.integration.ChatImageLogger;
import io.github.kituin.chatimage.network.PlatformNetworking;
@Mod("chatimage")
public final class ChatImage {
    public static final Logger LOGGER=LogUtils.getLogger();
    public ChatImage(FMLJavaModLoadingContext context) {
        ChatImageCodeInstance.LOGGER=new ChatImageLogger();
        PlatformNetworking.register();
    }
    @Mod.EventBusSubscriber(modid="chatimage",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
    public static final class ClientEvents {
        @net.minecraftforge.eventbus.api.listener.SubscribeEvent
        public static void setup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
            io.github.kituin.chatimage.client.ChatImageClient.initialize(net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get());
        }
        @net.minecraftforge.eventbus.api.listener.SubscribeEvent
        public static void keys(net.minecraftforge.client.event.RegisterKeyMappingsEvent event) {
            io.github.kituin.chatimage.client.ChatImageClient.registerKeys(event::register);
        }
        @net.minecraftforge.eventbus.api.listener.SubscribeEvent
        public static void commands(net.minecraftforge.client.event.RegisterClientCommandsEvent event) {
            io.github.kituin.chatimage.client.ChatImageClient.registerCommands(event.getDispatcher());
        }
        @net.minecraftforge.eventbus.api.listener.SubscribeEvent
        public static void tick(net.minecraftforge.event.TickEvent.ClientTickEvent.Post event) {
            io.github.kituin.chatimage.client.ChatImageClient.tick();
        }
    }
}
