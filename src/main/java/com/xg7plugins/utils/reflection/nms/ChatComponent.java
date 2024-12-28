package com.xg7plugins.utils.reflection.nms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.reflection.ReflectionClass;
import lombok.Getter;

import java.util.Optional;

@Getter
public class ChatComponent {

    private static final ReflectionClass chatComponentClass = NMSUtil.getNMSClassViaVersion(17, "ChatComponentText", "network.chat.IChatBaseComponent");

    public static final Class<?> iChatClass = NMSUtil.getNMSClassViaVersion(17, "IChatBaseComponent", "network.chat.IChatBaseComponent").getAClass();

    private final String content;
    private final Object chatComponent;

    public ChatComponent(String content) {
        this.content = content;

        this.chatComponent = XG7Plugins.getMinecraftVersion() < 17 ?
                chatComponentClass.getConstructor(String.class).newInstance(content).getObject() :
                Optional.of(chatComponentClass.getMethod("a", String.class).invoke(content));
    }

    public static ChatComponent of(String content) {
        return new ChatComponent(content);
    }

}
