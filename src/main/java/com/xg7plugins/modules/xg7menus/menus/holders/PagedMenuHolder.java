package com.xg7plugins.modules.xg7menus.menus.holders;

import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.Menu;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.PagedMenu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class PagedMenuHolder extends MenuHolder {

    private int page;

    public PagedMenuHolder(PagedMenu menu, Player player) {
        super(menu, player);
    }

    @Override
    public PagedMenu getMenu() {
        return (PagedMenu) super.getMenu();
    }

    public void goPage(int page) {
        this.page = page;
        getMenu().goPage(page);
    }
    public void nextPage() {
        getMenu().goPage(page + 1).thenAccept((page) -> this.page = page);
    }
    public void previousPage() {
        getMenu().goPage(page - 1).thenAccept((page) -> this.page = page);

    }

}
