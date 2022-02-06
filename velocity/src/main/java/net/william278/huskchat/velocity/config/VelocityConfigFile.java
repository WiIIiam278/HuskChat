package net.william278.huskchat.velocity.config;

import com.google.common.reflect.TypeToken;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.ConfigFile;
import net.william278.huskchat.velocity.HuskChatVelocity;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

public class VelocityConfigFile implements ConfigFile {

    private static final HuskChatVelocity plugin = HuskChatVelocity.getInstance();

    private ConfigurationNode rootNode;

    public VelocityConfigFile(String origin, String target) {
        // Ensure config is present to read; copy default file otherwise
        File targetFile = new File(plugin.getDataFolder(), target);
        if (!targetFile.exists()) {
            if (targetFile.toPath().getParent().toFile().mkdirs()) {
                plugin.getLoggingAdapter().log(Level.CONFIG, "Created HuskSync data folder");
            }
            try {
                Files.copy(Objects.requireNonNull(HuskChat.class.getClassLoader().getResourceAsStream(origin)), targetFile.toPath());
            } catch (IOException e) {
                plugin.getLoggingAdapter().log(Level.SEVERE, "An IOException occurred copying the default config", e);
                return;
            }
        }

        // Load config file
        try {
            rootNode = YAMLConfigurationLoader.builder()
                    .setPath(targetFile.toPath())
                    .setFlowStyle(DumperOptions.FlowStyle.BLOCK)
                    .setIndent(2)
                    .build()
                    .load();
        } catch (IOException e) {
            plugin.getLoggingAdapter().log(Level.SEVERE, "An IOException occurred loading the config file");
        }
    }

    private ConfigurationNode navigateTo(String path) {
        final Object[] pathToNode = path.split("\\.");
        return rootNode.getNode(pathToNode);
    }

    @Override
    public String getString(String path) {
        return navigateTo(path).getString();
    }

    @Override
    public String getString(String path, String defaultValue) {
        return navigateTo(path).getString(defaultValue);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public List<String> getStringList(String path) {
        try {
            return navigateTo(path).getList(new TypeToken<>() {
            });
        } catch (ObjectMappingException ignored) {
        }
        return new ArrayList<>();
    }

    @Override
    public Integer getInteger(String path) {
        return navigateTo(path).getInt();
    }

    @Override
    public Integer getInteger(String path, int defaultValue) {
        return navigateTo(path).getInt(defaultValue);
    }

    @Override
    public Boolean getBoolean(String path) {
        return navigateTo(path).getBoolean();
    }

    @Override
    public Boolean getBoolean(String path, boolean defaultValue) {
        return navigateTo(path).getBoolean(defaultValue);
    }

    @Override
    public Boolean contains(String path) {
        return !navigateTo(path).isVirtual();
    }

    @Override
    public Collection<String> getConfigKeys(String path) {
        ArrayList<String> childKeys = new ArrayList<>();
        for (ConfigurationNode node : navigateTo(path).getChildrenMap().values()) {
            childKeys.add((String) node.getKey());
        }
        return childKeys;
    }

    @Override
    public Collection<String> getConfigKeys() {
        ArrayList<String> childKeys = new ArrayList<>();
        for (ConfigurationNode node : rootNode.getChildrenMap().values()) {
            childKeys.add((String) node.getKey());
        }
        return childKeys;
    }
}
