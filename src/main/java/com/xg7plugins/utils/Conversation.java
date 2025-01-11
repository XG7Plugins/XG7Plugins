package com.xg7plugins.utils;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Conversation {

    private HashMap<Integer, ?> results = new HashMap<>();
    private ConversationFactory factory;
    private Plugin plugin;
    private String errorMessage;

    private Consumer<HashMap<Integer, ?>> onConversationFinish;

    private List<Prompt> prompts = new ArrayList<>();

    public Conversation(Plugin plugin) {
        factory = new ConversationFactory(plugin);
        this.plugin = plugin;

        factory.withLocalEcho(false)
                .withModality(false);
    }

    public static Conversation create(Plugin plugin) {
        return new Conversation(plugin);
    }

    public Conversation errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
    public Conversation timeOut(int seconds) {
        factory.withTimeout(seconds);
        return this;
    }

    public Conversation addPrompt(String prompt, Parser result) {

        int id = prompts.size();

        prompts.add(new Prompt() {

            @NotNull
            @Override
            public String getPromptText(@NotNull ConversationContext conversationContext) {
                return Text.detectLangOrText(plugin, (CommandSender) conversationContext.getForWhom(), prompt).join().getTextFor((Player) conversationContext.getForWhom());
            }

            @Override
            public boolean blocksForInput(@NotNull ConversationContext conversationContext) {
                return true;
            }

            @Nullable
            @Override
            public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
                try {
                    results.put(id, result.convert(s));
                } catch (Exception e) {
                    Player player = (Player) conversationContext.getForWhom();
                    Text.detectLangOrText(plugin, player, errorMessage).join().toComponent().send(player);
                    return this;
                }

                if (prompts.size() == id + 1) {
                    if (onConversationFinish != null) {
                        onConversationFinish.accept(results);
                    }
                    return Prompt.END_OF_CONVERSATION;
                }

                return prompts.get(id + 1);
            }
        });
        return this;
    }

    public Conversation cancelWord(String word) {
        factory.withEscapeSequence(word);
        return this;
    }

    public Conversation onAbandon(Consumer<ConversationAbandonedEvent> event) {
        factory.addConversationAbandonedListener(event::accept);
        return this;
    }

    public Conversation onFinish(Consumer<HashMap<Integer, ?>> onConversationFinish) {
        this.onConversationFinish = onConversationFinish;
        return this;
    }

    public <T> T get(int id) {
        return (T) results.get(id);
    }

    public void start(Player player) {
        factory.withFirstPrompt(prompts.get(0));
        factory.buildConversation(player).begin();
    }




}
