package com.xg7plugins.config.editor.impl;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.editor.InGameEditor;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.menus.config.ConfigIndexMenu;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogSingleOption;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DialogEditor extends InGameEditor {
    public DialogEditor(Player player) {
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

            AtomicInteger index = new AtomicInteger();
            List<DialogInput> inputs = cloneList.stream().map(s -> DialogInput
                    .textInput(
                            "line" + index.get(),
                            Text.fromLang(player, XG7Plugins.getInstance(), "config-editor.dialog.list-label")
                                .replace("index", index.get() + "")
                                .getText(),
                            true, 150, 16, 1, 32,
                            currentSection.getList(key, String.class).orElse(new ArrayList<>()).get(index.getAndIncrement())
                    )
            ).collect(Collectors.toList());

            inputs.add(DialogInput.textInput(
                            "newline",
                            Text.fromLang(player, XG7Plugins.getInstance(), "config-editor.dialog.list-label")
                                    .replace("index", "+1")
                                    .getText()
                    ));

            Dialog.confirmation(XG7Plugins.getInstance(), "config-editor")
                    .title("lang:[config-editor.dialog.title]")
                    .body(Arrays.asList(
                            DialogBodyElement.plainMessage("lang:[config-editor.dialog.list-description]"),
                            DialogBodyElement.item(
                                    Item.from(Material.EMERALD).enchant(Enchantment.DURABILITY, 1),
                                    "Awesome"
                            )
                    ))
                    .inputs(inputs)
                    .addBuildPlaceholders(Arrays.asList(
                            Pair.of("type", type.getSimpleName()),
                            Pair.of("path", key)
                    ))
                    .yesButton(DialogButton.yes("lang:[config-editor.dialog.confirm-edit]", "lang:[config-editor.dialog.confirm-hover]"))
                    .noButton(DialogButton.no("lang:[config-editor.dialog.back]", "lang:[config-editor.dialog.back-hover]"))
                    .onResponse((channel, response) -> {
                        if (channel.equalsIgnoreCase("/no")) {
                            sendPage(currentSection);
                            return;
                        }
                        List<String> newList = new ArrayList<>();

                        for (int i = 0; i < cloneList.size(); i++) {
                            Object raw = response.get("line" + i);
                            if (raw == null) continue;

                            String value = raw.toString();
                            if (!value.isEmpty()) {
                                newList.add(value);
                            }
                        }

                        Object rawNew = response.get("newline");
                        if (rawNew != null) {
                            String newLine = rawNew.toString();
                            if (!newLine.isEmpty()) {
                                newList.add(newLine);
                            }
                        }

                        currentSection.set(key, newList);
                        saved = false;
                        sendPage(currentSection);

                    })
                    .build().send(player);

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

        Dialog.confirmation(currentSection.getFile().getPlugin(), "config-editor")
                .title("lang:[config-editor.dialog.title]")
                .body(Arrays.asList(
                        DialogBodyElement.plainMessage("lang:[config-editor.dialog.description]"),
                        DialogBodyElement.item(
                                Item.from(Material.EMERALD).enchant(Enchantment.DURABILITY, 1),
                                "Awesome"
                        )
                ))
                .inputs(Collections.singletonList(
                        DialogInput.textInput(
                                "value",
                                "lang:[config-editor.dialog.input-label]",
                                true, 150, 16, 1, 32,
                                currentSection.get(key).toString()
                        )
                ))
                .addBuildPlaceholders(Arrays.asList(
                        Pair.of("type", type.getSimpleName()),
                        Pair.of("path", key)
                ))
                .yesButton(DialogButton.yes("lang:[config-editor.dialog.confirm-edit]", "lang:[config-editor.dialog.confirm-hover]"))
                .noButton(DialogButton.no("lang:[config-editor.dialog.back]", "lang:[config-editor.dialog.back-hover]"))
                .onResponse((channel, response) -> {
                    if (channel.equalsIgnoreCase("/no")) {
                        sendPage(currentSection);
                        return;
                    }

                    String value = response.get("value").toString();

                    Parser chosenParser = finalParser.equals(Parser.TIME) || finalParser.equals(Parser.UUID) ? Parser.STRING : finalParser;

                    try {
                        currentSection.set(key, chosenParser.convert(value));
                    } catch (Exception e) {
                        Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.value-error", Pair.of("type", type.getSimpleName()));
                        e.printStackTrace();
                        sendPage(currentSection);
                        return;
                    }

                    saved = false;

                    sendPage(currentSection);
                })
                .build().send(player);
    }

    @Override
    public void sendAddRequest() {

        StringBuilder types = new StringBuilder();

        for (Parser value : Parser.values()) {
            types.append(value.name()).append(",");
        }

        types.append("LIST").append(",").append("SECTION");

        List<DialogSingleOption.Option> options = new ArrayList<>();

        for (Parser value : Parser.values()) {
            options.add(DialogSingleOption.Option.of(value.name().toLowerCase(), value.name()));
        }

        options.add(DialogSingleOption.Option.of("list", "LIST"));
        options.add(DialogSingleOption.Option.of("section", "SECTION"));

        Dialog.confirmation(XG7Plugins.getInstance(), "config-editor")
                .title("lang:[config-editor.add.dialog-title]")
                .addBuildPlaceholders(Collections.singletonList(Pair.of("types", types.toString())))
                .inputs(Arrays.asList(
                        DialogInput.textInput("key", "lang:[config-editor.add.dialog-insert-key]"),
                        DialogInput.optionsInput("value_type", "lang:[config-editor.add.dialog-insert-value-type]", options)
                ))
                .yesButton(DialogButton.yes("lang:[config-editor.add.dialog-confirm]", "lang:[config-editor.add.dialog-confirm-hover]"))
                .noButton(DialogButton.no("lang:[config-editor.add.dialog-cancel]", "lang:[config-editor.add.dialog-cancel-hover]"))
                .onResponse((channel, response) -> {
                    if (channel.equalsIgnoreCase("/no")) {
                        sendPage(currentSection);
                        return;
                    }

                    String key = response.get("key").toString();

                    if (key.isEmpty() || !key.matches("^[a-zA-Z][a-zA-Z0-9-]*$")) {
                        Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.add.invalid-key");
                        sendPage(currentSection);
                        return;
                    }

                    if (currentSection.contains(key)) {
                        Text.sendTextFromLang(player, XG7Plugins.getInstance(), "config-editor.add.key-exists");
                        sendPage(currentSection);
                        return;
                    }

                    if (response.get("value_type").toString().equalsIgnoreCase("LIST")) {
                        currentSection.set(key, new ArrayList<String>());
                        saved = false;
                        sendPage(currentSection);
                        return;
                    }

                    if (response.get("value_type").toString().equalsIgnoreCase("SECTION")) {
                        Map<String, Object> newSection = new HashMap<>();
                        newSection.put("default", "value");

                        currentSection.set(key, newSection);
                        saved = false;

                        sendPage(currentSection);
                        return;
                    }

                    Parser parser = Parser.valueOf(response.get("value_type").toString().toUpperCase());

                    Object defVal = parser.isBoolean() ? false : parser.isNumber() ? 0 : parser.isTime() ? Time.of(0) : "null";

                    currentSection.set(key, defVal);
                    saved = false;

                    sendPage(currentSection);

                })
                .build()
                .send(player);
    }
}
