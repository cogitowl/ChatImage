package io.github.cogitowl.chatimage;

import io.github.kituin.chatimage.platform.GameScreens;
import com.mojang.serialization.JsonOps;
import io.github.kituin.ChatImageCode.ChatImageCode;
import io.github.kituin.ChatImageCode.ChatImageCodeInstance;
import io.github.kituin.chatimage.tool.ChatImageStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.MixinEnvironment;
import java.nio.file.Files;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/** Run only via runSmoke; this class is never packaged into the release mod. */
public final class SmokeTest {
    private static void result(String result) {
        com.mojang.logging.LogUtils.getLogger().info("CHATIMAGE_SMOKE: {}", result);
        try { Files.writeString(java.nio.file.Path.of("smoke-result.txt"), result); }
        catch (java.io.IOException error) { throw new RuntimeException(error); }
    }
    public static void start(Minecraft minecraft) {
            try {
                MixinEnvironment.getCurrentEnvironment().audit();
                var path = Files.createTempFile("chatimage-smoke-", ".png");
                BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) image.setRGB(x, y, 0xff00cc88);
                ImageIO.write(image, "png", path.toFile());
                var code = new ChatImageCode.Builder().setUrl(path.toUri().toString()).build();
                var hover = new ChatImageStyle.ShowImage(code);
                var encoded = HoverEvent.CODEC.encodeStart(JsonOps.INSTANCE, hover).getOrThrow();
                var decoded = HoverEvent.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();
                if (!(decoded instanceof ChatImageStyle.ShowImage)) throw new AssertionError("Hover codec round trip failed");
                try (var input = Files.newInputStream(path)) {
                    var texture = ChatImageCodeInstance.CLIENT_ADAPTER.loadTexture(input);
                    if (texture.getWidth() != 16 || texture.getHeight() != 16) throw new AssertionError("Wrong image dimensions");
                }
                java.util.concurrent.CompletableFuture.delayedExecutor(10, java.util.concurrent.TimeUnit.SECONDS)
                    .execute(() -> minecraft.execute(() -> GameScreens.set(minecraft, new SmokeScreen(code))));
                result("STARTED: hover codec and texture passed; waiting for render test");
            } catch (Throwable error) {
                com.mojang.logging.LogUtils.getLogger().error("CHATIMAGE_SMOKE_FAILED", error);
                result("FAILED: " + error);

                minecraft.stop();
            }
    }

    private static final class SmokeScreen extends Screen {
        private final ChatImageCode code;
        private int frames;
        private int ticks;
        SmokeScreen(ChatImageCode code) { super(Component.literal("ChatImage smoke test")); this.code = code; }
        public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, float delta) {
            try {
                var method = GuiGraphicsExtractor.class.getDeclaredMethod("componentHoverEffect", net.minecraft.client.gui.Font.class, Style.class, int.class, int.class);
                method.setAccessible(true);
                method.invoke(graphics, font, ChatImageStyle.getStyleFromCode(code), 100, 100);
                frames++;
                if (frames > 20 && code.getFrame().loadImage(320, 240)) {
                    result("OK: mixins, hover codec, texture registration, image rendering");
                    minecraft.stop();
                } else if (frames > 600) {
                    result("FAILED: image not loaded after 600 frames");
                    minecraft.stop();
                }
            } catch (Throwable error) {
                com.mojang.logging.LogUtils.getLogger().error("CHATIMAGE_SMOKE_FAILED", error);
                result("FAILED: " + error);

                minecraft.stop();
            }
        }
    }
}
