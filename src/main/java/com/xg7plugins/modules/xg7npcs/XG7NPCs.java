package com.xg7plugins.modules.xg7npcs;

import com.xg7plugins.events.Listener;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7npcs.listeners.NPCClickListener;
import com.xg7plugins.modules.xg7npcs.listeners.NPCListener;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.modules.xg7npcs.task.NPCsLookTask;
import com.xg7plugins.tasks.tasks.TimerTask;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class XG7NPCs implements Module {

    private final HashMap<String, NPC> registeredNPCs = new HashMap<>();
    private final HashMap<UUID, List<LivingNPC>> livingNPCs = new HashMap<>();

    private boolean enabled;


    @Override
    public void onInit() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {
        livingNPCs.values().forEach(listLiving -> listLiving.forEach(LivingNPC::kill));
    }

    @Override
    public String getName() {
        return "XG7NPCs";
    }

    public List<TimerTask> loadTasks() {
        return Collections.singletonList(new NPCsLookTask(this));
    }

    public List<Listener> loadListeners() {
        return Arrays.asList(new NPCClickListener(), new NPCListener());
    }

    public void registerNPC(NPC NPC) {
        if (NPC == null) return;
        this.registeredNPCs.put(NPC.getId(), NPC);
    }

    public List<LivingNPC> getAllLivingNPCs() {
        List<LivingNPC> livingNPCs = new ArrayList<>();
        for (List<LivingNPC> NPC : this.livingNPCs.values()) livingNPCs.addAll(NPC);
        return livingNPCs;
    }

    public void registerLivingNPC(LivingNPC livingNPC) {
        this.livingNPCs.putIfAbsent(livingNPC.getPlayer().getUniqueId(), new ArrayList<>());
        this.livingNPCs.get(livingNPC.getPlayer().getUniqueId()).add(livingNPC);
    }
    public void unregisterLivingNPC(UUID playerUUID, String NPCId) {
        if (!this.livingNPCs.containsKey(playerUUID)) return;

        this.livingNPCs.get(playerUUID).removeIf(lnpc -> lnpc.getNPC().getId().equals(NPCId));
    }

    public @NotNull List<LivingNPC> getLivingNPCsByUUID(UUID uuid) {
        return this.livingNPCs.getOrDefault(uuid, new ArrayList<>());
    }

    public LivingNPC getLivingNPC(UUID uuid, String NPCId) {
        if (!this.livingNPCs.containsKey(uuid)) return null;

        return this.livingNPCs.get(uuid).stream().filter(h -> h.getNPC().getId().equals(NPCId)).findFirst().orElse(null);
    }

    public void unregisterAllLivingNPCs(UUID uuid) {
        if (!this.livingNPCs.containsKey(uuid)) return;

        this.livingNPCs.get(uuid).forEach(LivingNPC::kill);

        this.livingNPCs.remove(uuid);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
