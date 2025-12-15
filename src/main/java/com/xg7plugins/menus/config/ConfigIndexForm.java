package com.xg7plugins.menus.config;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.editor.impl.FormEditor;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7geyserforms.builder.FormBuilder;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.cumulus.util.FormImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigIndexForm extends SimpleForm {

    private final FormEditor  formEditor;

    public ConfigIndexForm(FormEditor editor) {
        super(
                "config-form-index",
                "lang:[config-form-editor.title]",
                XG7Plugins.getInstance(),
                Arrays.asList(Pair.of("path", editor.getCurrentSection().getPath().isEmpty() ? "root" : editor.getCurrentSection().getPath()),
                        Pair.of("saved", editor.isSaved() + ""))
        );
        this.formEditor = editor;
    }

    @Override
    public String content(Player player) {
        return "lang:[config-form-editor.content]";
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> buttons = new ArrayList<>();

        ConfigSection section = formEditor.getCurrentSection();

        for (String key : section.getKeys(false)) {
            if (key.equals("config-version")) continue;
            buttons.add(ButtonComponent.of(ChatColor.RESET + key));
        }

        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.add").getText(), FormImage.of(FormImage.Type.URL, "https://www.flaticon.com/br/icone-gratis/adicionar_2550342")));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.save").getText(), FormImage.of(FormImage.Type.URL, "https://cdn-icons-png.flaticon.com/128/489/489707.png")));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.back").getText(), FormImage.of(FormImage.Type.URL, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ7Ri82Zr0dlnbmnQahDNo1LbIPkKuwlCpH1Q&s")));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.delete").getText(), FormImage.of(FormImage.Type.URL, "https://cdn-icons-png.flaticon.com/512/3687/3687412.png")));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.close").getText(), FormImage.of(FormImage.Type.URL, "https://cdn-icons-png.flaticon.com/512/9068/9068678.png")));

        return buttons;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {

        List<ButtonComponent> buttons = form.buttons();

        int buttonID = result.clickedButtonId();

        int addID = buttons.size() - 5;
        int saveID = buttons.size() - 4;
        int backID = buttons.size() - 3;
        int deleteID = buttons.size() - 2;
        int closeID = buttons.size() - 1;

        if (buttonID == addID) {
            formEditor.sendAddRequest();
            return;
        }
        if (buttonID == backID) {
            formEditor.sendPage(formEditor.getCurrentSection().parent());
            return;
        }
        if (buttonID == saveID) {
            formEditor.save();
            formEditor.sendPage(formEditor.getCurrentSection());
            return;
        }
        if (buttonID == closeID) {
            if (formEditor.isSaved()) return;
            FormBuilder.modal("config-close-confirmation", plugin)
                    .title("lang:[config-form-editor.close-confirmation.title]")
                    .content("lang:[config-form-editor.close-confirmation.sure-to-close]")
                    .button1("lang:[config-form-editor.close-confirmation.confirm]")
                    .button2("lang:[config-form-editor.close-confirmation.back]")
                    .onClose((f, p) -> formEditor.sendPage(formEditor.getCurrentSection()))
                    .onError((f, e, p) -> formEditor.sendPage(formEditor.getCurrentSection()))
                    .onFinish((f, r, p) -> {
                        if (!r.clickedFirst()) {
                            formEditor.sendPage(formEditor.getCurrentSection());
                            return;
                        }
                        try {
                            formEditor.getCurrentSection().getFile().reload();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .build()
                    .send(player);
            return;
        }
        if (buttonID == deleteID) {

            FormBuilder.simple("config-delete-list", plugin)
                    .title("lang:[config-form-editor.delete-form.title]")
                    .content("lang:[config-form-editor.delete-form.content]")
                    .components(p -> {
                        
                        List<ButtonComponent> btns = new ArrayList<>();
                        
                        for (String key : formEditor.getCurrentSection().getKeys(false)) {
                            if (key.equals("config-version")) continue;
                            btns.add(ButtonComponent.of(ChatColor.RESET + key));
                        }
                        return btns;
                    })
                    .addBuilderPlaceholders(Collections.singletonList(Pair.of("path", formEditor.getCurrentSection().getPath())))
                    .onClose((f, p) -> formEditor.sendPage(formEditor.getCurrentSection()))
                    .onError((f, e, p) -> formEditor.sendPage(formEditor.getCurrentSection()))
                    .onFinish((f, r, p) -> {

                        String path = ChatColor.stripColor(r.clickedButton().text());

                        FormBuilder.modal("config-delete-confirmation", plugin)
                                        .title("lang:[config-form-editor.delete-confirmation.title]")
                                        .content("lang:[config-form-editor.delete-confirmation.sure-to-delete]")
                                        .button1("lang:[config-form-editor.delete-confirmation.confirm]")
                                        .button2("lang:[config-form-editor.delete-confirmation.back]")
                                        .onClose((fo, pl) -> formEditor.sendPage(formEditor.getCurrentSection()))
                                        .onError((fo, e, pl) -> formEditor.sendPage(formEditor.getCurrentSection()))
                                        .onFinish((fo, re, pl) -> {
                                            if (re.clickedFirst()) formEditor.getCurrentSection().parent().remove(path);
                                            formEditor.sendPage(formEditor.getCurrentSection());
                                        })
                                        .build()
                                        .send(player);
                            }
                    ).build().send(player);


            return;
        }

        String buttonLabel = ChatColor.stripColor(buttons.get(buttonID).text());

        formEditor.sendEditRequest(buttonLabel, formEditor.getCurrentSection().getType(buttonLabel));

    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {

    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {

    }
}
