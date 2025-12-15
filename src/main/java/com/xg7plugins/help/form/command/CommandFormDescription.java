package com.xg7plugins.help.form.command;

import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.form.HelpForm;
import com.xg7plugins.modules.xg7geyserforms.forms.ModalForm;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.ArrayList;
import java.util.UUID;

public class CommandFormDescription extends ModalForm {

    private final CommandForm origin;
    private final HelpForm guiOrigin;
    private final CommandNode command;

    public CommandFormDescription(CommandForm origin, CommandNode command, ItemStack commandIcon, HelpForm guiOrigin) {
        super(
                XG7Plugins.getInstance(),
                "command-desc" + UUID.randomUUID(),
                "Contents of command: " + command.getName(),

                commandIcon.getItemMeta().getLore().get(0) + "\n" +
                        commandIcon.getItemMeta().getLore().get(1) + "\n" +
                        commandIcon.getItemMeta().getLore().get(2) + "\n" +
                        commandIcon.getItemMeta().getLore().get(3),

                command.getChildren().isEmpty() ? "lang:[commands-form.no-subcommands]" : "lang:[commands-form.subcommands-label]",
                "lang:[commands-form.subcommands-back]",
                new ArrayList<>()
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
            if (command.getChildren().isEmpty()) {
                FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
                return;
            }

            CommandForm commandMenu = new CommandForm(command.getChildren(), "Subcommands of: " + command.getName(), origin, guiOrigin);
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
