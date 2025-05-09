package com.xg7plugins.commands.core_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigBoolean;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataDAO;
import com.xg7plugins.modules.xg7geyserforms.XG7GeyserForms;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.MenuHolder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(
        name = "lang",
        description = "Sets the language of the player",
        syntax = "/xg7plugins lang (player, lang)",
        permission = "xg7plugins.command.lang",
        pluginClass = XG7Plugins.class,
        isEnabled = @ConfigBoolean(
                configName = "config",
                path = "lang-enabled"
        )
)
public class LangCommand implements ICommand {

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() == 0) {
            if (!(sender instanceof Player)) {
                Text.fromLang(sender, XG7Plugins.getInstance(), "commands.not-a-player").thenAccept(text -> text.send(sender));
                return;
            }
            if (XG7Plugins.isDependencyEnabled("floodgate") && Config.mainConfigOf(XG7Plugins.getInstance()).get("enable-geyser-forms",Boolean.class).orElse(false)) {
                if (XG7GeyserForms.getInstance().sendForm((Player) sender, "lang-form")) {
                    return;
                }
            }
            XG7Menus.getInstance().getMenu(XG7Plugins.getInstance(), "lang-menu").open((Player) sender);
            return;
        }

        if (!sender.hasPermission("xg7plugins.command.lang.other")) {
            CommandMessages.NO_PERMISSION.send(sender);
            return;
        }

        if (args.len() != 2) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandsConfigurations().syntax());
        }

        OfflinePlayer target = args.get(0, Player.class);
        String lang = XG7Plugins.getInstance().getName() + ":" + args.get(1, String.class);

        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            CommandMessages.PLAYER_NOT_FOUND.send(sender);
            return;
        }

        XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();

        if (!XG7Plugins.getInstance().getLangManager().getLangs().asMap().join().containsKey(lang)) {
            Text.fromLang(sender, XG7Plugins.getInstance(),"lang-not-found").thenAccept(text -> text.send(sender));
            return;
        }

        PlayerDataDAO dao = XG7Plugins.getInstance().getPlayerDataDAO();

        String dbLang = args.get(1, String.class);

        dao.update(new PlayerData(target.getUniqueId(), dbLang)).thenAccept(r -> {
            XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();
            Text.fromLang(sender, XG7Plugins.getInstance(), "lang-menu.toggle-success").thenAccept(text -> text.send(sender));
            if (target.isOnline()) {
                Player targetOnline = target.getPlayer();
                Text.fromLang(targetOnline, XG7Plugins.getInstance(), "lang-menu.toggle-success").thenAccept(text -> text.send(targetOnline));
                    if (targetOnline.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder) {
                        MenuHolder holder = (MenuHolder) targetOnline.getOpenInventory().getTopInventory().getHolder();
                        targetOnline.closeInventory();
                        holder.getMenu().open(targetOnline);
                    }
            }
        });

    }

    public Item getIcon() {
        return Item.commandIcon(XMaterial.WRITABLE_BOOK, this);
    }


}
