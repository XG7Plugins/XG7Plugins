package com.xg7plugins.help.xg7pluginshelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.libs.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.libs.xg7menus.item.BookItem;
import com.xg7plugins.utils.text.Text;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class XG7PluginsHelpForm extends SimpleForm {
    public XG7PluginsHelpForm(Plugin plugin) {
        super("help-command-index", "lang:[help-menu.index.title]", plugin);
    }

    @Override
    public String content(Player player) {

        Config lang = XG7Plugins.getInstance().getLangManager() == null ?
                XG7Plugins.getInstance().getConfig("messages") :
                Config.of(XG7Plugins.getInstance(), XG7Plugins.getInstance().getLangManager().getLangByPlayer(plugin, player).join());

        String about = Text.formatComponent((String) lang.get("help-menu.about", List.class).orElse(new ArrayList<String>()).stream().collect(Collectors.joining("\n")), plugin).getRawText();

        return Text.format(about, plugin)
                .replace("[DISCORD]", "https://discord.gg/jfrn8w92kF")
                .replace("[GITHUB]", "https://github.com/DaviXG7")
                .replace("[WEBSITE]", "https://xg7plugins.com")
                .replace("[VERSION]", XG7Plugins.getInstance().getDescription().getVersion())
                .getWithPlaceholders(player);
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> buttons = new ArrayList<>();
        buttons.add(ButtonComponent.of(Text.format("lang:[help-menu.index.lang-item.name]", plugin).getWithPlaceholders(player)));
        buttons.add(ButtonComponent.of(Text.format("lang:[help-menu.index.tasks-item.name]", plugin).getWithPlaceholders(player)));
        buttons.add(ButtonComponent.of(Text.format("lang:[help-menu.index.about-item.name]", plugin).getWithPlaceholders(player)));
        buttons.add(ButtonComponent.of(Text.format("lang:[help-menu.index.commands-item.name]", plugin).getWithPlaceholders(player)));

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
                player.performCommand("lang");
                break;
            case 1:
                player.performCommand("tasks");
                break;
            case 2:

                Config lang = XG7Plugins.getInstance().getLangManager() == null ?
                        XG7Plugins.getInstance().getConfig("messages") :
                        Config.of(XG7Plugins.getInstance(), XG7Plugins.getInstance().getLangManager().getLangByPlayer(plugin, player).join());

                String about = (String) lang.get("help-menu.about", List.class).orElse(new ArrayList<String>()).stream().collect(Collectors.joining("\n"));

                BookItem bookItem = BookItem.newBook();

                List<BaseComponent[]> pages = new ArrayList<>();
                List<BaseComponent> currentPage = new ArrayList<>();

                for (BaseComponent line : Text.formatComponent(about, XG7Plugins.getInstance())
                        .replace("[DISCORD]", "https://discord.gg/jfrn8w92kF")
                        .replace("[GITHUB]", "https://github.com/DaviXG7")
                        .replace("[WEBSITE]", "https://xg7plugins.com")
                        .replace("[VERSION]", XG7Plugins.getInstance().getDescription().getVersion())
                        .getText(player))
                {
                    currentPage.add(line);
                    if (currentPage.size() == 14) {
                        pages.add(currentPage.toArray(new BaseComponent[0]));
                        currentPage.clear();
                    }
                }
                if (!currentPage.isEmpty()) {
                    pages.add(currentPage.toArray(new BaseComponent[0]));
                }

                for (BaseComponent[] page : pages) {
                    bookItem.addPage(page);
                }

                bookItem.openBook(player);
                return;
            case 3:
                plugin.getHelpCommandForm().getForm("commands").send(player);
                break;
        }
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {
        FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {
        FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
    }
}
