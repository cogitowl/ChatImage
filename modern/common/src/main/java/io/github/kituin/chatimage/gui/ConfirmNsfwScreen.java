package io.github.kituin.chatimage.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;


import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import static io.github.kituin.chatimage.tool.SimpleUtil.*;


public class ConfirmNsfwScreen extends ConfirmScreen {

    public ConfirmNsfwScreen(BooleanConsumer callback, String link) {
        this(callback, createTranslatableComponent("nsfw.chatimage.open"), createLiteralComponent(link));
    }

    public ConfirmNsfwScreen(BooleanConsumer callback, Component title, Component message) {
        super(callback, title, message);
        this.yesButtonComponent = CommonComponents.GUI_YES;
        this.noButtonComponent = CommonComponents.GUI_NO;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor matrices, int mouseX, int mouseY, float delta) {
        super.extractRenderState(matrices, mouseX, mouseY, delta);
        matrices.centeredText(this.font, createTranslatableComponent("nsfw.chatimage.warning"), this.width / 2, 110, 16764108);
    }
}
