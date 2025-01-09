package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7holograms.HologramBuilder;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7npcs.NPCBuilder;
import com.xg7plugins.libs.xg7scores.builder.ScoreBoardBuilder;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
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

        String test = args.get(0, String.class);

        if (test.equals("npc")) {
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

        if (test.equals("hologram")) {
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
        if (test.equals("score")) {
            XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(XG7Plugins.getInstance().getName() + ":score-task"));

            ScoreBoardBuilder.scoreBoard("test-scoreb")
                    .delay(100)
                    .addLine("aaa")
                    .addLine("bbb")
                    .title(Arrays.asList("a", "A", "aa", "&aAA"))
                    .build(XG7Plugins.getInstance());
        }

        if (test.equals("tab")) {
            XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(XG7Plugins.getInstance().getName() + ":score-task"));

            ScoreBoardBuilder.tablist("test-tab")
                    .delay(100)
                    .addHeaderLine("Aqui é o cabeçalho \n mt bom")
                    .addHeaderLine("Aqui é o cabeçALHO \n mt bom")
                    .addFooterLine("Aqui é o rodapé \n mt bom")
                    .addFooterLine("Aqui é o RODAPÉ \n mt bom")
                    .playerPrefix("§a")
                    .build(XG7Plugins.getInstance());
        }
        if (test.equals("boss")) {
            XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(XG7Plugins.getInstance().getName() + ":score-task"));

            ScoreBoardBuilder.bossBar("bb")
                    .delay(100)
                    .progress(50f)
                    .addTitleUpdate("Title")
                    .addTitleUpdate("Title2")
                    .addTitleUpdate("Title3")
                    .addTitleUpdate("Title4")
                    .build(XG7Plugins.getInstance());
        }

        if (test.equals("actionbar")) {
            XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(XG7Plugins.getInstance().getName() + ":score-task"));

            ScoreBoardBuilder.actionBar("test-action")
                    .delay(500)
                    .addTextUpdate("Aqui é o texto")
                    .addTextUpdate("Aqui é o texto2")
                    .addTextUpdate("Aqui é o texto3")
                    .addTextUpdate("Aqui é o texto4")
                    .build(XG7Plugins.getInstance());
        }
        if (test.equals("xp")) {
            XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(XG7Plugins.getInstance().getName() + ":score-task"));

            ScoreBoardBuilder.XPBar("test-xp")
                    .delay(500)
                    .addXP(50, 0.5f)
                    .addXP(10, 1)
                    .build(XG7Plugins.getInstance());
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        List<String> suggestions = new ArrayList<>();

        if (args.len() == 1) {
            Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).forEach(suggestions::add);
        }
        if (args.len() == 2) {
            suggestions.addAll(XG7Plugins.getInstance().getLangManager().getLangs().asMap().join().keySet());
        }
        return suggestions;
    }


}
