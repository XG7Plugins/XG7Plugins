package com.xg7plugins.commands.core_commands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.ComponentBuilder;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.TextCentralizer;
import com.xg7plugins.utils.text.component.TextComponent;
import com.xg7plugins.utils.text.component.events.HoverEvent;
import com.xg7plugins.utils.text.component.events.action.HoverAction;
import com.xg7plugins.utils.text.component.sender.ActionBarSender;
import com.xg7plugins.utils.text.component.sender.CenterSender;
import com.xg7plugins.utils.text.component.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;

@CommandSetup(
        name = "test", description = "", syntax = "", pluginClass = XG7Plugins.class
)
public class TestCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        TextComponent component = new TextComponent("§cAAAA Q LEGAU Bugs??????/ ");

        component.addComponent(ComponentBuilder.builder("Hover").hoverEvent(HoverEvent.of(HoverAction.SHOW_TEXT, "LEGAU")).build());

        component.addComponent(ComponentBuilder.builder(" EAI FUNFOU?").build());

        component.setSender(new CenterSender(TextCentralizer.PixelsSize.CHAT.getPixels()));


        try {
            component.send(sender);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Text.format("[CENTER:CHAT] &cSou legau <click:suggest_command:Comando>Click aqui</click> <hover:show_text:mesmo n>Aqui não</hover> <click:suggest_command:aaaaa><hover:show_text:aaaaaaaaa>Nem aqui</hover></click>").send(sender);
    }

    @Override
    public Item getIcon() {
        return null;
    }
}
