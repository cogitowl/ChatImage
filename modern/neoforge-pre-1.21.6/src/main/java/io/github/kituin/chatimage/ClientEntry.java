package io.github.kituin.chatimage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import io.github.kituin.chatimage.client.ChatImageClient;
import io.github.kituin.chatimage.gui.ConfigScreen;
@Mod(value="chatimage",dist=Dist.CLIENT)
public final class ClientEntry {
    public ClientEntry(IEventBus bus, ModContainer container) {
        ChatImageClient.initialize(FMLPaths.CONFIGDIR.get());
        bus.addListener((RegisterKeyMappingsEvent event)->ChatImageClient.registerKeys(event::register));
        NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event)->ChatImageClient.registerCommands(event.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event)->ChatImageClient.tick());
        container.registerExtensionPoint(IConfigScreenFactory.class,(ignored,parent)->new ConfigScreen(parent));
    }
}
