package io.github.kituin.chatimage.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.kituin.ChatImageCode.ChatImageCode;
import io.github.kituin.ChatImageCode.ChatImageCodeInstance;
import io.github.kituin.ChatImageCode.ChatImageConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;


import static io.github.kituin.ChatImageCode.ChatImageCodeInstance.LOGGER;
import static io.github.kituin.chatimage.client.ChatImageClient.CONFIG;
import static io.github.kituin.chatimage.tool.SimpleUtil.createTranslatableComponent;

public class ChatImageCommand {
    public static int sendChatImage(CommandContext<CommandSourceStack> context) {
        String url = StringArgumentType.getString(context, "url");
        ChatImageCode.Builder builder = ChatImageCodeInstance.createBuilder().setUrlForce(url);
        try {
            String name = StringArgumentType.getString(context, "name");
            builder.setName(name);
        } catch (java.lang.IllegalArgumentException e) {
            LOGGER.info("arg: `name` is omitted, use the default string");
        }
        net.minecraft.client.Minecraft.getInstance().player.connection.sendChat(builder.build().toString());
        return Command.SINGLE_SUCCESS;
    }

    public static int help(CommandContext<CommandSourceStack> context) {

        context.getSource().sendSystemMessage(
                getHelpText("/chatimage help", "", "help.chatimage.command")
                        .append(getHelpText("/chatimage send ", "<name> <url>", "send.chatimage.command"))
                        .append(getHelpText("/chatimage url ", "<url>", "url.chatimage.command"))
                        .append(getHelpText("/chatimage reload ", "", "reload.chatimage.command"))
        );
        return Command.SINGLE_SUCCESS;
    }

    public static int reloadConfig(CommandContext<CommandSourceStack> context) {
        CONFIG = ChatImageConfig.loadConfig();
        context.getSource().sendSystemMessage(createTranslatableComponent("success.reload.chatimage.command").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
        return Command.SINGLE_SUCCESS;
    }

    private static MutableComponent getHelpText(String help, String arg, String usage) {
        String all = help + arg;
        StringBuilder sb = new StringBuilder(all);
        if (all.length() <= 35) {
            for (int i = 0; i < 35 - all.length(); i++) {
                sb.append(" ");
            }
        }
        MutableComponent text = (MutableComponent) Component.nullToEmpty(sb.toString());
        MutableComponent info = Component.translatable(usage);
        return text.setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withClickEvent(
                new ClickEvent.SuggestCommand(help)
        )).append(info).append(Component.nullToEmpty("\n"));
    }

}
