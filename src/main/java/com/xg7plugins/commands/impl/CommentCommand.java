package com.xg7plugins.commands.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.Secrets;
import com.xg7plugins.utils.http.HTTP;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@CommandSetup(
        name = "comment",
        description = "Sends a comment of a plugin to creator",
        syntax = "/comment <plugin> <message>",
        permission = "xg7plugins.comment",
        iconMaterial = XMaterial.DIAMOND,
        pluginClass = XG7Plugins.class
)
public class CommentCommand implements Command {

    private final AtomicLong cooldown = new AtomicLong(System.currentTimeMillis());

    @CommandConfig(isAsync = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 2) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        if (cooldown.get() > System.currentTimeMillis()) {
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "comment-cooldown", Pair.of("time", (cooldown.get() - System.currentTimeMillis()) + ""));
            return CommandState.ERROR;
        }

        Plugin plugin = args.get(0, Plugin.class);

        if (plugin == null) {
            return CommandState.error("plugin-not-found");
        }

        String message = args.toString(1);

        String webhookUrl = Secrets.DISCORD_WEBHOOK_URL;

        String jsonBody = "{\n" +
                "  \"content\": \"Uma mensagem do nosso maravilhoso %name% chegou! \uD83C\uDF1F\",\n" +
                "  \"embeds\": [\n" +
                "    {\n" +
                "      \"title\": \"🚀 Mensagem de: %name% Plugin: %plugin%\",\n" +
                "      \"description\": \"**IP | PORTA:** %ip%\\nMensagem: %message%\",\n" +
                "      \"color\": 65535,\n" +
                "      \"footer\": {\n" +
                "        \"text\": \"<3\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"attachments\": []\n" +
                "}";

        jsonBody = jsonBody.replace("%name%", sender.getName());
        jsonBody = jsonBody.replace("%plugin%", plugin.getName() + " v" + plugin.getVersion());
        jsonBody = jsonBody.replace("%message%", message);
        jsonBody = jsonBody.replace("%ip%", XG7Plugins.getAPI().getServerInfo().getAddress() + ":" + XG7Plugins.getAPI().getServerInfo().getPort());

        try {
            HTTP.post(
                    webhookUrl,
                    jsonBody,
                    Collections.singletonList(new Pair<>("Content-Type", "application/json"))
            );

            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "comment-message-sent");

            cooldown.set(System.currentTimeMillis() + 1000 * 60 * 3);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return CommandState.FINE;
    }


    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 1) return new ArrayList<>(XG7Plugins.getAPI().getAllXG7PluginsNames());

        return Collections.singletonList("Message");
    }
}
