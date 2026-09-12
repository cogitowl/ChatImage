package io.github.kituin.chatimage.mixin;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.kituin.chatimage.tool.ChatImageStyle;
import net.minecraft.network.chat.HoverEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Extend the existing codec so vanilla and previously registered actions survive. */
@Mixin(HoverEvent.Action.class)
public abstract class HoverEventActionMixin {
    @Shadow @Final @Mutable public static Codec<HoverEvent.Action<?>> CODEC;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void chatimage$register(CallbackInfo ci) {
        Codec<HoverEvent.Action<?>> original = CODEC;
        // Resolve SHOW_IMAGE lazily to avoid a circular static-initialization dependency.
        Codec<HoverEvent.Action<?>> image = Codec.STRING.comapFlatMap(
            name -> name.equals("show_chatimage") ? DataResult.success(ChatImageStyle.SHOW_IMAGE)
                : DataResult.error(() -> "Not a ChatImage hover action"),
            HoverEvent.Action::getSerializedName);
        CODEC = Codec.either(image, original).xmap(
            either -> either.map(action -> action, action -> action),
            action -> action == ChatImageStyle.SHOW_IMAGE ? Either.left(action) : Either.right(action));
    }
}
