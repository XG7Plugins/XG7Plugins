package com.xg7plugins.help.form.command;

import com.xg7plugins.commands.executors.MainCommand;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.form.HelpForm;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandForm extends SimpleForm {

    private final Map<String, CommandNode> commands;
    private final CommandForm superForm;

    @Getter
    private final HelpForm guiOrigin;

    public CommandForm(List<CommandNode> commands, String customTitle, CommandForm superForm, HelpForm guiOrigin) {
        super("command-form" + UUID.randomUUID(), customTitle == null ? "Commands" : customTitle, XG7Plugins.getInstance(), new ArrayList<>());

        this.commands = commands.stream().collect(
                Collectors.toMap(
                        CommandNode::getName,
                        command -> command
                )
        );
        this.guiOrigin = guiOrigin;
        this.superForm = superForm;

    }

    @Override
    public String content(Player player) {
        return "lang:[commands-form.content]";
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> buttons = commands.values().stream().filter(cmd -> !(cmd.getCommand() instanceof MainCommand)).map(
                command -> ButtonComponent.of(command.getName())
        ).collect(Collectors.toList());

        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin,"commands-form.back").getText()));

        return buttons;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {
        ButtonComponent clickedButton = buttons(player).get(result.clickedButtonId());

        if (result.clickedButtonId() == buttons(player).size() - 1) {
            guiOrigin.getForm("index").send(player);
            return;
        }
        CommandNode command = commands.get(clickedButton.text());
        if (command == null) {
            guiOrigin.getForm("index").send(player);
            return;
        }
        CommandFormDescription commandDescription = new CommandFormDescription(
                this,
                command,
                Item.commandIcon(command.getCommandMethod() == null ? command.getCommand().getCommandSetup().iconMaterial() : command.getCommandMethod().getAnnotation(CommandConfig.class).iconMaterial(), command)
                        .getItemFor(player, plugin),
                guiOrigin
        );
        commandDescription.send(player);

    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {
        guiOrigin.getForm("index").send(player);
    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {
        guiOrigin.getForm("index").send(player);
    }
}
