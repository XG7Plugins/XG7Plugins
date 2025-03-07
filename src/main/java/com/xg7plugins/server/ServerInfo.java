package com.xg7plugins.server;

import com.xg7plugins.XG7Plugins;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Data
public class ServerInfo {

    private String name;
    private String address;
    private int port;
    private transient final boolean bungeecord;
    private transient Software software;
    private transient final HashMap<String, Object> atributes = new HashMap<>();

    public ServerInfo(XG7Plugins plugin) throws ExecutionException, InterruptedException {
        this();
        this.name = plugin.getConfig("config").get("plugin-server-name", String.class).orElseThrow(() -> new RuntimeException("Server name not found"));
        this.address = Bukkit.getIp();
        this.port = Bukkit.getPort();
        this.software = Software.getSoftware();
    }

    private ServerInfo() {
        this.software = Software.getSoftware();
        this.bungeecord = Bukkit.spigot().getConfig().getBoolean("settings.bungeecord");

        if (bungeecord) XG7Plugins.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(XG7Plugins.getInstance(), "BungeeCord");
    }

    public void connectPlayer(Player player) throws IOException {
        connectTo(name, player);
    }
    public void connectTo(String serverName, Player player) throws IOException {
        if (!bungeecord) return;

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArray);
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        out.flush();

        player.sendPluginMessage(XG7Plugins.getInstance(), "BungeeCord", byteArray.toByteArray());

        out.close();
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ServerInfo serverInfo = (ServerInfo) other;
        return port == serverInfo.port && name.equals(serverInfo.name) && address.equals(serverInfo.address);
    }

    public void setAtribute(String key, Object value) {
        atributes.put(key, value);
    }
    public <T> Optional<T> getAtribute(String key, Class<T> type) {
        return Optional.ofNullable(type.cast(atributes.get(key)));
    }
    public boolean hasAtribute(String key) {
        return atributes.containsKey(key);
    }

    public enum Software {
        SPIGOT,
        PAPER_SPIGOT;

        public boolean isPaper() {
            return this == PAPER_SPIGOT;
        }

        public static Software getSoftware() {
            return Bukkit.getServer().getName().contains("Paper") ? PAPER_SPIGOT : SPIGOT;
        }
    }

}
