package com.xg7plugins.plugin_menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.utils.Pair;
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

        XG7PluginsAPI.langManager().loadLangsFrom(plugin).join();

        XG7PluginsAPI.langManager().getLangs().asMap().join().entrySet().stream().filter(entry -> entry.getKey().contains("XG7Plugins")).forEach((map)-> {

            PlayerData language = XG7PluginsAPI.getRepository(PlayerDataRepository.class).get(player.getUniqueId());

            boolean selected = language != null && language.getLangId().equals(map.getKey().contains(":") ? map.getKey().split(":")[1] : map.getKey());

            String[] icon = map.getValue().get("bedrock-icon",String.class).orElse("").split(", ");

            String formattedName = map.getValue().get("formated-name",String.class).orElse(null) != null ? selected ? "&a" + map.getValue().get("formated-name",String.class).orElse(null) : "&8" + map.getValue().get("formated-name",String.class).orElse(null) : selected ? "&a" + map.getKey() : "&8" + map.getKey();

            if (icon.length == 1) {
                components.add(ButtonComponent.of(formattedName));
                return;
            }
            components.add(
                    ButtonComponent.of(
                            formattedName,
                            FormImage.Type.valueOf(icon[0]),
                            icon[1]
                    )
            );

        });


        return components;
    }

    @Override
    public boolean isEnabled() {
        Config config = Config.mainConfigOf(XG7Plugins.getInstance());
        return config.get("lang-enabled", Boolean.class).orElse(false) && config.get("enable-lang-form", Boolean.class).orElse(false);
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {

        XG7PluginsAPI.langManager().loadLangsFrom(plugin).thenRun(() -> {

            PlayerData data = XG7PluginsAPI.getRepository(PlayerDataRepository.class).get(player.getUniqueId());

            if (data == null) return;

            String lang = XG7PluginsAPI.langManager().getLangs().asMap().join().keySet().toArray(new String[0])[result.clickedButtonId()];
            if (data.getLangId().equals(lang)) {
                Text.fromLang(player, plugin, "lang-menu.already-selected").thenAccept(text -> text.send(player));
                return;
            }
            if (XG7PluginsAPI.cooldowns().containsPlayer("lang-change", player)) {

                long cooldownToToggle = XG7PluginsAPI.cooldowns().getReamingTime("lang-change", player);

                Text.sendTextFromLang(player, plugin, "lang-menu.cooldown-to-toggle", Pair.of("time", String.valueOf((cooldownToToggle))));

                return;
            }

            PlayerDataRepository dao = XG7PluginsAPI.getRepository(PlayerDataRepository.class);

            String dbLang = lang.split(":")[1];

            data.setLangId(dbLang);

            dao.update(data);
            XG7PluginsAPI.langManager().loadLangsFrom(XG7Plugins.getInstance()).join();
            Text.sendTextFromLang(player, plugin, "lang-menu.toggle-success");
            send(player);


            XG7PluginsAPI.cooldowns().addCooldown(player, "lang-change", Config.mainConfigOf(plugin).getTimeInMilliseconds("cooldown-to-toggle-lang").orElse(5000L));


        });
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {}

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {}

}
