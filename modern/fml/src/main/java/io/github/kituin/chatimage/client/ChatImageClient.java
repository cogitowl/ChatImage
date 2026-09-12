package io.github.kituin.chatimage.client;
import io.github.kituin.chatimage.platform.GameScreens;
import java.nio.file.Path;
import java.io.File;
import java.util.function.Consumer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.kituin.ChatImageCode.ChatImageConfig;
import io.github.kituin.ChatImageCode.ChatImageCodeInstance;
import io.github.kituin.chatimage.integration.ChatImageClientAdapter;
import io.github.kituin.chatimage.integration.ChatImageLogger;
import io.github.kituin.chatimage.command.ChatImageCommand;
import io.github.kituin.chatimage.gui.ConfigScreen;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
public final class ChatImageClient {
    public static final String MOD_ID = "chatimage";
    public static ChatImageConfig CONFIG;
    private static KeyMapping configKey;
    public static void initialize(Path configDir) {
        ChatImageConfig.configFile = configDir.resolve("chatimageconfig.json").toFile();
        CONFIG = ChatImageConfig.loadConfig();
        ChatImageCodeInstance.CLIENT_ADAPTER = new ChatImageClientAdapter();
        ChatImageCodeInstance.LOGGER = new ChatImageLogger();
    }
    public static void registerKeys(Consumer<KeyMapping> register) {
        configKey = new KeyMapping("config.chatimage.key", InputConstants.Type.KEYSYM, 269,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "config")));
        register.accept(configKey);
    }
    public static void tick() {
        if (configKey == null) return;
        while (configKey.consumeClick()) GameScreens.set(Minecraft.getInstance(), new ConfigScreen());
    }
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher
                .register(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("chatimage").executes(ChatImageCommand::help)
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("send")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("name", string())
                                                .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("url", greedyString())
                                                        .executes(ChatImageCommand::sendChatImage)
                                                )
                                        )
                                )
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("url")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("url", greedyString())
                                                .executes(ChatImageCommand::sendChatImage)
                                        )
                                )
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("help")
                                        .executes(ChatImageCommand::help)
                                )
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("reload")
                                        .executes(ChatImageCommand::reloadConfig)
                                )
                );
    }
}
