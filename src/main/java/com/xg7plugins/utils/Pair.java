package com.xg7plugins.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class Pair<F,S> {
    private F first;
    private S second;

    public com.mojang.datafixers.util.Pair<F,S> toMojangPair() {
        return new com.mojang.datafixers.util.Pair<>(first, second);
    }

    public static List<com.mojang.datafixers.util.Pair<?,?>> toMojangPairList(List<Pair<?,?>> list) {
        return list.stream().map(Pair::toMojangPair).collect(Collectors.toList());
    }
}
