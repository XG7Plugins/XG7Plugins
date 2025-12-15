package com.xg7plugins.config.editor.impl;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.editor.InGameEditor;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.menus.config.ConfigIndexMenu;
import com.xg7plugins.utils.Conversation;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ConversationEditor extends InGameEditor {

    public ConversationEditor(Player player) {
        super(player);
    }

    @Override
    public void sendPage(ConfigSection configSection) {
        this.currentSection = configSection;
        new ConfigIndexMenu(this).open(player);
    }

    @Override
    public void sendEditRequest(String key, Class<?> type) {

        if (List.class.isAssignableFrom(type)) {
            List<String> cloneList = currentSection.getList(key, Object.class).orElse(new ArrayList<>()).stream().map(Object::toString).collect(Collectors.toList());

            Conversation conversation = Conversation.create(XG7Plugins.getInstance())
                    .addPrompt("lang:[config-editor.list-editor]", Parser.STRING)
                    .onError(e -> {
                        Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.value-error", Pair.of("type", type.getSimpleName()));
                        e.printStackTrace();
                    })
                    .cancelWord("cancel")
                    .onAbandon(conversationAbandonedEvent -> sendPage(currentSection))
                    .exitLoopWord("exit")
                    .onLoop(object -> {
                        String value = object.toString();

                        if (value.equalsIgnoreCase("remove")) {

                            if (!cloneList.isEmpty()) {
                                String previousValue = cloneList.remove(cloneList.size() - 1);
                                Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.list-remove",  Pair.of("line", previousValue));
                                return;
                            }

                            Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.list-empty");

                            return;
                        }

                        cloneList.add(value);

                        Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.list-state", Pair.of("list", cloneList.toString()));
                    })
                    .onFinish(response -> {
                        currentSection.set(key, cloneList);
                        saved = false;

                        sendPage(currentSection);
                    });

            conversation.start(player);

            return;
        }

        Parser parser = Parser.getParserOf(type);

        if (parser == null) parser = Parser.STRING;

        if (parser.equals(Parser.BOOLEAN)) {
            currentSection.set(key, !(boolean) currentSection.get(key));
            saved = false;
            sendPage(currentSection);
            return;
        }

        Parser finalParser = parser;
        Conversation conversation = Conversation.create(XG7Plugins.getInstance())
                .addPrompt("lang:[config-editor.insert-value]", parser)
                .addBuildPlaceholders(Collections.singletonList(Pair.of("type", type.getSimpleName())))
                .onError(e -> {
                    Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.value-error", Pair.of("type", type.getSimpleName()));
                    e.printStackTrace();
                })
                .cancelWord("cancel")
                .timeOut(120)
                .onAbandon(conversationAbandonedEvent -> sendPage(currentSection))
                .onFinish(response -> {
                    Object value = response.get(0);

                    if (finalParser.equals(Parser.TIME) || finalParser.equals(Parser.UUID)) value = value.toString();

                    currentSection.set(key, value);
                    saved = false;

                    sendPage(currentSection);
                });

        conversation.start(player);
    }

    @Override
    public void sendAddRequest() {

        StringBuilder types = new StringBuilder();

        for (Parser value : Parser.values()) {
            types.append(value.name()).append(",");
        }

        types.append("LIST").append(",").append("SECTION");

        Conversation conversation = Conversation.create(XG7Plugins.getInstance())
                .addPrompt("lang:[config-editor.add.insert-key]", Parser.STRING)
                .addPrompt("lang:[config-editor.add.insert-value-type]", Parser.STRING)
                .addBuildPlaceholders(Collections.singletonList(Pair.of("types", types.toString())))
                .cancelWord("cancel")
                .timeOut(120)
                .onAbandon(conversationAbandonedEvent -> sendPage(currentSection))
                .onFinish(response -> {
                    String key = response.get(0).toString();
                    String valueType = response.get(1).toString();

                    if (key.isEmpty() || !key.matches("^[a-zA-Z][a-zA-Z0-9-]*$")) {
                        Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.add.invalid-key", Pair.of("type", valueType));
                        sendPage(currentSection);
                        return;
                    }

                    if (currentSection.contains(key)) {
                        Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.add.key-exists");
                        sendPage(currentSection);
                        return;
                    }

                    if (valueType.equalsIgnoreCase("LIST")) {
                        currentSection.set(key, new ArrayList<String>());
                        saved = false;
                        sendPage(currentSection);
                        return;
                    }

                    if (valueType.equalsIgnoreCase("SECTION")) {
                        Map<String, Object> newSection = new HashMap<>();
                        newSection.put("default", "value");

                        currentSection.set(key, newSection);
                        saved = false;

                        sendPage(currentSection);
                        return;
                    }

                    Parser valueTypeParser;

                    try {
                        valueTypeParser = Parser.valueOf(valueType.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.add.invalid-type", Pair.of("type", valueType));
                        sendPage(currentSection);
                        return;
                    }

                    Object defVal = valueTypeParser.isBoolean() ? false : valueTypeParser.isNumber() ? 0 : valueTypeParser.isTime() ? Time.of(0) : "null";

                    currentSection.set(key, defVal);
                    saved = false;

                    sendPage(currentSection);
                });

        conversation.start(player);
    }

}
