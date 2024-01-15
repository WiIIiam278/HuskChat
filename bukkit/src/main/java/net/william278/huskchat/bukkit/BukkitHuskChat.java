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

package net.william278.huskchat.bukkit;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.william278.desertwell.util.Version;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.HuskChatAPI;
import net.william278.huskchat.bukkit.command.BukkitCommand;
import net.william278.huskchat.bukkit.event.BukkitEventDispatcher;
import net.william278.huskchat.bukkit.listener.BukkitListener;
import net.william278.huskchat.bukkit.placeholders.PlaceholderAPIReplacer;
import net.william278.huskchat.bukkit.player.BukkitPlayer;
import net.william278.huskchat.command.ShortcutCommand;
import net.william278.huskchat.config.Locales;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.discord.DiscordHook;
import net.william278.huskchat.event.EventDispatcher;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.getter.DefaultDataGetter;
import net.william278.huskchat.getter.LuckPermsDataGetter;
import net.william278.huskchat.placeholders.DefaultReplacer;
import net.william278.huskchat.placeholders.PlaceholderReplacer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.MorePaperLib;

import java.util.*;
import java.util.logging.Level;

public class BukkitHuskChat extends JavaPlugin implements HuskChat {

    // Instance provider
    private static BukkitHuskChat instance;

    public static BukkitHuskChat getInstance() {
        return instance;
    }

    private MorePaperLib morePaperLib;
    private BukkitAudiences audiences;
    private Settings settings;
    private List<BukkitCommand> commands;
    private BukkitEventDispatcher eventDispatcher;
    private DiscordHook discordHook;
    private Locales locales;
    private DataGetter playerDataGetter;
    private PlayerCache playerCache;
    private List<PlaceholderReplacer> placeholders;
    private BukkitHuskChatAPI api;

    @Override
    public void onLoad() {
        // Set instance for easy cross-class referencing
        instance = this;
        api = new BukkitHuskChatAPI(this);
    }

    @Override
    public void onEnable() {
        // Register audiences
        audiences = BukkitAudiences.create(this);

        // Load morePaperLib
        this.morePaperLib = new MorePaperLib(this);

        // Load config and locale files
        this.loadConfig();

        // Load discord hook
        this.loadDiscordHook();

        // Load saved social spy state
        this.playerCache = new PlayerCache(this);

        // Setup player data getter
        if (isPluginPresent("LuckPerms")) {
            this.playerDataGetter = new LuckPermsDataGetter();
        } else {
            this.playerDataGetter = new DefaultDataGetter();
        }

        // Setup placeholder parser
        this.placeholders = new ArrayList<>();
        this.placeholders.add(new DefaultReplacer(this));
        if (getSettings().doPlaceholderAPI() && isPluginPresent("PlaceholderAPI")) {
            this.placeholders.add(new PlaceholderAPIReplacer());
        }

        // Create the event dispatcher
        eventDispatcher = new BukkitEventDispatcher(this);

        // Register events
        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);

        // Register commands & channel shortcuts
        commands = new ArrayList<>(BukkitCommand.Type.getCommands(this));
        commands.addAll(getSettings().getChannels().values().stream()
                .flatMap(channel -> channel.getShortcutCommands().stream().map(command -> new BukkitCommand(
                        new ShortcutCommand(command, channel.getId(), this), this
                )))
                .toList());

        // Initialise metrics and log
        this.checkForUpdates();
        log(Level.INFO, "Enabled HuskChat version " + this.getVersion());
    }

    @NotNull
    @Override
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
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @NotNull
    @Override
    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    @NotNull
    @Override
    public List<PlaceholderReplacer> getPlaceholderReplacers() {
        return placeholders;
    }

    @NotNull
    @Override
    public DataGetter getDataGetter() {
        return playerDataGetter;
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
        return Version.fromString(getDescription().getVersion());
    }

    @NotNull
    @Override
    public String getPluginDescription() {
        return Optional.ofNullable(getDescription().getDescription())
                .orElseThrow(() -> new IllegalStateException("Plugin description not found"));
    }

    @NotNull
    @Override
    public String getPlatform() {
        return getServer().getName();
    }

    @Override
    public Optional<Player> getPlayer(@NotNull UUID uuid) {
        return Optional.ofNullable(getServer().getPlayer(uuid))
                .map(BukkitPlayer::adapt);
    }

    @Override
    public Optional<Player> findPlayer(@NotNull String username) {
        return Optional.ofNullable(getServer().getPlayerExact(username))
                .map(BukkitPlayer::adapt);
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return getServer().getOnlinePlayers().stream()
                .map(user -> (Player) BukkitPlayer.adapt(user))
                .toList();
    }

    @Override
    public Collection<Player> getOnlinePlayersOnServer(@NotNull Player player) {
        return getOnlinePlayers();
    }

    @NotNull
    @Override
    public Audience getConsole() {
        return audiences.console();
    }

    @Override
    public boolean isPluginPresent(@NotNull String dependency) {
        return getServer().getPluginManager().getPlugin(dependency) != null;
    }

    @Override
    public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... exceptions) {
        if (exceptions.length > 0) {
            getLogger().log(level, message, exceptions[0]);
            return;
        }
        getLogger().log(level, message);
    }

    @Override
    public HuskChatAPI getAPI() {
        return api;
    }

    @NotNull
    public BukkitAudiences getAudience() {
        return audiences;
    }

    @NotNull
    public CommandMap getCommandMap() {
        return morePaperLib.commandRegistration().getServerCommandMap();
    }


}
