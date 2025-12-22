package com.xg7plugins.commands.impl;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.dialog.Dialog;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.impl.reload.ReloadCause;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.config.editor.impl.ConversationEditor;
import com.xg7plugins.config.editor.impl.DialogEditor;
import com.xg7plugins.config.editor.impl.FormEditor;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.menus.config.ConfigFileForm;
import com.xg7plugins.menus.config.ConfigFileMenu;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.FileUtil;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import lombok.Data;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CommandSetup(
        name = "configs",
        description = "Base command for configuration management.",
        syntax = "/configs <plugin> [<file> [<edit|reload|save>]]",
        permission = "xg7plugins.config-edit",

        pluginClass = XG7Plugins.class
)
public class ConfigCommand implements Command {

    private final Consumer<ConfigEditTask> configFileConsumer = (task) -> {

        ConfigSection mainConfig = ConfigFile.mainConfigOf(XG7Plugins.getInstance()).section("config-editor");

        boolean enabled = XG7Plugins.getAPI().isGeyserFormsEnabled();

        if (enabled && mainConfig.get("form-editor", false)) {
            if (FloodgateApi.getInstance().isFloodgatePlayer(task.getPlayer().getUniqueId())) {
                new FormEditor(task.getPlayer()).sendPage(task.getFile().root());
                return;
            }
        }

        if (mainConfig.get("dialog-editor", true) && MinecraftServerVersion.isNewerOrEqual(ServerVersion.V_1_21_6)) {
            new DialogEditor(task.getPlayer()).sendPage(task.getFile().root());
            return;
        }

        new ConversationEditor(task.getPlayer()).sendPage(task.getFile().root());
    };


    @CommandConfig(isAsync = true)
    public CommandState root(CommandSender sender, CommandArgs args) throws IOException {

        if (args.len() == 0) {
            return CommandState.SYNTAX_ERROR;
        }

        Plugin plugin = XG7Plugins.getAPI().getXG7Plugin(args.get(0, String.class));
        if (plugin == null) {
            return CommandState.error("plugin-not-found");
        }

        if (args.len() == 1) {

            if (!(sender instanceof Player)) {
                return CommandState.NOT_A_PLAYER;
            }

            Player player = (Player) sender;

            XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of(() -> {
                if (
                        XG7Plugins.getAPI().isGeyserFormsEnabled() &&
                        ConfigFile.mainConfigOf(XG7Plugins.getInstance()).section("config-editor").get("form-config-file-list", false) &&
                        new ConfigFileForm(plugin).send(player)
                )
                    return;

               new ConfigFileMenu(plugin).open((Player) sender);
            }));

            return CommandState.FINE;

        }

        String yamlFileName = args.get(1, String.class).replace(".yml", "");

        if (FileUtil.exists(plugin, yamlFileName)) {
            return CommandState.error("file-not-found");
        }

        ConfigFile configFile = ConfigFile.of(yamlFileName, plugin);

        if (args.len() != 3) {

            if (!(sender instanceof Player)) {
                return CommandState.NOT_A_PLAYER;
            }

            XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of(() -> configFileConsumer.accept(new ConfigEditTask((Player) sender, plugin, configFile))));

            return CommandState.FINE;
        }


        String subCommand = args.get(2, String.class).toLowerCase();

        switch (subCommand.toLowerCase()) {

            case "edit":

                if (!(sender instanceof Player)) {
                    return CommandState.NOT_A_PLAYER;
                }

                XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of(() -> configFileConsumer.accept(new ConfigEditTask((Player) sender, plugin, configFile))));

                break;

            case "reload":

                configFile.reload();

                Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "config-files.config-reloaded",
                        Pair.of("plugin", plugin.getPrefix()),
                        Pair.of("file_name", yamlFileName)
                );
                break;

            case "save":

                configFile.save();

                Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "config-files.config-saved",
                        Pair.of("plugin", plugin.getPrefix()),
                        Pair.of("file_name", yamlFileName)
                );



        }

        return CommandState.FINE;
    }

    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (!sender.hasPermission("xg7plugins.command.config")) return null;

        if (args.len() == 1) {
            return XG7Plugins.getAPI().getAllXG7PluginsNames().stream().filter(pl -> pl.startsWith(args.get(0, String.class))).collect(Collectors.toList());
        }

        Plugin plugin = XG7Plugins.getAPI().getXG7Plugin(args.get(0, String.class));
        if (plugin == null) return null;

        if (args.len() == 2) {

            List<String> yamls = new ArrayList<>();

            Path dataFolder = plugin.getJavaPlugin().getDataFolder().toPath();

            try (Stream<Path> files = Files.walk(dataFolder)) {
                files.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".yml"))
                        .map(p -> dataFolder.relativize(p).toString())
                        .forEach(yamls::add);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return yamls.stream()
                    .filter(yaml -> yaml.toLowerCase().startsWith(args.get(1, String.class).toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.len() == 3) {
            List<String> subCommands = Arrays.asList("edit", "reload", "save");
            return subCommands.stream()
                    .filter(cmd -> cmd.startsWith(args.get(2, String.class).toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }

    @Data
    class ConfigEditTask {
        private final Player player;
        private final Plugin plugin;
        private final ConfigFile file;
    }

}
