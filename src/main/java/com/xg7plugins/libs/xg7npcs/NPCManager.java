package com.xg7plugins.libs.xg7npcs;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7npcs.npcs.NPC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class NPCManager {
    private HashMap<String, NPC> npcs = new HashMap<>();
    private XG7Plugins plugin;
    private String taskId;
    private long delay;

    public NPCManager(XG7Plugins plugin) {
        this.plugin = plugin;
        this.delay = plugin.getConfigsManager().getConfig("config").getTime("npcs-update-delay");
    }

    public NPC getNPCByID(UUID uuid) {
        return npcs.get(uuid);
    }
    public NPC getNPCByID(Player player, int id) {
        return npcs.values().stream().filter(hologram -> hologram.getNpcIDS().get(player.getUniqueId()) == id).findFirst().orElse(null);
    }

    public void addNPC(NPC npc) {
        npcs.put(npc.getId(), npc);
    }
    public void removeNPC(NPC npc) {
        npcs.remove(npc.getId());
    }
    public void addPlayer(Player player) {
        npcs.values().forEach(npcs -> npcs.spawn(player));
    }
    public void removePlayer(Player player) {
        npcs.values().forEach(npcs -> npcs.destroy(player));
    }

    public void initTask() {
        if (taskId != null) return;
        this.taskId = plugin.getTaskManager().addRepeatingTask(plugin, "npcs", () -> npcs.values().forEach(npc -> Bukkit.getOnlinePlayers().forEach(player -> {
            World world = npc.getLocation().getWorld();
            if (!player.getWorld().equals(world) && npc.getNpcIDS().containsKey(player.getUniqueId())) {
                npc.destroy(player);
                return;
            }
            if (player.getWorld().equals(world) && !npc.getNpcIDS().containsKey(player.getUniqueId())) {
                npc.spawn(player);
            }
        })),delay);
    }
    public void cancelTask() {
        plugin.getTaskManager().cancelTask(this.taskId);
        taskId = null;
    }
}
