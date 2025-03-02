package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataDAO;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.cumulus.util.FormImage;

import java.util.ArrayList;
import java.util.List;

public class LangForm extends SimpleForm {

    public LangForm() {
        super("lang-form", "lang:[lang-menu.title]", XG7Plugins.getInstance());
    }

    @Override
    public String content(Player player) {
        return "lang:[lang-menu.content]";
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> components = new ArrayList<>();

        XG7Plugins.getInstance().getLangManager().loadLangsFrom(plugin).join();

        XG7Plugins.getInstance().getLangManager().getLangs().asMap().join().entrySet().stream().filter(entry -> entry.getKey().contains("XG7Plugins")).forEach((map)-> {

            PlayerData language = XG7Plugins.getInstance().getPlayerDataDAO().get(player.getUniqueId()).join();

            boolean selected = language != null && language.getLangId().equals(map.getKey().contains(":") ? map.getKey().split(":")[1] : map.getKey());

            String[] icon = map.getValue().get("bedrock-icon",String.class).orElse("").split(", ");

            if (icon.length == 1) {
                components.add(ButtonComponent.of(map.getValue().get("formated-name",String.class).orElse(null) != null ? selected ? "§a" + map.getValue().get("formated-name",String.class).orElse(null) : "§8" + map.getValue().get("formated-name",String.class).orElse(null) : selected ? "§a" + map.getKey() : "§8" + map.getKey()));
                return;
            }
            components.add(
                    ButtonComponent.of(
                            map.getValue().get("formated-name",String.class).orElse(null) != null ? selected ? "§a" + map.getValue().get("formated-name",String.class).orElse(null) : "§8" + map.getValue().get("formated-name",String.class).orElse(null) : selected ? "§a" + map.getKey() : "§8" + map.getKey(),
                            FormImage.Type.valueOf(icon[0]),
                            icon[1]
                    )
            );

        });


        return components;
    }

    @Override
    public boolean isEnabled() {
        Config config = XG7Plugins.getInstance().getConfigsManager().getConfig("config");
        return config.get("enable-langs", Boolean.class).orElse(false) && config.get("enable-lang-form", Boolean.class).orElse(false);
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {

        XG7Plugins.getInstance().getLangManager().loadLangsFrom(plugin).thenRun(() -> XG7Plugins.getInstance().getPlayerDataDAO().get(player.getUniqueId()).thenAccept(language -> {

            String lang = XG7Plugins.getInstance().getLangManager().getLangs().asMap().join().keySet().toArray(new String[0])[result.clickedButtonId()];
            if (language != null && language.getLangId().equals(lang)) {
                Text.fromLang(player, plugin, "lang-menu.already-selected").thenAccept(text -> text.send(player));
                return;
            }
            if (XG7Plugins.getInstance().getCooldownManager().containsPlayer("lang-change", player)) {

                double cooldownToToggle = XG7Plugins.getInstance().getCooldownManager().getReamingTime("lang-change", player);

                Text.fromLang(player, plugin, "lang-menu.cooldown-to-toggle").thenAccept(
                        text -> text.replace("milliseconds", String.valueOf((cooldownToToggle)))
                                .replace("seconds", String.valueOf((int) ((cooldownToToggle) / 1000)))
                                .replace("minutes", String.valueOf((int) ((cooldownToToggle) / 60000)))
                                .replace("hours", String.valueOf((int) ((cooldownToToggle) / 3600000)))
                                .send(player)
                );

                return;
            }

            PlayerDataDAO dao = XG7Plugins.getInstance().getPlayerDataDAO();

            PlayerData data = dao.get(player.getUniqueId()).join();

            String dbLang = lang.split(":")[1];

            data.setLangId(dbLang);

            dao.update(data).thenAccept(r -> {
                XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();
                Text.fromLang(player, plugin, "lang-menu.toggle-success").thenAccept(text -> text.send(player));
                send(player);
            });

            XG7Plugins.getInstance().getCooldownManager().addCooldown(player, "lang-change", XG7Plugins.getInstance().getConfig("config").getTime("cooldown-to-toggle-lang").orElse(5000L));

        }));
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {}

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {}

}
