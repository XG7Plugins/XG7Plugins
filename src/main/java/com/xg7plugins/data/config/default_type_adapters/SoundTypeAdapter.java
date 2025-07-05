package com.xg7plugins.data.config.default_type_adapters;

import com.cryptomorin.xseries.XSound;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigTypeAdapter;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.PlayableSound;
import org.bukkit.Sound;

public class SoundTypeAdapter implements ConfigTypeAdapter<PlayableSound> {
    @Override
    public PlayableSound fromConfig(Config config, String path, Object... optionalArgs) {
        String[] soundString = config.get(path, String.class).orElse("ENTITY_BAT_TAKEOFF, 1, 1").split(", ");

        return new PlayableSound(XSound.of(soundString[0]).orElse(XSound.ENTITY_BAT_TAKEOFF).get(), Parser.FLOAT.convert(soundString[1]), Parser.FLOAT.convert(soundString[2]));
    }

    @Override
    public Class<PlayableSound> getTargetType() {
        return PlayableSound.class;
    }
}
