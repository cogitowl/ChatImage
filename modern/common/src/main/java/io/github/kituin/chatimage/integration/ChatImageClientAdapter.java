package io.github.kituin.chatimage.integration;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.kituin.ChatImageCode.ChatImageFrame;
import io.github.kituin.ChatImageCode.IClientAdapter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import io.github.kituin.chatimage.network.FileChannelPacket;
import static io.github.kituin.ChatImageCode.NetworkHelper.createFilePacket;
import static io.github.kituin.chatimage.client.ChatImageClient.CONFIG;
import static io.github.kituin.chatimage.client.ChatImageClient.MOD_ID;
import static io.github.kituin.chatimage.network.ChatImagePacket.*;
import static io.github.kituin.chatimage.tool.SimpleUtil.createTranslatableComponent;

public class ChatImageClientAdapter implements IClientAdapter {
    private static final java.util.concurrent.atomic.AtomicLong TEXTURE_IDS = new java.util.concurrent.atomic.AtomicLong();

    @Override
    public int getTimeOut() {
        return CONFIG.timeout;
    }

    @Override
    public ChatImageFrame.TextureReader<Identifier> loadTexture(InputStream image) throws IOException {
        NativeImage nativeImage = NativeImage.read(image);
        int width = nativeImage.getWidth();
        int height = nativeImage.getHeight();
        Minecraft minecraft = Minecraft.getInstance();
        java.util.function.Supplier<ChatImageFrame.TextureReader<Identifier>> register = () -> {
            net.minecraft.client.renderer.texture.DynamicTexture texture = null;
            try {
                Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, "image/" + TEXTURE_IDS.incrementAndGet());
                texture = new net.minecraft.client.renderer.texture.DynamicTexture(() -> id.toString(), nativeImage);
                minecraft.getTextureManager().register(id, texture);
                return new ChatImageFrame.TextureReader<>(id, width, height);
            } catch (RuntimeException error) {
                if (texture != null) texture.close(); else nativeImage.close();
                throw error;
            }
        };
        try {
            return minecraft.isSameThread() ? register.get() : minecraft.submit(register).join();
        } catch (RuntimeException error) {
            throw new IOException("Could not register ChatImage texture", error);
        }
    }

    @Override
    public void sendToServer(String url, File file, boolean isToServer) {
        if (isToServer) {
            List<String> stringList = createFilePacket(url, file);
            sendPacketAsync(FileChannelPacket::new, stringList);
        } else {
            loadFromServer(url);
        }
    }

    @Override
    public void checkCachePath() {
        File folder = new File(CONFIG.cachePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @Override
    public int getMaxFileSize() {
        return CONFIG.MaxFileSize;
    }

    @Override
    public Component getProcessMessage(int i) {
        return createTranslatableComponent("process.chatimage.message", i);
    }


}
