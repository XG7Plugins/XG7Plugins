package com.xg7plugins.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@ToString
public class Pair<F,S> {
    private F first;
    private S second;

    public static <F,S> Pair<F,S> of (F first, S second) {
        return new Pair<>(first, second);
    }

    public com.mojang.datafixers.util.Pair<F,S> toMojangPair() {
        return new com.mojang.datafixers.util.Pair<>(first, second);
    }

    public static List<com.mojang.datafixers.util.Pair<?,?>> toMojangPairList(List<Pair<?,?>> list) {
        return list.stream().map(Pair::toMojangPair).collect(Collectors.toList());
    }
}
