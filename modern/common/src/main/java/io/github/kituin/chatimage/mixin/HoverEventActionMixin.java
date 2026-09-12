package io.github.kituin.chatimage.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.github.kituin.chatimage.tool.ChatImageStyle;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Arrays;

/** Retains ActionLib's show_chatimage wire format on current Minecraft versions. */
@Mixin(HoverEvent.Action.class)
public abstract class HoverEventActionMixin {
    @Shadow @Final @Mutable private static HoverEvent.Action[] $VALUES;
    @Shadow @Final @Mutable public static Codec<HoverEvent.Action> UNSAFE_CODEC;
    @Shadow @Final @Mutable public static Codec<HoverEvent.Action> CODEC;

    @Invoker("<init>")
    private static HoverEvent.Action chatimage$create(String id, int ordinal, String name,
            boolean allowed, MapCodec<? extends HoverEvent> codec) { throw new AssertionError(); }

    @Invoker("filterForSerialization")
    private static DataResult<HoverEvent.Action> chatimage$validate(HoverEvent.Action action) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void chatimage$register(CallbackInfo ci) {
        int index = $VALUES.length;
        $VALUES = Arrays.copyOf($VALUES, index + 1);
        $VALUES[index] = chatimage$create("SHOW_IMAGE", index, "show_chatimage", true,
                ChatImageStyle.ShowImage.CODEC);
        UNSAFE_CODEC = StringRepresentable.fromEnum(() -> $VALUES);
        CODEC = UNSAFE_CODEC.validate(HoverEventActionMixin::chatimage$validate);
    }
}
