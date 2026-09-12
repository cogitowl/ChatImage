// IF >= fabric-1.21.11
//package io.github.kituin.chatimage.mixin;
//
//import io.github.kituin.ChatImageCode.ChatImageCode;
//import io.github.kituin.ChatImageCode.ClientStorage;
//import io.github.kituin.chatimage.gui.ConfirmNsfwScreen;
//import io.github.kituin.chatimage.tool.ChatImageStyle.ShowImage;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.gui.screen.ChatScreen;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.text.Style;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import static io.github.kituin.chatimage.client.ChatImageClient.CONFIG;
//
///** Since 1.21.11 chat clicks are handled by ChatScreen. */
//@Mixin(ChatScreen.class)
//public abstract class ChatScreenMixin {
//    @Inject(method = "handleClickEvent", at = @At("HEAD"), cancellable = true)
//    private void chatimage$confirm(Style style, boolean insert, CallbackInfoReturnable<Boolean> cir) {
//        if (style == null || !(style.getHoverEvent() instanceof ShowImage(ChatImageCode code))) return;
//        if (!code.isNsfw() || CONFIG.nsfw || ClientStorage.ContainNsfw(code.getUrl())) return;
//        MinecraftClient client = MinecraftClient.getInstance();
//        client.setScreen(new ConfirmNsfwScreen(open -> {
//            if (open) ClientStorage.AddNsfw(code.getUrl(), 1);
//            client.setScreen((Screen) (Object) this);
//        }, code.getUrl()));
//        cir.setReturnValue(true);
//    }
//}
// END IF
