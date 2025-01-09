package com.xg7plugins.help.chathelp;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class HelpPage {

    private final String id;

    @Setter
    private List<HelpComponent> messages;

    public HelpPage(String id, List<HelpComponent> messages) {
        this.id = id;
        this.messages = messages;
    }
    public HelpPage(String id) {
        this.id = id;
        this.messages = new ArrayList<>();
    }

    public void addMessage(HelpComponent message) {
        messages.add(message);
    }

    public void addMessages(HelpComponent... messages) {
        this.messages.addAll(Arrays.asList(messages));
    }


}
