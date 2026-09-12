package io.github.kituin.chatimage.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.kituin.ChatImageCode.ChatImageCode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/** Chat payload used before Minecraft 1.21.5 changed hover events to records. */
public final class ChatImageStyle {
    public static final MapCodec<ChatImageCode> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("url").forGetter(ChatImageCode::getUrl),
        Codec.BOOL.optionalFieldOf("nsfw", false).forGetter(ChatImageCode::isNsfw)
    ).apply(instance, (url, nsfw) -> new ChatImageCode.Builder().setNsfw(nsfw).setUrlForce(url).build()));
    public static final Codec<ChatImageCode> CODEC = MAP_CODEC.codec();
    public static final HoverEvent.Action<ChatImageCode> SHOW_IMAGE = new HoverEvent.Action<>(
        "show_chatimage", true, CODEC, (text, registry) -> legacy(text));

    private static DataResult<ChatImageCode> legacy(Component text) {
        try { return DataResult.success(new ChatImageCode.Builder().fromCode(text.getString()).build()); }
        catch (Exception error) { return DataResult.error(() -> "Invalid ChatImage code: " + error.getMessage()); }
    }
    public static Style getStyleFromCode(ChatImageCode code) { return getStyleFromCode(code, ChatFormatting.GREEN); }
    public static Style getStyleFromCode(ChatImageCode code, ChatFormatting color) {
        return Style.EMPTY.withHoverEvent(new HoverEvent(SHOW_IMAGE, code)).withColor(color);
    }
    public static MutableComponent messageFromCode(ChatImageCode code) {
        return code.messageFromCode(Component::literal, Component::translatable, MutableComponent::append)
            .withStyle(getStyleFromCode(code));
    }
}
