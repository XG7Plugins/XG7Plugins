package com.xg7plugins.modules.xg7menus.builders;

import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.menus.interfaces.player.PlayerMenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.player.PlayerMenu;
import com.xg7plugins.modules.xg7menus.simple.SimplePlayerMenu;

import java.util.function.Consumer;

public class PlayerMenuBuilder extends BasicMenuBuilder<PlayerMenuBuilder, PlayerMenu> {

    protected Consumer<ActionEvent> dropConsumer = e -> {};
    protected Consumer<ActionEvent> pickupConsumer = e -> {};
    protected Consumer<ActionEvent> breakConsumer = e -> {};
    protected Consumer<ActionEvent> placeConsumer = e -> {};


    public PlayerMenuBuilder(PlayerMenuConfigurations configs) {
        super(configs);
    }

    public PlayerMenuBuilder onDrop(Consumer<ActionEvent> consumer) {
        this.dropConsumer = consumer != null ? consumer : e -> {};
        return this;
    }

    public PlayerMenuBuilder onPickup(Consumer<ActionEvent> consumer) {
        this.pickupConsumer = consumer != null ? consumer : e -> {};
        return this;
    }

    public PlayerMenuBuilder onBreak(Consumer<ActionEvent> consumer) {
        this.breakConsumer = consumer != null ? consumer : e -> {};
        return this;
    }

    public PlayerMenuBuilder onPlace(Consumer<ActionEvent> consumer) {
        this.placeConsumer = consumer != null ? consumer : e -> {};
        return this;
    }

    @Override
    public PlayerMenu build() {
        return new SimplePlayerMenu(
                (PlayerMenuConfigurations) menuConfigs,
                items,
                clickableItems,
                clickConsumer,
                dragConsumer,
                openEventConsumer,
                closeEventConsumer,
                updateAction,
                repeatingUpdateAction,
                dropConsumer,
                pickupConsumer,
                breakConsumer,
                placeConsumer
        );
    }

    public static PlayerMenuBuilder inicialize(PlayerMenuConfigurations configurations) {
        return new PlayerMenuBuilder(configurations);
    }
}
