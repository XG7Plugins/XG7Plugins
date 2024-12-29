package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.data.lang.PlayerLanguageDAO;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(
        name = "lang",
        description = "Sets the language of the player",
        syntax = "/lang (player, lang)",
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
                Text.format("lang:[commands.not-a-player]",XG7Plugins.getInstance()).send(sender);
                return;
            }
            if (XG7Plugins.isFloodgate()) {
                if (XG7Plugins.getInstance().getFormManager().sendForm((Player) sender, "lang-form")) return;
            }
            XG7Plugins.getInstance().getMenuManager().getMenu("lang-menu").open((Player) sender);
            return;
        }

        if (!sender.hasPermission("xg7plugins.command.lang.other")) {
            Text.format("lang:[commands.no-permission]",XG7Plugins.getInstance()).send(sender);
            return;
        }

        if (args.len() != 2) {
            syntaxError(sender, "/lang (player, lang)");
        }

        OfflinePlayer target = args.get(0, Player.class);
        String lang = args.get(1, String.class);

        if (!target.hasPlayedBefore()) {
            Text.format("lang:[commands.player-not-found]",XG7Plugins.getInstance()).send(sender);
            return;
        }

        XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();

        if (!XG7Plugins.getInstance().getLangManager().getLangs().asMap().containsKey(lang)) {
            Text.format("lang:[lang-not-found]",XG7Plugins.getInstance()).send(sender);
            return;
        }

        PlayerLanguageDAO dao = XG7Plugins.getInstance().getLangManager().getPlayerLanguageDAO();

        dao.update(new PlayerLanguage(target.getUniqueId(), lang)).thenAccept(r -> {
            XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();
            Text.formatComponent("lang:[lang-menu.changed]", XG7Plugins.getInstance()).send(sender);
        });

    }

    public Item getIcon() {
        return Item.commandIcon(XMaterial.WRITABLE_BOOK, this);
    }


}
