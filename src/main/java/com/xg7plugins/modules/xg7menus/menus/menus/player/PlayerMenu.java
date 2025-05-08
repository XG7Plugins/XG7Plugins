package com.xg7plugins.modules.xg7menus.menus.menus.player;

import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.menus.IBasicMenu;
import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.holders.PlayerMenuHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public abstract class PlayerMenu implements IBasicMenu {

    private IPlayerMenuConfigurations menuConfigs;

    @Override
    public void open(Player player) {

        PlayerMenuHolder playerMenuHolder = new PlayerMenuHolder(this, player);

        XG7Menus.registerPlayerMenuHolder(playerMenuHolder);

    }

    @Override
    public void close(BasicMenuHolder menuHolder) {

        PlayerMenuHolder playerMenuHolder = (PlayerMenuHolder) menuHolder;

        Player player = playerMenuHolder.getPlayer();

        player.getInventory().clear();

        playerMenuHolder.getOldItems().forEach((slot, item) -> player.getInventory().setItem(slot, item));

        XG7Menus.removePlayerMenuHolder(player.getUniqueId());

    }

    public void onDrop(ActionEvent event) {}
    public void onPickup(ActionEvent event) {}

    public void onBreakBlocks(ActionEvent event) {}
    public void onPlaceBlocks(ActionEvent event) {}

}
