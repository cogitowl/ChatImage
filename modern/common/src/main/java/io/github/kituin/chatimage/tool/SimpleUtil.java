package io.github.kituin.chatimage.tool;

import io.github.kituin.chatimage.platform.GameScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class SimpleUtil {

    public static void setScreen(Minecraft client, Screen screen) {
        GameScreens.set(client, screen);
    }
    public static MutableComponent createTranslatableComponent(String text){
        return Component.translatable(text);
    }
    public static MutableComponent createTranslatableComponent(String key, Object... args){
        return Component.translatable(key, args);
    }


    public static MutableComponent createLiteralComponent(String text){
        return Component.literal(text);
    }
    public static MutableComponent composeGenericOptionText(Component text, Component value) {
        return net.minecraft.network.chat.CommonComponents.optionNameValue(text,value);
    }

}
