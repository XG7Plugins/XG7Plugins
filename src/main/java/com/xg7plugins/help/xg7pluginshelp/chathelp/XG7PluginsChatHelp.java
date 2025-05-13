package com.xg7plugins.help.xg7pluginshelp.chathelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.help.chat.HelpChat;

public class XG7PluginsChatHelp extends HelpChat {

    public XG7PluginsChatHelp() {
        super(XG7Plugins.getInstance(), new Index());

        registerPage(new AboutPage());
    }


}
