package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.utils.text.Text;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

@Command(
        name = "xg7pluginreload",
        description = "Reloads the plugin",
        syntax = "/xg7pluginreload [<plugin> or <invalidatejsoncache>] <[config, lang, database, events, all]>",
        aliasesPath = "reload",
        perm = "xg7plugins.command.reload"
)
public class ReloadCommand implements ICommand {

    private final ISubCommand[] subCommands = new ISubCommand[]{new JsonSubCommand(), new PluginSubCommand()};

    @Override
    public ItemBuilder getIcon() {
        return ItemBuilder.commandIcon(XMaterial.STONE_BUTTON, this, XG7Plugins.getInstance());
    }
    @Override
    public ISubCommand[] getSubCommands() {
        return subCommands;
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.Command command, CommandSender sender, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(subCommands[1].getOptions());
            suggestions.add("invalidatejsoncache");
        }
        if (args.length == 2) suggestions = Arrays.stream(subCommands[0].getSubCommands()).map(sub -> sub.getClass().getAnnotation(SubCommand.class).name()).collect(Collectors.toList());
        return suggestions;
    }

    static class JsonSubCommand implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, String[] args, String label) {
            XG7Plugins.getInstance().getJsonManager().invalidateCache();
            Text.format("lang:[reload-message.json]", XG7Plugins.getInstance()).send(sender);
        }

        @Override
        public ItemBuilder getIcon() {
            return ItemBuilder.subCommandIcon(XMaterial.PAPER, this, XG7Plugins.getInstance());
        }
    }

    @Data
    static class PluginSubCommand implements ISubCommand {

        private Set<String> plugins = new HashSet<>();
        private final ISubCommand[] subCommands = new ISubCommand[]{new ConfigSubCommand(), new LangSubCommand(), new DatabaseSubCommand(), new EventSubCommand(), new AllSubCommand()};

        @Override
        public ISubCommand[] getSubCommands() {
            return subCommands;
        }

        @Override
        public Set<String> getOptions() {
            plugins.addAll(XG7Plugins.getInstance().getPlugins().keySet());
            plugins.add("XG7Plugins");
            return plugins;
        }

        @Override
        public ItemBuilder getIcon() {
            return ItemBuilder.subCommandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance());
        }

        static class ConfigSubCommand implements ISubCommand {

            @Override
            public ItemBuilder getIcon() {
                return ItemBuilder.subCommandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance());
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {

                Plugin plugin = args[0].equals("XG7Plugins") ? XG7Plugins.getInstance() : XG7Plugins.getInstance().getPlugins().get(args[0]);
                plugin.getConfigsManager().getConfigs().values().forEach(Config::reload);

                Text.format("lang:[reload-message.config]", XG7Plugins.getInstance())
                        .replace("[PLUGIN]", plugin.getName())
                        .send(sender);
            }
        }

        static class DatabaseSubCommand implements ISubCommand {

            @Override
            public ItemBuilder getIcon() {
                return ItemBuilder.subCommandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance());
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {


                Plugin plugin = args[0].equals("XG7Plugins") ? XG7Plugins.getInstance() : XG7Plugins.getInstance().getPlugins().get(args[0]);
                XG7Plugins.getInstance().getDatabaseManager().disconnectPlugin(plugin);
                XG7Plugins.getInstance().getDatabaseManager().connectPlugin(plugin);

                Text.format("lang:[reload-message.database]", XG7Plugins.getInstance())
                        .replace("[PLUGIN]", plugin.getName())
                        .send(sender);
            }
        }

        static class LangSubCommand implements ISubCommand {

            @Override
            public ItemBuilder getIcon() {
                return ItemBuilder.subCommandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance());
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = args[0].equals("XG7Plugins") ? XG7Plugins.getInstance() : XG7Plugins.getInstance().getPlugins().get(args[0]);
                plugin.getLangManager().loadAllLangs();
                Text.format("lang:[reload-message.lang]", XG7Plugins.getInstance())
                        .replace("[PLUGIN]", plugin.getName())
                        .send(sender);
            }
        }

        static class EventSubCommand implements ISubCommand {
            @Override
            public ItemBuilder getIcon() {
                return ItemBuilder.subCommandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance());
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = args[0].equals("XG7Plugins") ? XG7Plugins.getInstance() : XG7Plugins.getInstance().getPlugins().get(args[0]);
                XG7Plugins.getInstance().getEventManager().unregisterEvents(plugin);
                XG7Plugins.getInstance().getEventManager().registerPlugin(plugin);
                XG7Plugins.getInstance().getPacketEventManager().unregisterPlugin(plugin);
                XG7Plugins.getInstance().getPacketEventManager().registerPlugin(plugin);
                Text.format("lang:[reload-message.events]", XG7Plugins.getInstance())
                        .replace("[PLUGIN]", plugin.getName())
                        .send(sender);;
            }
        }

        static class AllSubCommand implements ISubCommand {

            @Override
            public ItemBuilder getIcon() {
                return ItemBuilder.subCommandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance());
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {

                XG7Plugins xg7Plugins = XG7Plugins.getInstance();

                if (args[0].equals("XG7Plugins")) {

                    xg7Plugins.getConfigsManager().getConfigs().values().forEach(Config::reload);
                    xg7Plugins.getDatabaseManager().disconnectPlugin(xg7Plugins);
                    xg7Plugins.getDatabaseManager().connectPlugin(xg7Plugins);
                    xg7Plugins.getLangManager().loadAllLangs();
                    xg7Plugins.getEventManager().unregisterEvents(xg7Plugins);
                    xg7Plugins.getEventManager().registerPlugin(xg7Plugins);
                    Text.format("lang:[reload-message.all]", XG7Plugins.getInstance())
                            .replace("[PLUGIN]", XG7Plugins.getInstance().getName())
                            .send(sender);
                    return;
                }

                Plugin plugin = xg7Plugins.getPlugins().get(args[0]);
                Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {
                    Bukkit.getPluginManager().disablePlugin(plugin);
                    Bukkit.getPluginManager().enablePlugin(plugin);
                    Text.format("lang:[reload-message.all]", XG7Plugins.getInstance())
                            .replace("[PLUGIN]", plugin.getName())
                            .send(sender);
                });
            }
        }

    }


}
