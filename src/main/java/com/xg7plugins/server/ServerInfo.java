package com.xg7plugins.server;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Represents server information and handles server-to-server communication.
 * Contains details about the server name, address, port and software type.
 * Provides functionality for player connection management and attribute storage.
 */
@Data
public class ServerInfo {

    private String name;
    private String address;
    private int port;
    private transient final boolean bungeecord;
    private transient Software software;
    private transient final HashMap<String, Object> atributes = new HashMap<>();

    /**
     * Creates a new ServerInfo instance with plugin configuration.
     * Initializes server details including name, address and port.
     *
     * @param plugin The XG7Plugins instance
     * @throws ExecutionException   If there is an error accessing configuration
     * @throws InterruptedException If the initialization process is interrupted
     */
    public ServerInfo(XG7Plugins plugin) throws ExecutionException, InterruptedException {
        this();
        this.name = Config.mainConfigOf(plugin).get("plugin-server-name", String.class).orElseThrow(() -> new RuntimeException("Server name not found"));
        this.address = Bukkit.getIp();
        this.port = Bukkit.getPort();
        this.software = Software.getSoftware();
    }

    private ServerInfo() {
        this.software = Software.getSoftware();
        this.bungeecord = Bukkit.spigot().getConfig().getBoolean("settings.bungeecord");

        if (bungeecord) XG7Plugins.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(XG7Plugins.getInstance(), "BungeeCord");
    }

    /**
     * Connects a player to this server using BungeeCord messaging.
     *
     * @param player The player to connect
     * @throws IOException If there is an error sending the plugin message
     */
    public void connectPlayer(Player player) throws IOException {
        connectTo(name, player);
    }

    /**
     * Connects a player to the specified server using BungeeCord messaging.
     *
     * @param serverName The name of the target server
     * @param player     The player to connect
     * @throws IOException If there is an error sending the plugin message
     */
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

    /**
     * Sets a custom attribute for this server.
     *
     * @param key   The attribute key
     * @param value The attribute value
     */
    public void setAttribute(String key, Object value) {
        atributes.put(key, value);
    }

    /**
     * Gets a custom attribute value with type checking.
     *
     * @param key  The attribute key
     * @param type The expected attribute type
     * @param <T>  The generic type parameter
     * @return Optional containing the attribute value if present and of correct type
     */
    public <T> Optional<T> getAttribute(String key, Class<T> type) {
        return Optional.ofNullable(type.cast(atributes.get(key)));
    }

    /**
     * Checks if an attribute exists.
     *
     * @param key The attribute key to check
     * @return true if the attribute exists, false otherwise
     */
    public boolean hasAttribute(String key) {
        return atributes.containsKey(key);
    }

    /**
     * Represents different Minecraft server software types.
     * Currently supports SPIGOT and PAPER_SPIGOT variants.
     */
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
