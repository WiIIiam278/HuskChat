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

package net.william278.huskchat.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.audience.Audience;
import net.william278.desertwell.util.Version;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.command.ShortcutCommand;
import net.william278.huskchat.config.Locales;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.config.Webhook;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.getter.DefaultDataGetter;
import net.william278.huskchat.getter.LuckPermsDataGetter;
import net.william278.huskchat.placeholders.DefaultReplacer;
import net.william278.huskchat.placeholders.PAPIProxyBridgeReplacer;
import net.william278.huskchat.placeholders.PlaceholderReplacer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.velocity.command.VelocityCommand;
import net.william278.huskchat.velocity.event.VelocityEventDispatcher;
import net.william278.huskchat.velocity.listener.VelocityListener;
import net.william278.huskchat.velocity.player.VelocityPlayer;
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

    // Instance provider
    private static VelocityHuskChat instance;

    public static VelocityHuskChat getInstance() {
        return instance;
    }

    // Plugin version
    private final PluginContainer container;
    private final Logger logger;
    private final Metrics.Factory metrics;
    private final Path dataDirectory;
    private final ProxyServer server;
    private List<VelocityCommand> commands;
    private Settings settings;
    private VelocityEventDispatcher eventDispatcher;
    private Webhook webhook;
    private Locales locales;
    private DataGetter playerDataGetter;
    private PlayerCache playerCache;
    private List<PlaceholderReplacer> placeholders;

    @Inject
    public VelocityHuskChat(@NotNull ProxyServer server, @NotNull org.slf4j.Logger logger,
                            @DataDirectory Path dataDirectory, @NotNull Metrics.Factory metrics,
                            @NotNull PluginContainer pluginContainer) {
        instance = this;

        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metrics = metrics;
        this.container = pluginContainer;
    }

    @Subscribe
    public void onProxyInitialization(@NotNull ProxyInitializeEvent event) {
        // Load config and locale files
        this.loadConfig();
        this.eventDispatcher = new VelocityEventDispatcher(server);

        // Load saved social spy state
        this.playerCache = new PlayerCache(this);

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
        this.commands = new ArrayList<>(VelocityCommand.Type.getCommands(this));
        this.commands.addAll(getSettings().getChannels().values().stream()
                .flatMap(channel -> channel.getShortcutCommands().stream().map(command -> new VelocityCommand(
                        new ShortcutCommand(command, channel.getId(), this), this
                )))
                .toList());

        // Initialize webhook dispatcher
        if (getSettings().doDiscordIntegration()) {
            this.webhook = new Webhook(this);
        }

        // Initialise metrics and log
        this.metrics.make(this, METRICS_ID);
        this.checkForUpdates();
        log(Level.INFO, "Enabled HuskChat version " + getVersion());
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
    public PlayerCache getPlayerCache() {
        return playerCache;
    }


    @Override
    public Optional<Webhook> getWebhook() {
        return Optional.ofNullable(webhook);
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
    public Optional<Player> getPlayer(@NotNull UUID uuid) {
        final Optional<com.velocitypowered.api.proxy.Player> player = getProxyServer().getPlayer(uuid);
        return player.map(VelocityPlayer::adapt);
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        final ArrayList<Player> velocityPlayers = new ArrayList<>();
        for (com.velocitypowered.api.proxy.Player player : getProxyServer().getAllPlayers()) {
            velocityPlayers.add(VelocityPlayer.adapt(player));
        }
        return velocityPlayers;
    }

    @Override
    public Collection<Player> getOnlinePlayersOnServer(@NotNull Player serverPlayer) {
        final ArrayList<Player> velocityPlayers = new ArrayList<>();
        VelocityPlayer.toVelocity(serverPlayer).flatMap(com.velocitypowered.api.proxy.Player::getCurrentServer).ifPresent(serverConnection -> {
            for (com.velocitypowered.api.proxy.Player connectedPlayer : serverConnection.getServer().getPlayersConnected()) {
                velocityPlayers.add(VelocityPlayer.adapt(connectedPlayer));
            }
        });
        return velocityPlayers;
    }

    @Override
    @NotNull
    public Audience getConsole() {
        return getProxyServer().getConsoleCommandSource();
    }

    @Override
    public Optional<Player> findPlayer(@NotNull String username) {
        if (username.isEmpty()) {
            return Optional.empty();
        }

        final Optional<Player> optionalPlayer;
        if (getProxyServer().getPlayer(username).isPresent()) {
            final com.velocitypowered.api.proxy.Player player = getProxyServer().getPlayer(username).get();
            optionalPlayer = Optional.of(VelocityPlayer.adapt(player));
        } else {
            final List<com.velocitypowered.api.proxy.Player> matchedPlayers = getProxyServer().matchPlayer(username)
                    .stream().filter(val -> val.getUsername().startsWith(username)).sorted().toList();
            if (matchedPlayers.size() > 0) {
                optionalPlayer = Optional.of(VelocityPlayer.adapt(matchedPlayers.get(0)));
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
        return getProxyServer().getPluginManager().getPlugin(dependency).isPresent();
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

}
