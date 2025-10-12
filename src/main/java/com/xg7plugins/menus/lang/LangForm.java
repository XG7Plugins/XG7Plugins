package com.xg7plugins.menus.lang;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.lang.Lang;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.cumulus.util.FormImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


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

        XG7PluginsAPI.langManager().loadLangsFrom(XG7Plugins.getInstance()).join();

        Set<Map.Entry<String, Lang>> langsSet = XG7PluginsAPI.langManager().getLangs().asMap().join().entrySet();

        langsSet.stream().filter(entry -> entry.getKey().contains("XG7Plugins")).forEach((map)-> {

            PlayerData language = XG7PluginsAPI.getRepository(PlayerDataRepository.class).get(player.getUniqueId());

            boolean selected = language != null && language.getLangId().equals(map.getKey().contains(":") ? map.getKey().split(":")[1] : map.getKey());

            String[] icon = map.getValue().getLangConfiguration().get("bedrock-icon", "").split(", ");

            String formattedName = map.getValue().get("formated-name") != null ? selected ? "&a" + map.getValue().get("formated-name") : "&8" + map.getValue().get("formated-name") : selected ? "&a" + map.getKey() : "&8" + map.getKey();

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
        ConfigSection config = ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root();
        return config.get("lang-enabled", false) && config.get("enable-lang-form", false);
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {

        XG7PluginsAPI.langManager().loadLangsFrom(plugin).thenRun(() -> {

            PlayerData data = XG7PluginsAPI.getRepository(PlayerDataRepository.class).get(player.getUniqueId());

            if (data == null) return;

            Set<String> langsSet = XG7PluginsAPI.langManager().getLangs().asMap().join().keySet();

            String lang = langsSet.stream().filter(l -> l.contains("XG7Plugins")).collect(Collectors.toList()).get(result.clickedButtonId());
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

            XG7Scores.getInstance().removePlayer(player);
            XG7Scores.getInstance().addPlayer(player);

            send(player);



            XG7PluginsAPI.cooldowns().addCooldown(player, "lang-change", ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().getTimeInMilliseconds("cooldown-to-toggle-lang", 5000L));


        });
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {}

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {}

}
