package io.github.kituin.chatimage.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** Minecraft version-specific screen ownership. */
public final class GameScreens {
    private GameScreens() {}
    public static Screen current(Minecraft client) { return client.screen; }
    public static void set(Minecraft client, Screen screen) { client.setScreen(screen); }
}
