package com.xg7plugins.commands.core_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.data.config.section.ConfigBoolean;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.modules.xg7geyserforms.XG7GeyserForms;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.MenuHolder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandSetup(
        name = "lang",
        description = "Sets the language of the player",
        syntax = "/xg7plugins lang (player, lang)",
        permission = "xg7plugins.command.lang",
        pluginClass = XG7Plugins.class,
        isAsync = true,
        isEnabled = @ConfigBoolean(
                configName = "config",
                path = "lang-enabled"
        )
)
public class LangCommand implements Command {

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() == 0) {
            if (!(sender instanceof Player)) {
                CommandMessages.NOT_A_PLAYER.send(sender);
                return;
            }
            if (XG7PluginsAPI.isGeyserFormsEnabled() && XG7GeyserForms.getInstance().sendForm((Player) sender, "lang-form"))
                return;

            XG7Menus.getInstance().getMenu(XG7Plugins.getInstance(), "lang-menu").open((Player) sender);
            return;
        }

        if (!sender.hasPermission("xg7plugins.command.lang.other")) {
            CommandMessages.NO_PERMISSION.send(sender);
            return;
        }

        if (args.len() != 2) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandSetup().syntax());
            return;
        }

        OfflinePlayer target = args.get(0, Player.class);
        String lang = XG7Plugins.getInstance().getName() + ":" + args.get(1, String.class);

        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            CommandMessages.PLAYER_NOT_FOUND.send(sender);
            return;
        }

        XG7PluginsAPI.langManager().loadLangsFrom(XG7Plugins.getInstance()).join();

        if (!XG7PluginsAPI.langManager().hasLang(lang)) {
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "lang-not-found");
            return;
        }

        PlayerDataRepository dao = XG7PluginsAPI.getRepository(PlayerDataRepository.class);

        String dbLang = args.get(1, String.class);

        dao.update(new PlayerData(target.getUniqueId(), dbLang));

        XG7PluginsAPI.langManager().loadLangsFrom(XG7Plugins.getInstance()).join();
        Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "lang-menu.toggle-success");
        if (!target.isOnline()) return;

        Player targetOnline = target.getPlayer();
        Text.sendTextFromLang(targetOnline, XG7Plugins.getInstance(), "lang-menu.toggle-success");

        if (targetOnline.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder) {
            MenuHolder holder = (MenuHolder) targetOnline.getOpenInventory().getTopInventory().getHolder();
            holder.getMenu().open(targetOnline);
        }

    }

    public Item getIcon() {
        return Item.commandIcon(XMaterial.WRITABLE_BOOK, this);
    }


}
