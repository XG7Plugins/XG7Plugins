package com.xg7plugins.modules.xg7menus.item.parser;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.item.Item;

import java.util.List;

public interface ItemParser<T> {

    List<XMaterial> getMaterials();

    T parse(XMaterial material, String... args);


}
