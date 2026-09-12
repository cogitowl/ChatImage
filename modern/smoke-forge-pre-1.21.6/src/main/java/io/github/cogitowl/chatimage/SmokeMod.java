package io.github.cogitowl.chatimage;
import net.minecraftforge.fml.common.Mod;
@Mod("chatimage_smoke")
public final class SmokeMod {
    @Mod.EventBusSubscriber(modid="chatimage_smoke", value=net.minecraftforge.api.distmarker.Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class Events {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void setup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
            java.util.concurrent.CompletableFuture.delayedExecutor(15, java.util.concurrent.TimeUnit.SECONDS).execute(() -> {
                var minecraft = net.minecraft.client.Minecraft.getInstance();
                minecraft.execute(() -> SmokeTest.start(minecraft));
            });
        }
    }
}
