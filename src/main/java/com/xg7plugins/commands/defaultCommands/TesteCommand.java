package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.libs.xg7holograms.HologramBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.libs.xg7npcs.npcs.NPC1_17_1_XX;
import com.xg7plugins.utils.Conversation;
import com.xg7plugins.utils.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Command(
        name = "teste",
        description = "Teste",
        syntax = "/teste",
        aliasesPath = "teste",
        perm = "xg7plugins.command.teste",
        isOnlyPlayer = true
)
public class TesteCommand implements ICommand {


    @Override
    public ISubCommand[] getSubCommands() {
        return new ISubCommand[]{new Conversation(), new Hologram(), new NPC()};
    }

    @Override
    public ItemBuilder getIcon() {
        return null;
    }

    @SubCommand(
            name = "conversation",
            description = "Cria uma conversa",
            syntax = "/teste conversation",
            perm = "",
            type = SubCommandType.NORMAL,
            isOnlyPlayer = true
    )
    static class Conversation implements ISubCommand {

        @Override
        public ItemBuilder getIcon() {
            return null;
        }

        public void onSubCommand(CommandSender sender, String[] args, String label) {
            Player player = (Player) sender;
            com.xg7plugins.utils.Conversation
                    .create(XG7Plugins.getInstance())
                    .errorMessage("§cVocê digitou um valor inválido")
                    .addPrompt("Digite um número", com.xg7plugins.utils.Conversation.ResultType.INTEGER)
                    .addPrompt("Digite um texto", com.xg7plugins.utils.Conversation.ResultType.STRING)
                    .cancelWord("cancelar")
                    .onAbandon(conversationAbandonedEvent -> {
                        if (conversationAbandonedEvent.gracefulExit()) return;
                        player.sendMessage("§cVocê abandonou a conversa");
                    })
                    .onFinish(conversationResult -> {
                        player.sendMessage("§aNúmero: " + conversationResult.get(0));
                        player.sendMessage("§aTexto: " + conversationResult.get(1));
                    })
                    .start(player);
        }
    }
    @SubCommand(
            name = "hologram",
            description = "Cria um holograma",
            syntax = "/teste hologram",
            perm = "",
            type = SubCommandType.NORMAL,
            isOnlyPlayer = true
    )
    static class Hologram implements ISubCommand {

        @Override
        public ItemBuilder getIcon() {
            return null;
        }

        public void onSubCommand(CommandSender sender, String[] args, String label) {
            XG7Plugins.getInstance().getHologramsManager().initTask();
            HologramBuilder
                    .creator(XG7Plugins.getInstance())
                    .setLocation(Location.fromPlayer((Player) sender))
                    .addLine("§aTeste")
                    .addLine("lang:[formated-name]")
                    .addLine("§cTeste")
                    .build();
        }
    }
    @SubCommand(
            name = "npc",
            description = "Cria um NPC",
            syntax = "/teste npc",
            perm = "",
            type = SubCommandType.NORMAL,
            isOnlyPlayer = true
    )
    static class NPC implements ISubCommand {

        @Override
        public ItemBuilder getIcon() {
            return null;
        }

        public void onSubCommand(CommandSender sender, String[] args, String label) {
            NPC1_17_1_XX npc = new NPC1_17_1_XX(XG7Plugins.getInstance(), Arrays.asList("§aTeste"), Location.fromPlayer((Player) sender));
            npc.spawn((Player) sender);
        }
    }
}
