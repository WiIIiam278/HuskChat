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

package net.william278.huskchat.bungeecord;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.william278.desertwell.util.Version;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.bungeecord.command.BungeeCommand;
import net.william278.huskchat.bungeecord.event.BungeeEventDispatcher;
import net.william278.huskchat.bungeecord.getter.BungeePermsDataGetter;
import net.william278.huskchat.bungeecord.listener.BungeeListener;
import net.william278.huskchat.bungeecord.player.BungeePlayer;
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
import org.bstats.bungeecord.Metrics;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

public final class BungeeHuskChat extends Plugin implements HuskChat {

    // bStats ID
    private static final int METRICS_ID = 11882;

    // Instance provider
    private static BungeeHuskChat instance;

    public static BungeeHuskChat getInstance() {
        return instance;
    }

    private List<BungeeCommand> commands;
    private BungeeAudiences audiences;
    private Settings settings;
    private BungeeEventDispatcher eventDispatcher;
    private Webhook webhook;
    private Locales locales;
    private DataGetter playerDataGetter;
    private PlayerCache playerCache;
    private List<PlaceholderReplacer> placeholders;

    @Override
    public void onLoad() {
        // Set instance for easy cross-class referencing
        instance = this;

        // Create the event dispatcher, register audiences
        eventDispatcher = new BungeeEventDispatcher(getProxy());
        audiences = BungeeAudiences.create(this);
    }

    @Override
    public void onEnable() {
        // Load config and locale files
        this.loadConfig();

        // Load saved social spy state
        this.playerCache = new PlayerCache(this);

        // Setup player data getter
        if (isPluginPresent("LuckPerms")) {
            this.playerDataGetter = new LuckPermsDataGetter();
        } else {
            if (isPluginPresent("BungeePerms")) {
                this.playerDataGetter = new BungeePermsDataGetter();
            } else {
                this.playerDataGetter = new DefaultDataGetter();
            }
        }

        // Setup placeholder parser
        this.placeholders = new ArrayList<>();
        this.placeholders.add(new DefaultReplacer(this));
        if (isPluginPresent("PAPIProxyBridge")) {
            this.placeholders.add(new PAPIProxyBridgeReplacer());
        }

        // Register events
        getProxy().getPluginManager().registerListener(this, new BungeeListener(this));

        // Register commands & channel shortcuts
        this.commands = new ArrayList<>(BungeeCommand.Type.getCommands(this));
        this.commands.addAll(getSettings().getChannels().values().stream()
                .flatMap(channel -> channel.getShortcutCommands().stream().map(command -> new BungeeCommand(
                        new ShortcutCommand(command, channel.getId(), this), this
                )))
                .toList());

        // Initialize webhook dispatcher
        if (getSettings().doDiscordIntegration()) {
            this.webhook = new Webhook(this);
        }

        // Initialise metrics and log
        new Metrics(this, METRICS_ID);
        this.checkForUpdates();
        log(Level.INFO, "Enabled HuskChat version " + this.getVersion());
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
    public Locales getLocales() {
        return locales;
    }

    @Override
    public void setLocales(@NotNull Locales locales) {
        this.locales = locales;
    }

    @NotNull
    @Override
    public Version getVersion() {
        return Version.fromString(getDescription().getVersion());
    }

    @NotNull
    @Override
    public String getPluginDescription() {
        return getDescription().getDescription();
    }

    @NotNull
    @Override
    public String getPlatform() {
        return ProxyServer.getInstance().getName();
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
    public Optional<Webhook> getWebhook() {
        return Optional.ofNullable(webhook);
    }

    @NotNull
    public BungeeEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Override
    @NotNull
    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    @Override
    public Optional<Player> getPlayer(@NotNull UUID uuid) {
        final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
            return Optional.of(BungeePlayer.adapt(player));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        ArrayList<Player> crossPlatform = new ArrayList<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            crossPlatform.add(BungeePlayer.adapt(player));
        }
        return crossPlatform;
    }

    @Override
    public Collection<Player> getOnlinePlayersOnServer(@NotNull Player player) {
        ArrayList<Player> crossPlatform = new ArrayList<>();
        BungeePlayer.toBungee(player).ifPresent(bungeePlayer -> {
            for (ProxiedPlayer playerOnServer : bungeePlayer.getServer().getInfo().getPlayers()) {
                crossPlatform.add(BungeePlayer.adapt(playerOnServer));
            }
        });
        return crossPlatform;
    }

    @Override
    @NotNull
    public Audience getConsole() {
        return audiences.console();
    }

    @Override
    public InputStream getResource(@NotNull String path) {
        return getResourceAsStream(path);
    }

    @Override
    public boolean isPluginPresent(@NotNull String dependency) {
        return ProxyServer.getInstance().getPluginManager().getPlugin(dependency) != null;
    }

    @Override
    public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... throwable) {
        getLogger().log(level, message, throwable);
    }

    @NotNull
    public BungeeAudiences getAudience() {
        return audiences;
    }

    @Override
    public Optional<Player> findPlayer(@NotNull String username) {
        if (username.isEmpty()) {
            return Optional.empty();
        }

        final Optional<Player> optionalPlayer;
        if (ProxyServer.getInstance().getPlayer(username) != null) {
            final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(username);
            optionalPlayer = Optional.of(BungeePlayer.adapt(player));
        } else {
            final List<ProxiedPlayer> matchedPlayers = ProxyServer.getInstance().matchPlayer(username)
                    .stream().filter(val -> val.getName().startsWith(username)).sorted().toList();
            if (matchedPlayers.size() > 0) {
                optionalPlayer = Optional.of(BungeePlayer.adapt(matchedPlayers.get(0)));
            } else {
                optionalPlayer = Optional.empty();
            }
        }
        return optionalPlayer;
    }

}
