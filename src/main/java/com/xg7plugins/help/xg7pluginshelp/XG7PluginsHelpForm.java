package com.xg7plugins.help.xg7pluginshelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.ArrayList;
import java.util.List;

public class XG7PluginsHelpForm extends SimpleForm {
    public XG7PluginsHelpForm(Plugin plugin) {
        super("help-command-index", "lang:[help-menu.index.title]", plugin);
    }

    @Override
    public String content(Player player) {

        Config lang = XG7Plugins.getInstance().getLangManager().getLangByPlayer(plugin, player).join().getLangConfiguration();

        String about = String.join("\n", lang.getList("help-menu.about", String.class).orElse(new ArrayList<>()));

        return Text.detectLangs(player, XG7Plugins.getInstance(),about).join()
                .replace("[DISCORD]", "discord.gg/jfrn8w92kF")
                .replace("[GITHUB]", "github.com/DaviXG7")
                .replace("[WEBSITE]", "xg7plugins.com")
                .replace("[VERSION]", XG7Plugins.getInstance().getDescription().getVersion())
                .getText();
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> buttons = new ArrayList<>();
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin,"help-menu.index.lang-item.name").join().getText()));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin,"help-menu.index.tasks-item.name").join().getText()));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin,"help-menu.index.commands-item.name").join().getText()));

        return buttons;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {
        switch (result.clickedButtonId()) {
            case 0:
                XG7Plugins.taskManager().runSyncTask(XG7Plugins.getInstance(), () -> player.performCommand("lang"));
                break;
            case 1:
                XG7Plugins.taskManager().runSyncTask(XG7Plugins.getInstance(), () -> player.performCommand("tasks"));
                break;
            case 2:
                plugin.getHelpCommandForm().getForm("commands").send(player);
                break;
        }
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {
        FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {}
}
