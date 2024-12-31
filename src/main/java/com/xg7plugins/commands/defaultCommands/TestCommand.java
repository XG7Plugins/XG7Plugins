package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7holograms.HologramBuilder;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7npcs.NPCBuilder;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Command(
        name = "test",
        description = "test",
        syntax = "test",
        permission = "xg7plugins.commands.test"
)
public class TestCommand implements ICommand {
    @Override
    public Item getIcon() {
        return null;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Text.format("lang:[commands.test]", XG7Plugins.getInstance()).send(sender);

        if (args.get(0, String.class).equals("npc")) {
            XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(XG7Plugins.getInstance().getName() + ":holograms"));

            XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(XG7Plugins.getInstance().getName() + ":npcs"));

            XG7Plugins.getInstance().getNpcManager().addNPC(
                    NPCBuilder.creator(XG7Plugins.getInstance(), "npc1")
                            .setLocation(Location.fromPlayer((Player) sender))
                            .setName("Ola", "hola", "hi", "conitchua")
                            .setHelmet(new ItemStack(Material.DIAMOND_HELMET))
                            .withPlayerSkin(true)
                            .build()
            );
        }

        if (args.get(0, String.class).equals("hologram")) {
            XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(XG7Plugins.getInstance().getName() + ":holograms"));

            XG7Plugins.getInstance().getHologramsManager().addHologram(
                    HologramBuilder.creator(XG7Plugins.getInstance(), "hologram1")
                            .setLocation(Location.fromPlayer((Player) sender))
                            .addLine("Ola")
                            .addLine("hola")
                            .addLine("hi")
                            .addLine("conitchua")
                            .addLine("%player_direction%")
                            .build()
            );
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        List<String> suggestions = new ArrayList<>();

        if (args.len() == 1) {
            Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).forEach(suggestions::add);
        }
        if (args.len() == 2) {
            suggestions.addAll(XG7Plugins.getInstance().getLangManager().getLangs().asMap().keySet());
        }
        return suggestions;
    }


}
