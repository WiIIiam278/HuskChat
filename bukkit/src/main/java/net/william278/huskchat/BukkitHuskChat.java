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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.william278.desertwell.util.Version;
import net.william278.huskchat.api.BukkitHuskChatAPI;
import net.william278.huskchat.command.BukkitCommand;
import net.william278.huskchat.command.ShortcutCommand;
import net.william278.huskchat.config.Channels;
import net.william278.huskchat.config.Filters;
import net.william278.huskchat.config.Locales;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.discord.DiscordHook;
import net.william278.huskchat.event.BukkitEventProvider;
import net.william278.huskchat.filter.ChatFilter;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.getter.DefaultDataGetter;
import net.william278.huskchat.getter.LuckPermsDataGetter;
import net.william278.huskchat.listener.BukkitListener;
import net.william278.huskchat.placeholders.BukkitPlaceholderAPIReplacer;
import net.william278.huskchat.placeholders.DefaultReplacer;
import net.william278.huskchat.placeholders.PlaceholderReplacer;
import net.william278.huskchat.user.BukkitUser;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.UserCache;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.MorePaperLib;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

@Getter
public class BukkitHuskChat extends JavaPlugin implements HuskChat, BukkitEventProvider {

    private MorePaperLib morePaperLib;
    private BukkitAudiences audiences;
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

        // Setup player data getter
        if (isPluginPresent("LuckPerms")) {
            this.dataGetter = new LuckPermsDataGetter();
        } else {
            this.dataGetter = new DefaultDataGetter();
        }

        // Setup placeholder parser
        this.placeholderReplacers.add(new DefaultReplacer(this));
        if (getSettings().getPlaceholder().isUsePapi() && isPluginPresent("PlaceholderAPI")) {
            this.placeholderReplacers.add(new BukkitPlaceholderAPIReplacer());
        }

        // Register events
        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);

        // Register commands & channel shortcuts
        BukkitCommand.Type.registerAll(this);
        getChannels().getChannels().forEach(channel -> channel.getShortcutCommands()
                .forEach(command -> new BukkitCommand(
                        new ShortcutCommand(command, channel.getId(), this), this
                )));

        // Register API
        BukkitHuskChatAPI.register(this);

        // Initialise metrics and log
        this.checkForUpdates();
        log(Level.INFO, "Enabled HuskChat version " + this.getVersion());
    }

    @Override
    public Optional<DiscordHook> getDiscordHook() {
        return Optional.ofNullable(discordHook);
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
    public Optional<OnlineUser> getPlayer(@NotNull UUID uuid) {
        return Optional.ofNullable(getServer().getPlayer(uuid))
                .map(player -> BukkitUser.adapt(player, this));
    }

    @Override
    public Optional<OnlineUser> findPlayer(@NotNull String username) {
        return Optional.ofNullable(getServer().getPlayer(username))
                .map(player -> BukkitUser.adapt(player, this));
    }

    @Override
    public @NotNull Collection<OnlineUser> getOnlinePlayers() {
        return getServer().getOnlinePlayers().stream()
                .map(user -> (OnlineUser) BukkitUser.adapt(user, this))
                .toList();
    }

    @Override
    public @NotNull Collection<OnlineUser> getOnlinePlayersOnServer(@NotNull OnlineUser player) {
        return getOnlinePlayers();
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

    @NotNull
    public CommandMap getCommandMap() {
        return morePaperLib.commandRegistration().getServerCommandMap();
    }


    @NotNull
    @Override
    public Audience getAudience(@NotNull UUID user) {
        return audiences.player(user);
    }

    @NotNull
    @Override
    public Audience getConsole() {
        return audiences.console();
    }

    @Override
    @NotNull
    public Path getConfigDirectory() {
        return getDataFolder().toPath();
    }

    @NotNull
    @Override
    public BukkitHuskChat getPlugin() {
        return this;
    }

}
