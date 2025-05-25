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

/**
 * This class handles interactive conversations with players in a Bukkit/Spigot plugin.
 * It provides a builder-style API to create and manage sequential prompts for user input.
 */
public class Conversation {

    private HashMap<Integer, ?> results = new HashMap<>();
    private ConversationFactory factory;
    private Plugin plugin;
    private String errorMessage;

    private Consumer<HashMap<Integer, ?>> onConversationFinish;

    private List<Prompt> prompts = new ArrayList<>();

    /**
     * Creates a new Conversation instance.
     *
     * @param plugin The plugin instance that owns this conversation
     */
    public Conversation(Plugin plugin) {
        factory = new ConversationFactory(plugin);
        this.plugin = plugin;

        factory.withLocalEcho(false)
                .withModality(false);
    }

    /**
     * Factory method to create a new Conversation instance.
     *
     * @param plugin The plugin instance that owns this conversation
     * @return A new Conversation instance
     */
    public static Conversation create(Plugin plugin) {
        return new Conversation(plugin);
    }

    /**
     * Sets the error message to be displayed when input validation fails.
     *
     * @param errorMessage The error message to display
     * @return This conversation instance for method chaining
     */
    public Conversation errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    /**
     * Sets the timeout duration for this conversation.
     *
     * @param seconds The number of seconds before the conversation times out
     * @return This conversation instance for method chaining
     */
    public Conversation timeOut(int seconds) {
        factory.withTimeout(seconds);
        return this;
    }

    /**
     * Adds a new prompt to the conversation.
     *
     * @param prompt The text to display to the user
     * @param result The parser to handle and validate the user's input
     * @return This conversation instance for method chaining
     */
    public Conversation addPrompt(String prompt, Parser result) {

        int id = prompts.size();

        prompts.add(new Prompt() {

            @NotNull
            @Override
            public String getPromptText(@NotNull ConversationContext conversationContext) {
                return Text.detectLangs((CommandSender) conversationContext.getForWhom(),plugin, prompt).join().textFor((Player) conversationContext.getForWhom()).getPlainText();
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
                    Text.detectLangs(player,plugin, errorMessage).join().send(player);
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

    /**
     * Sets a word that will cancel the conversation when typed.
     *
     * @param word The word that will trigger conversation cancellation
     * @return This conversation instance for method chaining
     */
    public Conversation cancelWord(String word) {
        factory.withEscapeSequence(word);
        return this;
    }

    /**
     * Sets a listener for when the conversation is abandoned.
     *
     * @param event The consumer to handle the abandon event
     * @return This conversation instance for method chaining
     */
    public Conversation onAbandon(Consumer<ConversationAbandonedEvent> event) {
        factory.addConversationAbandonedListener(event::accept);
        return this;
    }

    /**
     * Sets a callback for when the conversation completes successfully.
     *
     * @param onConversationFinish The consumer to handle the conversation results
     * @return This conversation instance for method chaining
     */
    public Conversation onFinish(Consumer<HashMap<Integer, ?>> onConversationFinish) {
        this.onConversationFinish = onConversationFinish;
        return this;
    }

    /**
     * Gets the result value for a specific prompt by its ID.
     *
     * @param id  The ID of the prompt result to retrieve
     * @param <T> The expected type of the result
     * @return The result value cast to the expected type
     */
    public <T> T get(int id) {
        return (T) results.get(id);
    }

    /**
     * Starts the conversation with a player.
     *
     * @param player The player to start the conversation with
     */
    public void start(Player player) {
        factory.withFirstPrompt(prompts.get(0));
        factory.buildConversation(player).begin();
    }




}
