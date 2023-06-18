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

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.bungeecord.command.BungeeCommand;
import net.william278.huskchat.bungeecord.event.BungeeEventDispatcher;
import net.william278.huskchat.bungeecord.getter.BungeePermsDataGetter;
import net.william278.huskchat.bungeecord.listener.BungeeListener;
import net.william278.huskchat.bungeecord.player.BungeePlayer;
import net.william278.huskchat.bungeecord.util.BungeeLogger;
import net.william278.huskchat.command.*;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.discord.WebhookDispatcher;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.getter.DefaultDataGetter;
import net.william278.huskchat.getter.LuckPermsDataGetter;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.placeholderparser.DefaultParser;
import net.william278.huskchat.placeholderparser.PAPIProxyBridgeParser;
import net.william278.huskchat.placeholderparser.Placeholders;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.util.Logger;
import org.bstats.bungeecord.Metrics;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public final class HuskChatBungee extends Plugin implements HuskChat {

    // Metrics ID
    private static final int METRICS_ID = 11882;

    // Instance provider
    private static HuskChatBungee instance;

    public static HuskChatBungee getInstance() {
        return instance;
    }

    // Adventure audiences
    private BungeeAudiences audiences;

    // Event dispatcher
    private BungeeEventDispatcher eventDispatcher;

    // Webhook dispatcher
    private WebhookDispatcher webhookDispatcher;

    // Message manager
    public MessageManager messageManager;

    // Player data fetcher
    public DataGetter playerDataGetter;

    // Placeholder Parser
    public Placeholders placeholders;

    @Override
    public void onLoad() {
        // Set instance for easy cross-class referencing
        instance = this;

        // Create the event dispatcher
        eventDispatcher = new BungeeEventDispatcher(getProxy());

        // Create audiences
        audiences = BungeeAudiences.create(this);
    }

    @Override
    public void onEnable() {
        // Load config
        reloadSettings();

        // Load messages
        reloadMessages();

        // Load saved social spy state
        PlayerCache.setDataFolder(getDataFolder());

        try {
            PlayerCache.loadSpy();
        } catch (IOException e) {
            getLoggingAdapter().log(Level.SEVERE, "Failed to load spies file");
        }

        // Setup player data getter
        Plugin luckPerms = ProxyServer.getInstance().getPluginManager().getPlugin("LuckPerms");
        if (luckPerms != null) {
            playerDataGetter = new LuckPermsDataGetter();
        } else {
            Plugin bungeePerms = ProxyServer.getInstance().getPluginManager().getPlugin("BungeePerms");
            if (bungeePerms != null) {
                playerDataGetter = new BungeePermsDataGetter();
            } else {
                playerDataGetter = new DefaultDataGetter();
            }
        }

        // Setup placeholder parser
        Plugin papiBridge = ProxyServer.getInstance().getPluginManager().getPlugin("PAPIProxyBridge");
        if (papiBridge != null) {
            placeholders = new PAPIProxyBridgeParser();
        } else {
            placeholders = new DefaultParser();
        }

        // Register events
        getProxy().getPluginManager().registerListener(this, new BungeeListener());

        // Register commands
        new BungeeCommand(new HuskChatCommand(this));
        new BungeeCommand(new ChannelCommand(this));

        if (Settings.doMessageCommand) {
            new BungeeCommand(new MsgCommand(this));
            new BungeeCommand(new ReplyCommand(this));
            new BungeeCommand(new OptOutMsgCommand(this));
        }

        if (Settings.doBroadcastCommand) {
            new BungeeCommand(new BroadcastCommand(this));
        }

        if (Settings.doSocialSpyCommand) {
            new BungeeCommand(new SocialSpyCommand(this));
        }

        if (Settings.doLocalSpyCommand) {
            new BungeeCommand(new LocalSpyCommand(this));
        }

        // Register shortcut commands
        Settings.channels.forEach((id, channel) -> {
            for (String command : channel.shortcutCommands) {
                new BungeeCommand(new ShortcutCommand(command, channel.id, this));
            }
        });

        // Initialize webhook dispatcher
        if (Settings.doDiscordIntegration) {
            webhookDispatcher = new WebhookDispatcher(Settings.webhookUrls);
        }

        // Initialise metrics
        new Metrics(this, METRICS_ID);

        // Plugin startup logic
        getLoggingAdapter().info("Enabled HuskChat version " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLoggingAdapter().info("Disabled HuskChat version " + getDescription().getVersion());
    }

    @Override
    public void reloadMessages() {
        this.messageManager = new MessageManager(this);
    }

    @NotNull
    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public void reloadSettings() {
        try {
            Settings.load(YamlDocument.create(new File(getDataFolder(), "config.yml"),
                    getResourceAsStream("config.yml"),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.builder().setEncoding(DumperSettings.Encoding.UNICODE).build(),
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build()));
        } catch (IOException e) {
            getLoggingAdapter().log(Level.SEVERE, "Failed to load config file");
        }
    }

    @NotNull
    @Override
    public String getMetaVersion() {
        return getDescription().getVersion();
    }

    @NotNull
    @Override
    public String getMetaDescription() {
        return getDescription().getDescription();
    }

    @NotNull
    @Override
    public String getMetaPlatform() {
        return ProxyServer.getInstance().getName();
    }

    @Override
    public Placeholders getParser() {
        return placeholders;
    }

    @Override
    public DataGetter getDataGetter() {
        return playerDataGetter;
    }

    @Override
    public Optional<WebhookDispatcher> getWebhookDispatcher() {
        if (webhookDispatcher != null) {
            return Optional.of(webhookDispatcher);
        }
        return Optional.empty();
    }

    @NotNull
    public BungeeEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Override
    public Optional<Player> getPlayer(UUID uuid) {
        final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
            return Optional.of(BungeePlayer.adaptCrossPlatform(player));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        ArrayList<Player> crossPlatform = new ArrayList<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            crossPlatform.add(BungeePlayer.adaptCrossPlatform(player));
        }
        return crossPlatform;
    }

    @Override
    public Collection<Player> getOnlinePlayersOnServer(Player player) {
        ArrayList<Player> crossPlatform = new ArrayList<>();
        BungeePlayer.adaptBungee(player).ifPresent(bungeePlayer -> {
            for (ProxiedPlayer playerOnServer : bungeePlayer.getServer().getInfo().getPlayers()) {
                crossPlatform.add(BungeePlayer.adaptCrossPlatform(playerOnServer));
            }
        });
        return crossPlatform;
    }

    @Override
    public Audience getConsoleAudience() {
        return audiences.console();
    }

    @NotNull
    @Override
    public Logger getLoggingAdapter() {
        return BungeeLogger.get();
    }

    @NotNull
    public BungeeAudiences getAudience() {
        return audiences;
    }

    @Override
    public Optional<Player> matchPlayer(String username) {
        if (username.isEmpty()) {
            return Optional.empty();
        }

        final Optional<Player> optionalPlayer;
        if (ProxyServer.getInstance().getPlayer(username) != null) {
            final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(username);
            optionalPlayer = Optional.of(BungeePlayer.adaptCrossPlatform(player));
        } else {
            final List<ProxiedPlayer> matchedPlayers = ProxyServer.getInstance().matchPlayer(username)
                    .stream().filter(val -> val.getName().startsWith(username)).sorted().toList();
            if (matchedPlayers.size() > 0) {
                optionalPlayer = Optional.of(BungeePlayer.adaptCrossPlatform(matchedPlayers.get(0)));
            } else {
                optionalPlayer = Optional.empty();
            }
        }
        return optionalPlayer;
    }
}
