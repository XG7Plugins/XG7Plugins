package com.xg7plugins.utils.text.component.modfiers;

import com.xg7plugins.utils.text.component.Component;

public interface TextModifier {

    String serialize();

    void apply(Component component);

}
