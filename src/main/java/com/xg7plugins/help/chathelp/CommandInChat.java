package com.xg7plugins.help.chathelp;

import com.xg7plugins.commands.setup.ICommand;

import java.util.Map;
import java.util.UUID;

public class CommandInChat extends HelpPage {

    private final Map<String, ICommand> commands;

    public CommandInChat(Map<String, ICommand> commands) {
        super("command-in-chat" + UUID.randomUUID());
        this.commands = commands;

        addMessage(new HelpComponent(
                "&m-&6&m------------------&8*&8&m------------------&f&m-",
                null,null
        ));


    }
}
