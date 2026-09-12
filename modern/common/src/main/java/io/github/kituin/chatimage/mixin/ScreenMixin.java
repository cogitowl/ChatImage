package io.github.kituin.chatimage.mixin;


import io.github.kituin.chatimage.gui.ConfirmNsfwScreen;
import io.github.kituin.chatimage.tool.ChatImageStyle.ShowImage;
import io.github.kituin.ChatImageCode.ChatImageCode;
import io.github.kituin.ChatImageCode.ClientStorage;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;

import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;


import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.kituin.chatimage.client.ChatImageClient.CONFIG;
import static io.github.kituin.chatimage.tool.ChatImageStyle.ShowImage;
import static io.github.kituin.chatimage.tool.SimpleUtil.*;


/**
 * 注入修改悬浮显示图片
 *
 * @author kitUIN
 */

@Mixin(net.minecraft.client.gui.screens.ChatScreen.class)
public abstract class ScreenMixin {




    @Unique
    private String nsfwUrl;

    @Unique
    private void confirmNsfw(boolean open) {
        if (open) {
            ClientStorage.AddNsfw(nsfwUrl, 1);
        }
        this.nsfwUrl = null;
        setScreen(Minecraft.getInstance(), (Screen) (Object) this);
    }

    @Inject(at = @At("RETURN"),
            method = "handleComponentClicked", cancellable = true)
    private void handleTextClick(Style style, boolean insertionMode, CallbackInfoReturnable<Boolean> cir) {
        if (style != null && style.getHoverEvent() != null) {
            HoverEvent hoverEvent = style.getHoverEvent();
            if (!(hoverEvent instanceof ShowImage(ChatImageCode code)))return;
            if (code != null && code.isNsfw() && !ClientStorage.ContainNsfw(code.getUrl()) && !CONFIG.nsfw) {
                this.nsfwUrl = code.getUrl();
                setScreen(Minecraft.getInstance(), new ConfirmNsfwScreen(this::confirmNsfw, nsfwUrl));
                cir.setReturnValue(true);
            }
        }
    }
}
