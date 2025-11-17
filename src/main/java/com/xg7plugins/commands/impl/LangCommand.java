package com.xg7plugins.commands.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.config.utils.ConfigCheck;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.modules.xg7geyserforms.XG7GeyserForms;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.menus.menuholders.MenuHolder;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandSetup(
        name = "lang",
        description = "Sets the language of the player",
        syntax = "/xg7plugins lang (player, lang)",
        permission = "xg7plugins.command.lang",
        pluginClass = XG7Plugins.class,
        iconMaterial = XMaterial.WRITABLE_BOOK,
        isEnabled = @ConfigCheck(
                configName = "config",
                path = "lang-enabled"
        )
)
public class LangCommand implements Command {

    @CommandConfig(isAsync = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() == 0) {
            if (!(sender instanceof Player)) {
                return CommandState.NOT_A_PLAYER;
            }
            XG7PluginsAPI.taskManager().runSync(BukkitTask.of(() -> {
                if (XG7PluginsAPI.isGeyserFormsEnabled() &&
                        XG7PluginsAPI.geyserForms().sendForm((Player) sender, "lang-form"))
                    return;

                XG7PluginsAPI.menus().getMenu(XG7Plugins.getInstance(), "lang-menu").open((Player) sender);
            }));

            return CommandState.FINE;
        }

        if (!sender.hasPermission("xg7plugins.command.lang.other")) {
            return CommandState.NO_PERMISSION;
        }

        if (args.len() != 2) {
            return CommandState.SYNTAX_ERROR;
        }

        OfflinePlayer target = args.get(0, Player.class);
        String lang = XG7Plugins.getInstance().getName() + ":" + args.get(1, String.class);

        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            return CommandState.PLAYER_NOT_FOUND;
        }

        XG7PluginsAPI.langManager().loadLangsFrom(XG7Plugins.getInstance()).join();

        if (!XG7PluginsAPI.langManager().hasLang(lang)) {
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "lang-not-found");
            return CommandState.ERROR;
        }

        PlayerDataRepository dao = XG7PluginsAPI.getRepository(PlayerDataRepository.class);
        String dbLang = args.get(1, String.class);

        dao.update(new PlayerData(target.getUniqueId(), dbLang));

        Text.sendTextFromLang(sender, getPlugin(), "lang-other", Pair.of("target", target.getName()), Pair.of("lang", dbLang));

        XG7PluginsAPI.langManager().loadLangsFrom(XG7Plugins.getInstance()).join();

        if (target.isOnline()) {
            Player targetOnline = target.getPlayer();
            XG7PluginsAPI.scores().removePlayer(targetOnline);
            XG7PluginsAPI.scores().addPlayer(targetOnline);

            if (targetOnline.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder) {
                MenuHolder holder = (MenuHolder) targetOnline.getOpenInventory().getTopInventory().getHolder();
                holder.getMenu().open(targetOnline);
            }
            Text.sendTextFromLang(targetOnline, getPlugin(), "lang-changed-by-other", Pair.of("target", sender.getName()), Pair.of("lang", dbLang));
        }


        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (!sender.hasPermission("xg7plugins.command.lang.other")) return null;

        if (args.len() == 1) return new ArrayList<>(XG7PluginsAPI.getAllPlayerNames());

        if (args.len() == 2) return Arrays.asList(XG7PluginsAPI.langManager().getDefLangs());

        return null;
    }

}
