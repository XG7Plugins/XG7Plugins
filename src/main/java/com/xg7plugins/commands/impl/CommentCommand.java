package com.xg7plugins.commands.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.http.HTTP;
import com.xg7plugins.utils.http.HTTPResponse;
import com.xg7plugins.utils.text.Text;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@CommandSetup(
        name = "comment",
        description = "Sends a comment of a plugin to creator",
        syntax = "/comment <plugin> message",
        permission = "xg7plugins.comment",
        pluginClass = XG7Plugins.class,
        isAsync = true
)
public class CommentCommand implements Command {
    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.DIAMOND, this);
    }

    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 2) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        Plugin plugin = args.get(0, Plugin.class);

        if (plugin == null) {
            return CommandState.error("plugin-not-found");
        }

        String message = Strings.join(Arrays.asList(Arrays.copyOfRange(args.getArgs(), 1, args.len())), ' ');

        String webhookUrl = "https://discord.com/api/webhooks/1422772152062447617/1xLhoUeaXAY46pNNSdqKSDQQ3rT7ABTE1PAlaIitSL1m-in08JhV3kJuEuqOh0d4jZ-G";

        // JSON no mesmo formato do fetch
        String jsonBody = "{\n" +
                "  \"content\": \"Uma mensagem do nosso maravilhoso %name% chegou! \uD83C\uDF1F\",\n" +
                "  \"embeds\": [\n" +
                "    {\n" +
                "      \"title\": \"🚀 Mensagem de: %name% Plugin: %plugin%\",\n" +
                "      \"description\": \"IP|PORTA: %ip%\\nMensagem: %message%\",\n" +
                "      \"color\": 65535,\n" +
                "      \"footer\": {\n" +
                "        \"text\": \"<3\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"attachments\": []\n" +
                "}";

        jsonBody = jsonBody.replace("%name%", sender.getName());
        jsonBody = jsonBody.replace("%plugin%", plugin.getName() + " " + plugin.getDescription().getVersion());
        jsonBody = jsonBody.replace("%message%", message);
        jsonBody = jsonBody.replace("%ip%", plugin.getServer().getIp() + ":" + plugin.getServer().getPort());

        try {
            HTTP.post(
                    webhookUrl,
                    jsonBody,
                    Collections.singletonList(new Pair<>("Content-Type", "application/json"))
            );

            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "comment-message-sent");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return CommandState.FINE;
    }


    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 1) return new ArrayList<>(XG7PluginsAPI.getAllXG7PluginsNames());

        return Collections.singletonList("Message");
    }
}
