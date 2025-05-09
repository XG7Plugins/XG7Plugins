package com.xg7plugins.boot;

import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.help.chathelp.HelpInChat;
import com.xg7plugins.help.formhelp.HelpCommandForm;
import com.xg7plugins.help.guihelp.HelpCommandGUI;
import com.xg7plugins.utils.Debug;
import lombok.Data;

@Data
public abstract class PluginContext {

    private ConfigManager configsManager;
    private CommandManager commandManager;
    private Debug debug;

    private HelpCommandGUI helpCommandGUI;
    private HelpInChat helpInChat;
    private HelpCommandForm helpCommandForm;


}
