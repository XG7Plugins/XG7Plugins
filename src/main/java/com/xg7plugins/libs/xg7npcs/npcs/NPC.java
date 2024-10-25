package com.xg7plugins.libs.xg7npcs.npcs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7holograms.HologramBuilder;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.utils.Location;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class NPC {

    protected Hologram name;
    protected GameProfile skin;
    protected Location location;
    protected List<Integer> entityIDS;

    public NPC(Plugin plugin, List<String> name, Location location) {
        this.name = HologramBuilder.creator(plugin).setLines(name).setLocation(location.add(0,-0.2,0)).build();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "dummy");
        gameProfile.getProperties().put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTcyOTcyMTI4Nzc5NSwKICAicHJvZmlsZUlkIiA6ICI1ZTI1YzI0ZmIxMGI0MTlmOWIzYTQzMWIyOTYzYjQ2YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6MW5jcSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zY2M3ODdkMmUxYmQ5YjhmMjc1ZjIwZjhmNjllYzFkMWYwOGNmZmI2NzBmZTNhZDhkOTRiOWQwMTBkZWIwYzJhIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "U0z9lhEvmncnsP1X5xc5sTTve0pH6dK99iQCL0Mw4SwzHcIcp8h28oL5sdRKM5xieUTkrBklNKAMoOloQUjz4m05xq852CyOKjesy3Q/3j2oCj4IEosoCG1ahSeIQGCTZAyhpdbTFRnCXB159peXDQER0MlWU9ZA6OGWl3GDfmrmAr8smiWYufIkS/jh9Nc90VCXzCi4vH9kxOTOJDV8TXIGYCvROPlVR7psfsJmBeyj3fcTGqEwwcns4yvklaEsGosDvvYAODfD+bJpf3dErUhu8nciFhCaIBykM5SybCxGnt2pbqs+rnETqJvABmuMMOivKEgQnzwm2b+YuW7flp3028pBaqEKXpgALwZUx4DvD3XTRklh4Bq9adYdTIAFLLxGf7ntYtSMpEG86l5Ch3XVfQn81yCei3sRggUWAzwpAorjqt8OeXxuCgGfmOqfrdhgSQLWR2n+YacR7vqoXRTFDDWgESBgO1ybZZKUqlpTsBZF5NYuJSzHIy6G6UFu57au7or7j3FzzYdSCo+Xpv/xIwTWt8zQFrwKJZzuyL+Of55+HuwWkk2sdypmPWhzPK6LEgv8DRjDgZrG80ZKfTPsufavrHDP5b06zES/LKaeq3OGFmCoSxTJY9ML/R5EoW+vbimsqFbbhqVxCrYfQwYTjca/bvA1Bpje7UfiN4c="));

        this.skin = gameProfile;
        this.entityIDS = new ArrayList<>();
        this.location = location;
    }

    public abstract void spawn(Player player);
    public abstract void destroy(Player player);
    public abstract void update(Player player);
    public void setSkin(String input) {
        skin.getProperties().put("textures", new Property("textures", input));
        Bukkit.getOnlinePlayers().forEach(player -> {
            destroy(player);
            spawn(player);
        });
    };

}
