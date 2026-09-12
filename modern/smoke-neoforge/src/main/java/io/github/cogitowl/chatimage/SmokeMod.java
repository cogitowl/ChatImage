package io.github.cogitowl.chatimage;
import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@Mod(value="chatimage_smoke", dist=Dist.CLIENT)
public final class SmokeMod {
    public SmokeMod(IEventBus bus) { bus.addListener((FMLClientSetupEvent event) -> {
        java.util.concurrent.CompletableFuture.delayedExecutor(15, java.util.concurrent.TimeUnit.SECONDS).execute(() -> {
            var minecraft = net.minecraft.client.Minecraft.getInstance();
            minecraft.execute(() -> SmokeTest.start(minecraft));
        });
    }); }
}
