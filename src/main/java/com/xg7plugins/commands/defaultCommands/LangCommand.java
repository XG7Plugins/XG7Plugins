package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataDAO;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7menus.menus.holders.MenuHolder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(
        name = "lang",
        description = "Sets the language of the player",
        syntax = "/xg7plugins lang (player, lang)",
        permission = "xg7plugins.command.lang"
)
public class LangCommand implements ICommand {

    @Override
    public boolean isEnabled() {
        return XG7Plugins.getInstance().getConfig("config").get("enable-langs", Boolean.class).orElse(false);
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() == 0) {
            if (!(sender instanceof Player)) {
                Text.formatLang(XG7Plugins.getInstance(), sender, "commands.not-a-player").thenAccept(text -> text.send(sender));
                return;
            }
            if (XG7Plugins.isFloodgate()) {
                if (XG7Plugins.getInstance().getFormManager().sendForm((Player) sender, "lang-form")) return;
            }
            XG7Plugins.getInstance().getMenuManager().getMenu(XG7Plugins.getInstance(), "lang-menu").open((Player) sender);
            return;
        }

        if (!sender.hasPermission("xg7plugins.command.lang.other")) {
            Text.formatLang(XG7Plugins.getInstance(), sender, "commands.no-permission").thenAccept(text -> text.send(sender));
            return;
        }

        if (args.len() != 2) {
            syntaxError(sender, "/lang (player, lang)");
        }

        OfflinePlayer target = args.get(0, Player.class);
        String lang = XG7Plugins.getInstance().getName() + ":" + args.get(1, String.class);

        if (target == null || !target.hasPlayedBefore()) {
            Text.formatLang(XG7Plugins.getInstance(), sender, "commands.player-not-found").thenAccept(text -> text.send(sender));
            return;
        }

        XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();

        if (!XG7Plugins.getInstance().getLangManager().getLangs().asMap().join().containsKey(lang)) {
            Text.formatLang(XG7Plugins.getInstance(),sender,"lang-not-found").thenAccept(text -> text.send(sender));
            return;
        }

        PlayerDataDAO dao = XG7Plugins.getInstance().getPlayerDataDAO();

        String dbLang = args.get(1, String.class);

        dao.update(new PlayerData(target.getUniqueId(), dbLang)).thenAccept(r -> {
            XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();
            Text.formatLang(XG7Plugins.getInstance(), sender, "lang-menu.toggle-success").thenAccept(text -> text.send(sender));
            if (target.isOnline()) {
                Player targetOnline = target.getPlayer();
                Text.formatLang(XG7Plugins.getInstance(), targetOnline, "lang-menu.toggle-success").thenAccept(text -> text.send(targetOnline));
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
