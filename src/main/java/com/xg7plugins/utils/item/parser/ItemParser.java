package com.xg7plugins.utils.item.parser;

import com.cryptomorin.xseries.XMaterial;

import java.util.List;

/**
 * Interface for parsing items based on materials and arguments.
 *
 * @param <T> the type of item to be parsed
 */
public interface ItemParser<T> {

    /**
     * Get the list of materials that this parser can handle.
     *
     * @return a list of XMaterial objects
     */
    List<XMaterial> getMaterials();

    /**
     * Parse an item based on the given material and arguments.
     *
     * @param material the material to parse
     * @param args     additional arguments for parsing
     * @return the parsed item of type T
     */
    T parse(XMaterial material, String... args);


}
