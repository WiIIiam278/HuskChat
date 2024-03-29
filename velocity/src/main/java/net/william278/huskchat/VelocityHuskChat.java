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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.william278.desertwell.util.Version;
import net.william278.huskchat.api.VelocityHuskChatAPI;
import net.william278.huskchat.command.ShortcutCommand;
import net.william278.huskchat.command.VelocityCommand;
import net.william278.huskchat.config.Channels;
import net.william278.huskchat.config.Filters;
import net.william278.huskchat.config.Locales;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.discord.DiscordHook;
import net.william278.huskchat.event.VelocityEventProvider;
import net.william278.huskchat.filter.ChatFilter;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.getter.DefaultDataGetter;
import net.william278.huskchat.getter.LuckPermsDataGetter;
import net.william278.huskchat.listener.VelocityEventChatListener;
import net.william278.huskchat.listener.VelocityPacketChatListener;
import net.william278.huskchat.listener.VelocityPlayerListener;
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

import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

@Plugin(id = "huskchat")
@Getter
public class VelocityHuskChat implements HuskChat, VelocityEventProvider {

    // bStats ID
    private static final int METRICS_ID = 14187;

    // Plugin version
    private final PluginContainer container;
    private final Logger logger;
    private final Metrics.Factory metrics;
    private final Path configDirectory;
    private final ProxyServer server;
    private final List<ChatFilter> filtersAndReplacers = new ArrayList<>();
    private final List<PlaceholderReplacer> placeholderReplacers = new ArrayList<>();

    @Setter
    private Settings settings;
    @Setter
    private Locales locales;
    @Setter
    private Channels channels;
    @Setter
    private Filters filterSettings;
    @Setter
    private UserCache.Editor userCache;
    @Setter
    @Getter(AccessLevel.NONE)
    private DiscordHook discordHook;
    private DataGetter dataGetter;

    @Inject
    public VelocityHuskChat(@NotNull ProxyServer server, @NotNull org.slf4j.Logger logger,
                            @DataDirectory Path configDirectory, @NotNull Metrics.Factory metrics,
                            @NotNull PluginContainer pluginContainer) {
        this.server = server;
        this.logger = logger;
        this.configDirectory = configDirectory;
        this.metrics = metrics;
        this.container = pluginContainer;
    }

    @Subscribe
    public void onProxyInitialization(@NotNull ProxyInitializeEvent event) {
        // Load config and locale files
        this.loadConfig();

        // Load discord hook
        this.loadDiscordHook();

        // Setup player data getter
        if (isPluginPresent("luckperms")) {
            this.dataGetter = new LuckPermsDataGetter();
        } else {
            this.dataGetter = new DefaultDataGetter();
        }

        // Setup PlaceholderParser
        this.placeholderReplacers.add(new DefaultReplacer(this));
        if (getSettings().getPlaceholder().isUsePapi() && isPluginPresent("papiproxybridge")) {
            this.placeholderReplacers.add(new PAPIProxyBridgeReplacer(this));
        }

        // Register events
        getProxyServer().getEventManager().register(this, new VelocityPlayerListener(this));
        if (getSettings().isUsePacketListening()) {
            new VelocityPacketChatListener(this).register();
        } else {
            getProxyServer().getEventManager().register(this, new VelocityEventChatListener(this));
        }

        // Register commands & channel shortcuts
        VelocityCommand.Type.registerAll(this);
        getChannels().getChannels().forEach(channel -> channel.getShortcutCommands()
                .forEach(command -> new VelocityCommand(
                        new ShortcutCommand(command, channel.getId(), this), this
                )));
        getPlugin().log(Level.INFO, String.format("Loaded %s channels with %s associated shortcut commands",
                getChannels().getChannels().size(), getChannels().getChannels().stream()
                        .mapToInt(channel -> channel.getShortcutCommands().size()).sum()));

        VelocityHuskChatAPI.register(this);

        // Initialise metrics and log
        this.metrics.make(this, METRICS_ID);
        this.checkForUpdates();
        log(Level.INFO, "Enabled HuskChat version " + getVersion());
    }

    @Override
    public Optional<DiscordHook> getDiscordHook() {
        return Optional.ofNullable(discordHook);
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


    @Override
    public Optional<OnlineUser> getPlayer(@NotNull UUID uuid) {
        return getProxyServer().getPlayer(uuid).map(player -> VelocityUser.adapt(player, this));
    }

    @Override
    @NotNull
    public Collection<OnlineUser> getOnlinePlayers() {
        return getProxyServer().getAllPlayers().stream()
                .map(player -> (OnlineUser) VelocityUser.adapt(player, this)).toList();
    }

    @Override
    @NotNull
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

    @Nullable
    @Override
    public InputStream getResource(@NotNull String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
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
