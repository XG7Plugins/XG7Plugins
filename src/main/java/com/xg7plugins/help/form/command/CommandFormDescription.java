package com.xg7plugins.help.form.command;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.help.form.HelpForm;
import com.xg7plugins.modules.xg7geyserforms.forms.ModalForm;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class CommandFormDescription extends ModalForm {

    private final CommandForm origin;
    private final HelpForm guiOrigin;
    private final Command command;

    public CommandFormDescription(CommandForm origin, Command command, ItemStack commandIcon, HelpForm guiOrigin) {
        super(
                "command-desc" + UUID.randomUUID(),
                "Contents of command: " + command.getCommandSetup().name(),
                XG7Plugins.getInstance(),

                commandIcon.getItemMeta().getLore().get(0) + "\n" +
                        commandIcon.getItemMeta().getLore().get(1) + "\n" +
                        commandIcon.getItemMeta().getLore().get(2) + "\n" +
                        commandIcon.getItemMeta().getLore().get(3),

                command.getSubCommands().isEmpty() ? "lang:[commands-form.no-subcommands]" : "lang:[commands-form.subcommands-label]",
                "lang:[commands-form.subcommands-back]"
        );
        this.origin = origin;
        this.command = command;
        this.guiOrigin = guiOrigin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.ModalForm form, ModalFormResponse result, Player player) {
        if (result.clickedFirst()) {
            if (command.getSubCommands().isEmpty()) {
                FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
                return;
            }

            CommandForm commandMenu = new CommandForm(plugin, command.getSubCommands(), "Subcommands of: " + command.getCommandSetup().name(), origin, guiOrigin);
            commandMenu.send(player);
        } origin.send(player);

    }

    @Override
    public void onError(org.geysermc.cumulus.form.ModalForm form, InvalidFormResponseResult<ModalFormResponse> result, Player player) {
        FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
    }

    @Override
    public void onClose(org.geysermc.cumulus.form.ModalForm form, Player player) {
        origin.getGuiOrigin().getForm("index").send(player);
    }

}
