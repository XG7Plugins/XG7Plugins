package com.xg7plugins.modules.xg7menus.menus.holders;

import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
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
        getMenu().goPage(page, this).thenAccept((p) -> this.page = p);
    }
    public void nextPage() {
        goPage(page + 1);
    }
    public void previousPage() {
        goPage(page - 1);
    }

    public static void refresh(PagedMenuHolder menuHolder) {
        BasicMenu.refresh(menuHolder).thenRun(() -> menuHolder.goPage(0));
    }

}
