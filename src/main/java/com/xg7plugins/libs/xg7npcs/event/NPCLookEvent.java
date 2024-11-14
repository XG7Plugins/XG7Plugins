package com.xg7plugins.libs.xg7npcs.event;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.libs.xg7npcs.NPCManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class NPCLookEvent implements Event {
    private final NPCManager npcManager = XG7Plugins.getInstance().getNpcManager();

    @Override
    public boolean isEnabled() {
        return XG7Plugins.getInstance().getConfigsManager().getConfig("config").get("npcs-look-at-player");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        npcManager.getNpcs().values().forEach(npc -> {
            if (!npc.isLookAtPlayer()) return;

            if (!npc.getNpcIDS().containsKey(player.getUniqueId())) return;
            int npcId = npc.getNpcIDS().get(player.getUniqueId());
            npc.lookAtPlayer(player, npcId, npcManager.getLookingNPCS().get(npcId));
        });


    }
}
