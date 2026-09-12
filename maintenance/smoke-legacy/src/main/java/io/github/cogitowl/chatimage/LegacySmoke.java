package io.github.cogitowl.chatimage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import io.github.kituin.ChatImageCode.ChatImageCodeInstance;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.MixinEnvironment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** Exercises loader startup, every mixin target, and PNG texture registration. */
public final class LegacySmoke implements ClientModInitializer {
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
            CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> client.execute(() -> run(client))));
    }
    private static void run(MinecraftClient client) {
        String result;
        Path imagePath = null;
        try {
            MixinEnvironment.getCurrentEnvironment().audit();
            imagePath = Files.createTempFile("chatimage-smoke-", ".png");
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            for (int x=0;x<16;x++) for (int y=0;y<16;y++) image.setRGB(x,y,0xff00cc88);
            ImageIO.write(image,"png",imagePath.toFile());
            try (var input = Files.newInputStream(imagePath)) {
                var texture = ChatImageCodeInstance.CLIENT_ADAPTER.loadTexture(input);
                if (texture.getWidth()!=16 || texture.getHeight()!=16) throw new AssertionError("Wrong image dimensions");
            }
            result = "OK: loader startup, mixin audit, PNG texture registration";
        } catch (Throwable error) {
            org.slf4j.LoggerFactory.getLogger("ChatImageSmoke").error("Smoke failed", error);
            result = "FAILED: " + error;
        }
        try {
            Files.writeString(Path.of("smoke-result.txt"),result);
            if (imagePath!=null) Files.deleteIfExists(imagePath);
        } catch (java.io.IOException error) { throw new RuntimeException(error); }
        org.slf4j.LoggerFactory.getLogger("ChatImageSmoke").info(result);
        client.scheduleStop();
    }
}
