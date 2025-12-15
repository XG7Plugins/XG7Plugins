package com.xg7plugins.menus.config;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.editor.impl.ConversationEditor;
import com.xg7plugins.config.editor.impl.DialogEditor;
import com.xg7plugins.config.editor.impl.FormEditor;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class ConfigFileForm extends SimpleForm {

    private final BiConsumer<Player, String> configFileConsumer;

    private final Plugin filesPlugin;

    public ConfigFileForm(Plugin plugin) {
        super(
                "config-files-form-" + plugin.getName(),
                "lang:[config-files.title]",
                XG7Plugins.getInstance(),
                Collections.singletonList(Pair.of("plugin", plugin.getName()))
        );

        this.filesPlugin = plugin;

        this.configFileConsumer = (player, yaml) -> {

            System.out.println("O YMl: " + yaml);

            ConfigFile configFile = new ConfigFile(filesPlugin,  yaml);

            ConfigSection mainConfig = ConfigFile.mainConfigOf(XG7Plugins.getInstance()).section("config-editor");

            boolean enabled = XG7Plugins.getAPI().isGeyserFormsEnabled();

            if (enabled && mainConfig.get("form-editor", false)) {
                if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                    new FormEditor(player).sendPage(configFile.root());
                    return;
                }
            }

            if (mainConfig.get("dialog-editor", true) && MinecraftServerVersion.isNewerOrEqual(ServerVersion.V_1_21_6)) {
                if (!(enabled && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId()))) {
                    new DialogEditor(player).sendPage(configFile.root());
                }
                return;
            }

            new ConversationEditor(player).sendPage(configFile.root());
        };
    }

    @Override
    public String content(Player player) {
        return "lang:[config-files.form-content]";
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> buttons = new ArrayList<>();

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


        yamls.forEach(yaml -> buttons.add(ButtonComponent.of("§r" + yaml, FormImage.Type.URL, "https://cdn-icons-png.flaticon.com/128/2052/2052698.png")));

        return buttons;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {
        configFileConsumer.accept(player, ChatColor.stripColor(result.clickedButton().text().replace("\\", "/").replace(".yml", "")));
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {

    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {

    }
}
