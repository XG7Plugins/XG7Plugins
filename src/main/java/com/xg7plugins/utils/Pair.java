package com.xg7plugins.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * A generic pair class that holds two elements of different types.
 *
 * @param <F> The type of the first element
 * @param <S> The type of the second element
 */
@Data
@AllArgsConstructor
@ToString
public class Pair<F, S> {

    /**
     * The first element in the pair
     */
    private F first;

    /**
     * The second element in the pair
     */
    private S second;

    /**
     * Creates a new Pair instance with the given elements
     *
     * @param first  The first element
     * @param second The second element
     * @param <F>    The type of the first element
     * @param <S>    The type of the second element
     * @return A new Pair containing the provided elements
     */
    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    public static <F, S> Pair<F, S> fromEntry(Map.Entry<F, S> entry) {
        return new Pair<>(entry.getKey(), entry.getValue());
    }

    public static <F, S> List<Pair<F, S>> getListFromEntrySet(Set<? extends Map.Entry<F, S>> entries) {
        return entries.stream().map(Pair::fromEntry).collect(Collectors.toList());
    }

    /**
     * Converts this pair to a Mojang pair
     *
     * @return An equivalent Mojang Pair instance containing the same elements
     */
    public com.mojang.datafixers.util.Pair<F, S> toMojangPair() {
        return new com.mojang.datafixers.util.Pair<>(first, second);
    }

    /**
     * Converts a list of Pairs to a list of Mojang Pairs
     *
     * @param list The list of Pairs to convert
     * @return A list of equivalent Mojang Pairs
     */
    public static List<com.mojang.datafixers.util.Pair<?, ?>> toMojangPairList(List<Pair<?, ?>> list) {
        return list.stream().map(Pair::toMojangPair).collect(Collectors.toList());
    }

    /**
     * Applies a consumer function to both elements of the pair
     *
     * @param consumer The consumer function to apply
     */
    public void each(BiConsumer<F, S> consumer) {
        consumer.accept(first, second);
    }
}