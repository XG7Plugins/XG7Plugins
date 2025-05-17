package com.xg7plugins;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.CacheManager;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.processor.DatabaseProcessor;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.dependencies.DependencyManager;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.modules.ModuleManager;
import com.xg7plugins.server.ServerInfo;
import com.xg7plugins.tasks.CooldownManager;
import com.xg7plugins.tasks.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Classe utilitária de API que fornece acesso centralizado a todos os componentes e funcionalidades
 * do framework XG7Plugins.
 *
 * Esta classe contém métodos estáticos que permitem acessar gerenciadores, informações do servidor,
 * dados de jogadores, e outras funcionalidades do ecossistema XG7Plugins sem a necessidade de
 * instanciar objetos ou gerenciar dependências manualmente.
 *
 * @author XG7Plugins
 */
public class XG7PluginsAPI {

    /**
     * Obtém a instância de um plugin XG7 específico pelo seu tipo de classe.
     *
     * @param <T> O tipo do plugin a ser retornado
     * @param pluginClass A classe do plugin desejado
     * @return A instância do plugin requisitado, ou null se não for encontrado
     */
    public static <T extends Plugin> T getXG7Plugin(Class<T> pluginClass) {
        return (T) XG7Plugins.getInstance().getPlugins().values().stream().filter(plugin -> pluginClass == plugin.getClass()).findFirst().orElse(null);
    }

    /**
     * Obtém a instância de um plugin XG7 pelo seu nome.
     *
     * @param <T> O tipo do plugin a ser retornado
     * @param name O nome do plugin
     * @return A instância do plugin requisitado, ou null se não for encontrado
     */
    public static <T extends Plugin> T getXG7Plugins(String name) {
        return (T) XG7Plugins.getInstance().getPlugins().get(name);
    }

    /**
     * Obtém um conjunto contendo todas as instâncias de plugins XG7 registrados.
     *
     * @return Um conjunto de todas as instâncias de plugins XG7
     */
    public static Set<Plugin> getAllXG7Plugins() {
        return new HashSet<>(XG7Plugins.getInstance().getPlugins().values());
    }

    /**
     * Obtém um conjunto contendo os nomes de todos os plugins XG7 registrados.
     *
     * @return Um conjunto com os nomes de todos os plugins XG7
     */
    public static Set<String> getAllXG7PluginsName() {
        return XG7Plugins.getInstance().getPlugins().values().stream().map(Plugin::getName).collect(Collectors.toSet());
    }

    /**
     * Obtém o gerenciador de tarefas que controla as tarefas agendadas.
     *
     * @return A instância global do TaskManager
     */
    public static TaskManager taskManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), TaskManager.class);
    }

    /**
     * Obtém o gerenciador de banco de dados que lida com conexões e operações de persistência.
     *
     * @return A instância global do DatabaseManager
     */
    public static DatabaseManager database() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), DatabaseManager.class);
    }

    /**
     * Obtém o gerenciador de cache que manipula dados em memória.
     *
     * @return A instância global do CacheManager
     */
    public static CacheManager cacheManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), CacheManager.class);
    }

    /**
     * Obtém o gerenciador de eventos Bukkit do sistema.
     *
     * @return A instância global do EventManager
     */
    public static EventManager eventManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), EventManager.class);
    }

    /**
     * Obtém o gerenciador de eventos de pacotes de rede.
     *
     * @return A instância global do PacketEventManager
     */
    public static PacketEventManager packetEventManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), PacketEventManager.class);
    }

    /**
     * Obtém o gerenciador de cooldowns para controle de tempo entre ações.
     *
     * @return A instância global do CooldownManager
     */
    public static CooldownManager cooldowns() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), CooldownManager.class);
    }

    /**
     * Obtém o gerenciador de idiomas do sistema.
     *
     * @return A instância global do LangManager
     */
    public static LangManager langManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), LangManager.class);
    }

    /**
     * Obtém o gerenciador de módulos do sistema.
     *
     * @return A instância global do ModuleManager
     */
    public static ModuleManager moduleManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), ModuleManager.class);
    }

    /**
     * Obtém o gerenciador de configurações para um plugin específico.
     *
     * @param plugin O plugin para o qual obter o gerenciador de configurações
     * @return O ConfigManager associado ao plugin especificado
     */
    public static ConfigManager configManager(Plugin plugin) {
        return ManagerRegistry.get(plugin, ConfigManager.class);
    }

    /**
     * Obtém o gerenciador de comandos para um plugin específico.
     *
     * @param plugin O plugin para o qual obter o gerenciador de comandos
     * @return O CommandManager associado ao plugin especificado
     */
    public static CommandManager commandManager(Plugin plugin) {
        return ManagerRegistry.get(plugin, CommandManager.class);
    }

    /**
     * Obtém o gerenciador de JSON do sistema.
     *
     * @return A instância global do JsonManager
     */
    public static JsonManager jsonManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), JsonManager.class);
    }

    /**
     * Obtém o gerenciador de dependências que controla plugins externos.
     *
     * @return A instância global do DependencyManager
     */
    public static DependencyManager dependencyManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), DependencyManager.class);
    }

    /**
     * Obtém o processador de banco de dados para operações de baixo nível com o banco.
     *
     * @return A instância do DatabaseProcessor
     */
    public static DatabaseProcessor dbProcessor() {
        return database().getProcessor();
    }

    /**
     * Verifica se uma dependência específica está carregada e habilitada.
     *
     * @param name O nome da dependência a verificar
     * @return true se a dependência estiver habilitada, false caso contrário
     */
    public static boolean isDependencyEnabled(String name) {
        return dependencyManager().isLoaded(name);
    }

    /**
     * Verifica se o suporte a formulários Geyser está habilitado.
     * Exige que o plugin Floodgate esteja carregado e a opção habilitada na configuração.
     *
     * @return true se o suporte a formulários Geyser estiver habilitado, false caso contrário
     */
    public static boolean isGeyserFormsEnabled() {
        return isDependencyEnabled("floodgate") && Config.mainConfigOf(XG7Plugins.getInstance()).get("enable-geyser-forms",Boolean.class).orElse(false);
    }

    /**
     * Verifica se um mundo específico está habilitado para um plugin.
     *
     * @param plugin O plugin a verificar
     * @param world O nome do mundo a verificar
     * @return true se o mundo estiver habilitado para o plugin, false caso contrário
     */
    public static boolean isWorldEnabled(Plugin plugin, String world) {
        return plugin.getEnvironmentConfig().getEnabledWorlds().contains(world);
    }

    /**
     * Verifica se um mundo específico está habilitado para um plugin.
     *
     * @param plugin O plugin a verificar
     * @param world O objeto World a verificar
     * @return true se o mundo estiver habilitado para o plugin, false caso contrário
     */
    public static boolean isWorldEnabled(Plugin plugin, World world) {
        return isWorldEnabled(plugin, world.getName());
    }

    /**
     * Verifica se o mundo em que o jogador está atualmente está habilitado para um plugin.
     *
     * @param plugin O plugin a verificar
     * @param player O jogador cujo mundo será verificado
     * @return true se o mundo do jogador estiver habilitado para o plugin, false caso contrário
     */
    public static boolean isInWorldEnabled(Plugin plugin, Player player) {
        return isWorldEnabled(plugin, player.getWorld());
    }

    /**
     * Solicita os dados de um jogador pelo seu UUID de forma assíncrona.
     *
     * @param uuid O UUID do jogador
     * @return Um CompletableFuture que conterá os dados do jogador quando estiver disponível
     */
    public static CompletableFuture<PlayerData> requestPlayerData(UUID uuid) {
        return XG7Plugins.getInstance().getPlayerDataDAO().get(uuid);
    }

    /**
     * Solicita os dados de um jogador a partir da instância do jogador de forma assíncrona.
     *
     * @param player O jogador para o qual obter os dados
     * @return Um CompletableFuture que conterá os dados do jogador quando estiver disponível
     */
    public static CompletableFuture<PlayerData> requestPlayerData(Player player) {
        return requestPlayerData(player.getUniqueId());
    }

    /**
     * Obtém um conjunto com os nomes de todos os jogadores online.
     *
     * @return Um conjunto contendo os nomes de todos os jogadores online
     */
    public static Set<String> getAllPlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet());
    }

    /**
     * Obtém um conjunto com os UUIDs de todos os jogadores online.
     *
     * @return Um conjunto contendo os UUIDs de todos os jogadores online
     */
    public static Set<UUID> getAllPlayerUUIDs() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

    /**
     * Obtém um conjunto com as instâncias de todos os jogadores online.
     *
     * @return Um conjunto contendo as instâncias de todos os jogadores online
     */
    public static Set<Player> getAllPlayers() {
        return new HashSet<>(Bukkit.getOnlinePlayers());
    }

    /**
     * Obtém o tipo de software do servidor (Bukkit, Spigot, Paper, etc).
     *
     * @return O enum ServerInfo.Software representando o tipo de servidor
     */
    public static ServerInfo.Software getServerSoftware() {
        return XG7Plugins.getInstance().getServerInfo().getSoftware();
    }

    /**
     * Obtém as informações completas do servidor.
     *
     * @return A instância de ServerInfo contendo informações sobre o servidor
     */
    public static ServerInfo getServerInfo() {
        return XG7Plugins.getInstance().getServerInfo();
    }
}
