package com.xg7plugins.boot;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.core_commands.reload.ReloadCause;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.help.HelpMessenger;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.utils.Debug;
import lombok.*;
import org.apache.commons.lang.IllegalClassException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
/**
 * Classe base abstrata para plugins XG7 que estende JavaPlugin do Bukkit.
 * Fornece um framework estruturado para desenvolvimento de plugins com suporte a
 * configurações, comandos, eventos, banco de dados e gerenciamento de dependências.
 *
 * Esta classe gerencia o ciclo de vida do plugin incluindo carregamento, habilitação,
 * recarregamento e desabilitação.
 *
 * @author DaviXG7
 */
@Getter
public abstract class Plugin extends JavaPlugin {

    private final PluginSetup configurations;

    private final EnvironmentConfig environmentConfig;

    protected ManagerRegistry managerRegistry;
    protected Debug debug;

    protected HelpMessenger helpMessenger;


    public Plugin() {
        configurations = getClass().getAnnotation(PluginSetup.class);
        if (configurations == null) throw new IllegalClassException("PluginConfigurations annotation not found in " + getClass().getName());

        managerRegistry = new ManagerRegistry(this);
        this.environmentConfig = new EnvironmentConfig();
    }

    @Override
    public void onLoad() {
        environmentConfig.setPrefix(ChatColor.translateAlternateColorCodes('&', configurations.prefix()));
        environmentConfig.setCustomPrefix(environmentConfig.getPrefix());

        managerRegistry.registerManagers(
                new ConfigManager(this, configurations.configs()),
                new CommandManager(this)
        );
        debug = new Debug(this);

        debug.loading("Loading " + environmentConfig.getCustomPrefix() + "...");

        for (String cause : configurations.reloadCauses()) ReloadCause.registerCause(this, new ReloadCause(cause));

        XG7Plugins.register(this);
    }

    @Override
    public void onEnable() {
        if (configurations.onEnableDraw().length != 0) Arrays.stream(configurations.onEnableDraw()).forEach(Bukkit.getConsoleSender()::sendMessage);

        Config config = Config.mainConfigOf(this);

        environmentConfig.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', config.get("prefix", String.class).orElse(environmentConfig.getPrefix())));

        environmentConfig.setEnabledWorlds(config.getList("enabled-worlds", String.class).orElse(Collections.emptyList()));

        debug.loading("Custom prefix: " + environmentConfig.getCustomPrefix());
    }

    /**
     * Manipula o recarregamento do plugin com base na causa específica fornecida.
     * Este método recarrega apenas os componentes necessários com base no tipo de causa,
     * permitindo recargas parciais mais eficientes.
     * <p>

     * Comportamento:
     * <p>
     * - ReloadCause.CONFIG: Recarrega todas as configurações e reinicializa o debugger <p>
     * - ReloadCause.EVENTS: Recarrega todos os manipuladores de eventos e listeners de pacotes <p>
     * - ReloadCause.DATABASE: Reestabelece as conexões com o banco de dados <p>
     * - ReloadCause.LANGS: Limpa o cache de linguagens e recarrega os arquivos de idioma <p>
     * - ReloadCause.TASKS: Cancela todas as tarefas em execução e as reinicia <p>
     * - ReloadCause.Personalizado: Você pode criar uma causa para manipular outro tipo de recarregamento <p>
     * <p>

     * @param cause A causa do recarregamento que determina quais componentes serão recarregados
     *              Causas padrões incluem: CONFIG, EVENTS, DATABASE, LANGS, TASKS
     * <p>
     */
    public void onReload(ReloadCause cause) {

        XG7Plugins xg7Plugin = XG7Plugins.getInstance();

        if (cause.equals(ReloadCause.CONFIG)) {
            XG7PluginsAPI.configManager(xg7Plugin).reloadConfigs();
            debug = new Debug(this);
        }
        if (cause.equals(ReloadCause.EVENTS)) {
            XG7PluginsAPI.eventManager().reloadEvents(this);
            XG7PluginsAPI.packetEventManager().reloadListeners(this);
        }
        if (cause.equals(ReloadCause.DATABASE)) {
            XG7PluginsAPI.database().reloadConnection(this);
        }
        if (cause.equals(ReloadCause.LANGS)) {
            XG7PluginsAPI.langManager().clearCache();
            XG7PluginsAPI.langManager().loadLangsFrom(this);
        }
        if (cause.equals(ReloadCause.TASKS)) {
            XG7PluginsAPI.taskManager().cancelTasks(this);
            XG7PluginsAPI.taskManager().reloadTasks(this);
        }

    };


    @Override
    public void onDisable() {
        debug.loading("Disabling " + environmentConfig.getCustomPrefix() + "...");

    }

    public <T extends EnvironmentConfig> T getEnvironmentConfig() {
        return (T) environmentConfig;
    }

    /**
     * Carrega as entidades de banco de dados do plugin.
     *
     * @return Um array de classes que estendem Entity, usado para mapeamento de objetos no banco de dados
     */
    public Class<? extends Entity<?,?>>[] loadEntities() {
        return null;
    }

    /**
     * Carrega os comandos do plugin que serão registrados automaticamente.
     *
     * @return Uma lista de comandos a serem registrados pelo sistema de comandos
     */
    public List<Command> loadCommands() {
        return null;
    }

    /**
     * Carrega os listeners de eventos do Bukkit para este plugin.
     *
     * @return Uma lista de listeners a serem registrados pelo gerenciador de eventos
     */
    public List<Listener> loadEvents() {
        return null;
    }

    /**
     * Carrega os listeners de pacotes de rede para este plugin.
     *
     * @return Uma lista de packet listeners a serem registrados
     */
    public List<PacketListener> loadPacketEvents() {
        return null;
    }

    /**
     * Carrega as tarefas repetitivas (schedulers) para o plugin.
     *
     * @return Uma lista de tarefas a serem executadas periodicamente
     */
    public List<Task> loadRepeatingTasks() {
        return null;
    }

    /**
     * Configura o sistema de ajuda do plugin.
     * Este método deve ser implementado para registrar mensagens de ajuda.
     */
    public abstract void loadHelp();

    /**
     * Carrega as dependências opcionais do plugin.
     *
     * @return Uma lista de dependências que o plugin pode utilizar
     */
    public List<Dependency> loadDependencies() {
        return null;
    }

    /**
     * Carrega as dependências obrigatórias do plugin.
     *
     * @return Uma lista de dependências que são necessárias para o funcionamento do plugin
     */
    public List<Dependency> loadRequiredDependencies() {
        return null;
    }

}
