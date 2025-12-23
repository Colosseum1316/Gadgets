package colosseum.prohibition;

import colosseum.prohibition.definition.AbstractDefinition;
import lombok.Getter;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.ServiceLoader;

public final class Prohibition extends JavaPlugin {
    @Getter
    private static Prohibition instance;

    private static final String CMD_PREFIX = "colosseum-prohibition";

    private ServiceLoader<AbstractDefinition> serviceLoader;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;

        FileConfiguration config = getConfig();
        if (serviceLoader == null) {
            serviceLoader = ServiceLoader.load(AbstractDefinition.class, AbstractDefinition.class.getClassLoader());
        }

        for (AbstractDefinition provider : serviceLoader) {
            getLogger().info("Configuring " + provider.getName());
            provider.configure(config);
        }
        config.options().copyDefaults(true);
        saveConfig();

        CommandMap commandMap = CommandUtils.getCommandMap();
        for (AbstractDefinition provider : serviceLoader) {
            String alias = "prohibition-" + provider.getName();
            PluginCommand command = CommandUtils.getPluginCommand(alias);
            command.setAliases(Collections.singletonList(alias));
            command.setDescription(provider.getName());
            command.setUsage("/" + alias);
            command.setExecutor(provider);
            if (provider instanceof TabCompleter) {
                command.setTabCompleter((TabCompleter) provider);
            }
            command.setPermission(String.format("colosseum.prohibition.%s;colosseum.prohibition.*", provider.getName()));
            commandMap.register(CMD_PREFIX, command);
            getLogger().info("Registering " + provider.getName());
        }

        for (AbstractDefinition provider : serviceLoader) {
            if (!provider.isEnabled()) {
                provider.activate();
            }
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
        if (serviceLoader != null) {
            for (AbstractDefinition provider : serviceLoader) {
                if (provider.isEnabled()) {
                    getLogger().info("Turning off " + provider.getName());
                    provider.activate();
                }
            }
        }
        instance = null;
    }
}
