package me.william278.huskchat.config;

import me.william278.huskchat.HuskChat;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigManager {

    private static final HuskChat plugin = HuskChat.getInstance();

    public static void loadConfig() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdir();
            }
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                Files.copy(plugin.getResourceAsStream("config.yml"), configFile.toPath());
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.CONFIG, "An exception occurred loading the configuration file", e);
        }
    }

    public static Configuration getConfig() {
        try {
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.CONFIG, "An IOException occurred fetching the configuration file", e);
            return null;
        }
    }

}
