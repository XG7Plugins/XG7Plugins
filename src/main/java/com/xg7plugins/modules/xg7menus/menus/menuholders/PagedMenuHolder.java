package com.xg7plugins.modules.xg7menus.menus.menuholders;

import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
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
        System.out.println("Going to page: " + page);
        if (getMenu().goPage(page, this)) this.page = page;
    }
    public void nextPage() {
        goPage(page + 1);
    }
    public void previousPage() {
        goPage(page - 1);
    }

}
