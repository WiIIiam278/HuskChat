package net.william278.huskchat.config;

import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import de.exlll.configlib.YamlConfigurations;
import net.william278.huskchat.HuskChat;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Interface for getting and setting data from plugin configuration files
 *
 * @since 3.0
 */
public interface ConfigProvider {

    @NotNull
    YamlConfigurationProperties.Builder<?> YAML_CONFIGURATION_PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_UNDERSCORE);

    default void loadConfig() {
        loadSettings();
        loadChannels();
        loadFilterSettings();
        loadLocales();
    }

    /**
     * Get the plugin settings, read from the config file
     *
     * @return the plugin settings
     * @since 3.0
     */
    @NotNull
    Settings getSettings();

    /**
     * Set the plugin settings
     *
     * @param settings The settings to set
     * @since 3.0
     */
    void setSettings(@NotNull Settings settings);

    /**
     * Load the plugin settings from the config file
     *
     * @since 3.0
     */
    default void loadSettings() {
        setSettings(YamlConfigurations.update(
                getConfigDirectory().resolve("config.yml"),
                Settings.class,
                YAML_CONFIGURATION_PROPERTIES.header(Settings.CONFIG_HEADER).build()
        ));
    }

    /**
     * Get the channel settings, read from the config file
     *
     * @return the channel settings
     * @since 3.0
     */
    @NotNull
    Channels getChannels();

    /**
     * Set the plugin channel settings
     *
     * @param channels The channel settings to set
     * @since 3.0
     */
    void setChannels(@NotNull Channels channels);

    /**
     * Load the plugin channel settings from the config file
     *
     * @since 3.0
     */
    default void loadChannels() {
        setChannels(YamlConfigurations.update(
                getConfigDirectory().resolve("channels.yml"),
                Channels.class,
                YAML_CONFIGURATION_PROPERTIES.header(Channels.CONFIG_HEADER).build()
        ));
    }


    /**
     * Get the filter settings, read from the config file
     *
     * @return the Filter settings
     * @since 3.0
     */
    @NotNull
    Filters getFilterSettings();

    /**
     * Set the plugin filter settings
     *
     * @param Filters The Filter settings to set
     * @since 3.0
     */
    void setFilterSettings(@NotNull Filters Filters);

    /**
     * Load the plugin Filter settings from the config file
     *
     * @since 3.0
     */
    default void loadFilterSettings() {
        setFilterSettings(YamlConfigurations.update(
                getConfigDirectory().resolve("filters.yml"),
                Filters.class,
                YAML_CONFIGURATION_PROPERTIES.header(Filters.CONFIG_HEADER).build()
        ));
    }
    
    /**
     * Get the locales for the plugin
     *
     * @return the locales for the plugin
     * @since 3.0
     */
    @NotNull
    Locales getLocales();

    /**
     * Set the locales for the plugin
     *
     * @param locales The locales to set
     * @since 3.0
     */
    void setLocales(@NotNull Locales locales);

    /**
     * Load the locales from the config file
     *
     * @since 3.0
     */
    default void loadLocales() {
        final YamlConfigurationStore<Locales> store = new YamlConfigurationStore<>(
                Locales.class, YAML_CONFIGURATION_PROPERTIES.header(Locales.CONFIG_HEADER).build()
        );
        // Read existing locales if present
        final Path path = getConfigDirectory().resolve(String.format("messages-%s.yml", getSettings().getLanguage()));
        if (Files.exists(path)) {
            setLocales(store.load(path));
            return;
        }

        // Otherwise, save and read the default locales
        try (InputStream input = getResource(String.format("locales/%s.yml", getSettings().getLanguage()))) {
            final Locales locales = store.read(input);
            store.save(locales, path);
            setLocales(locales);
        } catch (Throwable e) {
            getPlugin().log(Level.SEVERE, "An error occurred loading the locales (invalid lang code?)", e);
        }
    }

    /**
     * Get a plugin resource
     *
     * @param name The name of the resource
     * @return the resource, if found
     * @since 3.0
     */
    InputStream getResource(@NotNull String name);

    /**
     * Get the plugin config directory
     *
     * @return the plugin config directory
     * @since 1.0
     */
    @NotNull
    Path getConfigDirectory();

    @NotNull
    HuskChat getPlugin();

}