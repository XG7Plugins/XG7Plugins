package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.libs.xg7geyserforms.builders.SimpleFormCreator;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;

public class LangForm {

    public static void create(Player player) {

        XG7Plugins plugin = XG7Plugins.getInstance();

        if (plugin.getFormManager().contaninsForm("lang", player)) {
            plugin.getFormManager().sendPlayerForm("lang:" + player.getUniqueId(), player);
            return;
        }

        SimpleFormCreator formCreator = new SimpleFormCreator("lang",plugin);

        plugin.getLangManager().loadAllLangs();

        Config config = plugin.getConfigsManager().getConfig("config");

        formCreator.title("lang:[lang-menu.title]");
        formCreator.content("lang:[lang-menu.content]");


        plugin.getLangManager().getLangs().asMap().forEach((s, c)-> {

            PlayerLanguage language = plugin.getLangManager().getPlayerLanguageDAO().getLanguage(player.getUniqueId());


            boolean selected = language != null && language.getLangId().equals(s);

            String[] icon = c.getString("bedrock-icon").split(", ");

            if (icon.length == 1) {
                formCreator.addButton(ButtonComponent.of(c.getString("formated-name") != null ? selected ? "§a" + c.getString("formated-name") : "§8" + c.getString("formated-name") : selected ? "§a" + s : "§8" + s));
                return;
            }
            formCreator.addButton(
                    ButtonComponent.of(
                            c.getString("formated-name") != null ? selected ? "§a" + c.getString("formated-name") : "§8" + c.getString("formated-name") : selected ? "§a" + s : "§8" + s,
                            FormImage.Type.valueOf(icon[0]),
                            icon[1]
                    )
            );



        });

        formCreator.onFinish((form, res) -> {

                    PlayerLanguage language = plugin.getLangManager().getPlayerLanguageDAO().getLanguage(player.getUniqueId());

                    SimpleFormResponse response = (SimpleFormResponse) res;

                    String lang = plugin.getLangManager().getLangs().asMap().keySet().toArray(new String[0])[response.clickedButtonId()];
                    if (language != null && language.getLangId().equals(lang)) {
                        Text.formatComponent("lang:[lang-menu.already-selected]", plugin).send(player);
                        return;
                    }
                    LangMenu.cooldownToToggle.putIfAbsent(player.getUniqueId(), 0L);

                    if (LangMenu.cooldownToToggle.get(player.getUniqueId()) >= System.currentTimeMillis()) {
                        Text.formatComponent("lang:[lang-menu.cooldown-to-toggle]", plugin)
                                .replace("[MILLISECONDS]", String.valueOf((LangMenu.cooldownToToggle.get(player.getUniqueId()) - System.currentTimeMillis())))
                                .replace("[SECONDS]", String.valueOf((int) ((LangMenu.cooldownToToggle.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000)))
                                .replace("[MINUTES]", String.valueOf((int) ((LangMenu.cooldownToToggle.get(player.getUniqueId()) - System.currentTimeMillis()) / 60000)))
                                .replace("[HOURS]", String.valueOf((int) ((LangMenu.cooldownToToggle.get(player.getUniqueId()) - System.currentTimeMillis()) / 3600000)))
                                .send(player);
                        return;
                    }

                    plugin.getLangManager().getPlayerLanguageDAO().updatePlayerLanguage(lang, player.getUniqueId()).thenAccept(r -> {
                        plugin.getMenuManager().removePlayerFromAll(player);
                        plugin.getFormManager().unregisterCreator("lang", player);
                        create(player);
                        Text.formatComponent("lang:[lang-menu.toggle-success]", plugin).send(player);
                    });
                    plugin.getPlugins().forEach((n, pl) -> pl.getLangManager().getPlayerLanguageDAO().updatePlayerLanguage(lang, player.getUniqueId()));


                    LangMenu.cooldownToToggle.put(player.getUniqueId(), System.currentTimeMillis() + Text.convertToMilliseconds(plugin, config.get("cooldown-to-toggle-lang")));
        }
        );

        plugin.getFormManager().registerCreator(formCreator, player);

        plugin.getFormManager().sendPlayerForm("lang:" + player.getUniqueId(), player);
    }

}
