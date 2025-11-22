package com.xg7plugins.utils.item.parser;

import com.cryptomorin.xseries.XMaterial;

import java.util.List;

public interface ItemParser<T> {

    List<XMaterial> getMaterials();

    T parse(XMaterial material, String... args);


}
