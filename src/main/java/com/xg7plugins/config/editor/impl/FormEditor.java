package com.xg7plugins.config.editor.impl;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.editor.InGameEditor;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.menus.config.ConfigIndexForm;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogSingleOption;
import com.xg7plugins.modules.xg7geyserforms.builder.FormBuilder;
import com.xg7plugins.modules.xg7geyserforms.forms.customform.IComponent;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FormEditor extends InGameEditor {

    public FormEditor(Player player) {
        super(player);
    }

    @Override
    public void sendPage(ConfigSection configSection) {
        this.currentSection = configSection;
        new ConfigIndexForm(this).send(player);
    }

    @Override
    public void sendEditRequest(String key, Class<?> type) {

        if (Map.class.isAssignableFrom(type)) {
            sendPage(currentSection.child(key));
            return;
        }

        if (List.class.isAssignableFrom(type)) {

            List<String> cloneList = currentSection.getList(key, Object.class).orElse(new ArrayList<>()).stream().map(Object::toString).collect(Collectors.toList());

            AtomicInteger index = new AtomicInteger();
            List<IComponent> inputs = cloneList.stream().map(s -> new IComponent.Input(
                            Text.fromLang(player, XG7Plugins.getInstance(), "config-form-editor.value-editor.list-label")
                                    .replace("index", index.get() + "")
                                    .getText(),
                            "lang:[config-form-editor.value-editor.list-placeholder]",
                            cloneList.get(index.getAndIncrement())
                    ))
                    .collect(Collectors.toList());

            inputs.add(new IComponent.Input(
                    "lang:[config-form-editor.value-editor.new-line-label]",
                    "lang:[config-form-editor.value-editor.new-line-placeholder]",
                    ""
                    )
            );


            FormBuilder.custom("config-edit-form", XG7Plugins.getInstance())
                    .title("lang:[config-form-editor.value-editor.title]")
                    .addBuilderPlaceholders(Collections.singletonList(Pair.of("path", currentSection.getPath())))
                    .addComponents(inputs)
                    .onClose((f, p) -> sendPage(currentSection))
                    .onError((f, e, p) -> sendPage(currentSection))
                    .onFinish((f, r, p) -> {
                        List<String> newList = new ArrayList<>();

                        for (int i = 0; i < cloneList.size(); i++) {
                            String value = r.asInput(i);

                            if (value != null && !value.isEmpty()) {
                                newList.add(value);
                            }
                        }

                        String newLine = r.asInput(cloneList.size());
                        if (newLine != null && !newLine.isEmpty()) {
                            newList.add(newLine);
                        }

                        currentSection.set(key, newList);
                        saved = false;
                        sendPage(currentSection);

                    })
                    .build().send(player);
            return;
        }

        Object defaultVal = currentSection.get(key);

        Parser parser = Parser.getParserOf(type);

        if (parser == null) parser = Parser.STRING;

        IComponent chosenComponent = parser == Parser.BOOLEAN ?
                new IComponent.Toggle("lang:[config-form-editor.value-editor.value-toggle]", (Boolean) defaultVal) :
                new IComponent.Input("lang:[config-form-editor.value-editor.value-input]", "lang:[config-form-editor.value-editor.value-placeholder]", defaultVal.toString());

        Parser finalParser = parser;

        FormBuilder.custom("config-edit-form", XG7Plugins.getInstance())
                .title("lang:[config-form-editor.value-editor.title]")
                .addBuilderPlaceholders(Arrays.asList(
                        Pair.of("path", currentSection.getPath()),
                        Pair.of("type", type.getSimpleName())
                ))
                .addComponent(chosenComponent)
                .onClose((f, p) -> sendPage(currentSection))
                .onError((f, e, p) -> sendPage(currentSection))
                .onFinish((f, r, p) -> {

                    Object value = finalParser.equals(Parser.BOOLEAN) ? r.asToggle(0) : r.asInput(0);

                    Parser chosenParser = finalParser.equals(Parser.TIME) || finalParser.equals(Parser.UUID) ? Parser.STRING : finalParser;

                    try {
                        currentSection.set(key, chosenParser.convert(value + ""));
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
        List<String> options = new ArrayList<>();

        for (Parser value : Parser.values()) {
           options.add(ChatColor.RESET + value.name().toLowerCase());
        }

        options.add(ChatColor.RESET + "list");
        options.add(ChatColor.RESET + "section");

        FormBuilder.custom("config-editor-add", XG7Plugins.getInstance())
                .title("lang:[config-form-editor.add-form.title]")
                .addComponents(
                        new IComponent.Input(
                                "lang:[config-form-editor.add-form.key-label]",
                                "lang:[config-form-editor.add-form.key-placeholder]",
                                ""
                        ),
                        new IComponent.DropDown(
                                "lang:[config-form-editor.add-form.type-label]",
                                options,
                                0
                        )
                )
                .onClose((f, p) -> sendPage(currentSection))
                .onError((f, e,p) -> sendPage(currentSection))
                .onFinish((f, r, p) -> {

                    String key = r.asInput(0);

                    String value = ChatColor.stripColor(options.get(r.asDropdown(1)));

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

                    if (value.equalsIgnoreCase("LIST")) {
                        currentSection.set(key, new ArrayList<String>());
                        saved = false;
                        sendPage(currentSection);
                        return;
                    }

                    if (value.equalsIgnoreCase("SECTION")) {
                        Map<String, Object> newSection = new HashMap<>();
                        newSection.put("default", "value");

                        currentSection.set(key, newSection);
                        saved = false;

                        sendPage(currentSection);
                        return;
                    }

                    Parser parser = Parser.valueOf(value.toUpperCase());

                    Object defVal = parser.isBoolean() ? false : parser.isNumber() ? 0 : parser.isTime() ? Time.of(0) : "null";

                    currentSection.set(key, defVal);
                    saved = false;

                    sendPage(currentSection);

                })
                .build()
                .send(player);
    }
}
