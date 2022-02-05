package net.william278.huskchat.bungeecord.config;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.config.ConfigFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class BungeeConfigFile implements ConfigFile {

    private static final HuskChatBungee plugin = HuskChatBungee.getInstance();

    private Configuration config;

    public BungeeConfigFile(String sourceFile, String targetFile) {
        try {
            File configFile = new File(plugin.getDataFolder(), targetFile);
            if (!configFile.exists()) {
                try {
                    if (plugin.getDataFolder().mkdir()) {
                        plugin.getLogger().log(Level.CONFIG, "Created config directory");
                    }
                    File newConfig = new File(plugin.getDataFolder(), targetFile);
                    if (!newConfig.exists()) {
                        Files.copy(plugin.getResourceAsStream(sourceFile), newConfig.toPath());
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.CONFIG, "An exception occurred loading the configuration file", e);
                }
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.CONFIG, "An IOException occurred fetching the configuration file", e);
        }
    }

    @Override
    public String getString(String s) {
        return config.getString(s);
    }

    @Override
    public String getString(String s, String s1) {
        return config.getString(s, s1);
    }

    @Override
    public List<String> getStringList(String s) {
        return config.getStringList(s);
    }

    @Override
    public Integer getInteger(String s) {
        return config.getInt(s);
    }

    @Override
    public Integer getInteger(String s, int i) {
        return config.getInt(s, i);
    }

    @Override
    public Boolean getBoolean(String s) {
        return config.getBoolean(s);
    }

    @Override
    public Boolean getBoolean(String s, boolean b) {
        return config.getBoolean(s, b);
    }

    @Override
    public Boolean contains(String s) {
        return config.getBoolean(s);
    }

    @Override
    public Collection<String> getConfigKeys(String s) {
        return config.getSection(s).getKeys();
    }

    @Override
    public Collection<String> getConfigKeys() {
        return config.getKeys();
    }

}
