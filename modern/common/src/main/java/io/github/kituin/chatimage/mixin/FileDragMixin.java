package io.github.kituin.chatimage.mixin;

import io.github.kituin.chatimage.platform.GameScreens;
import io.github.kituin.chatimage.client.ChatImageClient;


import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;


@Mixin(MouseHandler.class)
public class FileDragMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(at = @At("RETURN"), method = "onDrop")
    private void onFilesDropped(long window, List<Path> paths,
                                int invalidFilesCount,
                                CallbackInfo ci) {
        if (GameScreens.current(this.minecraft) != null &&
                GameScreens.current(this.minecraft) instanceof ChatScreen &&
                this.minecraft.level != null && ChatImageClient.CONFIG.dragImage) {
            StringBuilder sb = new StringBuilder();
            for (Path o : paths) {
                if (ChatImageClient.CONFIG.dragUseCicode) {
                    sb.append("[[CICode,url=file:///").append(o).append("]]");
                } else {
                    sb.append("file:///").append(o);
                }
            }
            GameScreens.set(this.minecraft, new ChatScreen(sb.toString(), true));
        }
    }
}
