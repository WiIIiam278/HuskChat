/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.audience.Audience;
import net.william278.desertwell.util.Version;
import net.william278.huskchat.api.VelocityHuskChatAPI;
import net.william278.huskchat.command.ShortcutCommand;
import net.william278.huskchat.command.VelocityCommand;
import net.william278.huskchat.config.Locales;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.discord.DiscordHook;
import net.william278.huskchat.event.VelocityEventDispatcher;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.getter.DefaultDataGetter;
import net.william278.huskchat.getter.LuckPermsDataGetter;
import net.william278.huskchat.listener.VelocityListener;
import net.william278.huskchat.placeholders.DefaultReplacer;
import net.william278.huskchat.placeholders.PAPIProxyBridgeReplacer;
import net.william278.huskchat.placeholders.PlaceholderReplacer;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.UserCache;
import net.william278.huskchat.user.VelocityUser;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

@Plugin(id = "huskchat")
public class VelocityHuskChat implements HuskChat {

    // bStats ID
    private static final int METRICS_ID = 14187;

    // Plugin version
    private final PluginContainer container;
    private final Logger logger;
    private final Metrics.Factory metrics;
    private final Path dataDirectory;
    private final ProxyServer server;
    private Settings settings;
    private VelocityEventDispatcher eventDispatcher;
    private DiscordHook discordHook;
    private Locales locales;
    private DataGetter playerDataGetter;
    private UserCache userCache;
    private List<PlaceholderReplacer> placeholders;

    @Inject
    public VelocityHuskChat(@NotNull ProxyServer server, @NotNull org.slf4j.Logger logger,
                            @DataDirectory Path dataDirectory, @NotNull Metrics.Factory metrics,
                            @NotNull PluginContainer pluginContainer) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metrics = metrics;
        this.container = pluginContainer;
    }

    @Subscribe
    public void onProxyInitialization(@NotNull ProxyInitializeEvent event) {
        // Check plugin compat
        if (!isSigningPluginInstalled()) {
            return;
        }

        // Load config and locale files
        this.loadConfig();

        // Load discord hook
        this.loadDiscordHook();

        // Load event dispatcher
        this.eventDispatcher = new VelocityEventDispatcher(server);

        // Load saved social spy state
        this.userCache = new UserCache(this);

        // Setup player data getter
        if (isPluginPresent("luckperms")) {
            this.playerDataGetter = new LuckPermsDataGetter();
        } else {
            this.playerDataGetter = new DefaultDataGetter();
        }

        // Setup PlaceholderParser
        this.placeholders = new ArrayList<>();
        this.placeholders.add(new DefaultReplacer(this));
        if (getSettings().doPlaceholderAPI() && isPluginPresent("papiproxybridge")) {
            this.placeholders.add(new PAPIProxyBridgeReplacer(this));
        }

        // Register events
        getProxyServer().getEventManager().register(this, new VelocityListener(this));

        // Register commands & channel shortcuts
        VelocityCommand.Type.registerAll(this);
        getSettings().getChannels().values().forEach(channel -> channel.getShortcutCommands()
                .forEach(command -> new VelocityCommand(
                        new ShortcutCommand(command, channel.getId(), this), this
                )));

        VelocityHuskChatAPI.register(this);

        // Initialise metrics and log
        this.metrics.make(this, METRICS_ID);
        this.checkForUpdates();
        log(Level.INFO, "Enabled HuskChat version " + getVersion());
    }

    // Ensures a signing plugin is installed
    private boolean isSigningPluginInstalled() {
        boolean usvPresent = isPluginPresent("unsignedvelocity");
        boolean svPresent = isPluginPresent("signedvelocity");
        if (usvPresent && svPresent) {
            log(Level.SEVERE, "Both UnsignedVelocity and SignedVelocity are present!\n" +
                    "Please uninstall UnsignedVelocity. HuskChat will now be disabled."
            );
            return false;
        }
        if (!(usvPresent || svPresent)) {
            log(Level.WARNING, "Neither UnsignedVelocity nor SignedVelocity are present!\n" +
                    "Install SignedVelocity (https://modrinth.com/plugin/signedvelocity) for 1.19+ support.");
        } else if (usvPresent) {
            log(Level.WARNING, "UnsignedVelocity is deprecated; please install SignedVelocity " +
                    " (https://modrinth.com/plugin/signedvelocity) instead for better support.");
        }
        return true;
    }

    @NotNull
    @Override
    public Locales getLocales() {
        return locales;
    }

    @Override
    public void setLocales(@NotNull Locales locales) {
        this.locales = locales;
    }

    @Override
    @NotNull
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void setSettings(@NotNull Settings settings) {
        this.settings = settings;
    }

    @NotNull
    @Override
    public VelocityEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Override
    @NotNull
    public UserCache getPlayerCache() {
        return userCache;
    }


    @Override
    public Optional<DiscordHook> getDiscordHook() {
        return Optional.ofNullable(discordHook);
    }

    @Override
    public void setDiscordHook(@NotNull DiscordHook discordHook) {
        this.discordHook = discordHook;
    }

    @NotNull
    @Override
    public Version getVersion() {
        return Version.fromString(container.getDescription().getVersion()
                .orElseThrow(() -> new IllegalStateException("Could not fetch plugin version from container")));
    }

    @NotNull
    @Override
    public String getPluginDescription() {
        return container.getDescription().getDescription().orElse("Unknown");
    }

    @NotNull
    @Override
    public String getPlatform() {
        return "Velocity";
    }


    @NotNull
    @Override
    public List<PlaceholderReplacer> getPlaceholderReplacers() {
        return placeholders;
    }

    @Override
    @NotNull
    public DataGetter getDataGetter() {
        return playerDataGetter;
    }

    @Override
    public Optional<OnlineUser> getPlayer(@NotNull UUID uuid) {
        return getProxyServer().getPlayer(uuid).map(player -> VelocityUser.adapt(player, this));
    }

    @Override
    public Collection<OnlineUser> getOnlinePlayers() {
        return getProxyServer().getAllPlayers().stream()
                .map(player -> (OnlineUser) VelocityUser.adapt(player, this)).toList();
    }

    @Override
    public Collection<OnlineUser> getOnlinePlayersOnServer(@NotNull OnlineUser user) {
        return ((VelocityUser) user).getPlayer().getCurrentServer()
                .map(conn -> conn.getServer().getPlayersConnected().stream()
                        .map(player -> (OnlineUser) VelocityUser.adapt(player, this)).toList())
                .orElseGet(Collections::emptyList);
    }

    @Override
    public Optional<OnlineUser> findPlayer(@NotNull String username) {
        if (username.isEmpty()) {
            return Optional.empty();
        }

        final Optional<OnlineUser> optionalPlayer;
        if (getProxyServer().getPlayer(username).isPresent()) {
            final com.velocitypowered.api.proxy.Player player = getProxyServer().getPlayer(username).get();
            optionalPlayer = Optional.of(VelocityUser.adapt(player, this));
        } else {
            final List<com.velocitypowered.api.proxy.Player> matchedPlayers = getProxyServer().matchPlayer(username)
                    .stream().filter(val -> val.getUsername().startsWith(username)).sorted().toList();
            if (!matchedPlayers.isEmpty()) {
                optionalPlayer = Optional.of(VelocityUser.adapt(matchedPlayers.get(0), this));
            } else {
                optionalPlayer = Optional.empty();
            }
        }
        return optionalPlayer;
    }


    // Get the data folder
    @NotNull
    @Override
    public File getDataFolder() {
        return dataDirectory.toFile();
    }

    @Nullable
    @Override
    public InputStream getResource(@NotNull String path) {
        return HuskChat.class.getClassLoader().getResourceAsStream(path);
    }

    @Override
    public boolean isPluginPresent(@NotNull String dependency) {
        return getProxyServer().getPluginManager().getPlugin(dependency.toLowerCase(Locale.ENGLISH)).isPresent();
    }

    @NotNull
    public ProxyServer getProxyServer() {
        return server;
    }

    @Override
    public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... exceptions) {
        switch (level.getName()) {
            case "SEVERE" -> {
                if (exceptions.length > 0) {
                    logger.error(message, exceptions[0]);
                } else {
                    logger.error(message);
                }
            }
            case "WARNING" -> {
                if (exceptions.length > 0) {
                    logger.warn(message, exceptions[0]);
                } else {
                    logger.warn(message);
                }
            }
            default -> logger.info(message);
        }
    }

    @NotNull
    @Override
    public Audience getAudience(@NotNull UUID user) {
        return getProxyServer().getPlayer(user).map(player -> (Audience) player).orElse(Audience.empty());
    }

    @NotNull
    @Override
    public Audience getConsole() {
        return getProxyServer().getConsoleCommandSource();
    }

    @NotNull
    @Override
    public HuskChat getPlugin() {
        return this;
    }
}
