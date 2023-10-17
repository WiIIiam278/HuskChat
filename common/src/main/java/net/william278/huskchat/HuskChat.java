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

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.kyori.adventure.audience.Audience;
import net.william278.desertwell.util.UpdateChecker;
import net.william278.desertwell.util.Version;
import net.william278.huskchat.config.Locales;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.discord.DiscordHook;
import net.william278.huskchat.discord.SpicordHook;
import net.william278.huskchat.discord.WebHook;
import net.william278.huskchat.event.EventDispatcher;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.placeholders.PlaceholderReplacer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public interface HuskChat {

    int SPIGOT_RESOURCE_ID = 94496;

    @NotNull
    Settings getSettings();

    void setSettings(@NotNull Settings settings);

    @NotNull
    Locales getLocales();

    void setLocales(@NotNull Locales locales);

    default void loadConfig() {
        try {
            // Set settings
            this.setSettings(new Settings(YamlDocument.create(
                    new File(getDataFolder(), "config.yml"),
                    Objects.requireNonNull(getResource("config.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.builder().setEncoding(DumperSettings.Encoding.UNICODE).build(),
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build()
            )));
            this.setLocales(new Locales(this));
        } catch (Throwable e) {
            log(Level.SEVERE, "Failed to load plugin config/locale files", e);
        }
    }

    // Initialize webhook dispatcher
    default void loadDiscordHook() {
        if (getSettings().doDiscordIntegration()) {
            setDiscordHook(getSettings().useSpicord() && isPluginPresent("Spicord")
                    ? new SpicordHook(this) : new WebHook(this));
        }
    }

    @NotNull
    EventDispatcher getEventDispatcher();

    @NotNull
    PlayerCache getPlayerCache();

    @NotNull
    List<PlaceholderReplacer> getPlaceholderReplacers();

    default CompletableFuture<String> replacePlaceholders(@NotNull Player player, @NotNull String message) {
        CompletableFuture<String> future = CompletableFuture.completedFuture(message);
        for (PlaceholderReplacer replacer : getPlaceholderReplacers()) {
            future = future.thenComposeAsync(toFormat -> replacer.formatPlaceholders(toFormat, player));
        }
        return future;
    }

    @NotNull
    DataGetter getDataGetter();

    Optional<DiscordHook> getDiscordHook();

    void setDiscordHook(@NotNull DiscordHook discordHook);

    @NotNull
    Version getVersion();

    @NotNull
    String getPluginDescription();

    @NotNull
    String getPlatform();

    Optional<Player> getPlayer(@NotNull UUID uuid);

    Optional<Player> findPlayer(@NotNull String username);

    Collection<Player> getOnlinePlayers();

    Collection<Player> getOnlinePlayersOnServer(@NotNull Player player);

    @NotNull
    Audience getConsole();

    File getDataFolder();

    InputStream getResource(@NotNull String path);

    boolean isPluginPresent(@NotNull String dependency);

    @NotNull
    default UpdateChecker getUpdateChecker() {
        return UpdateChecker.builder()
                .currentVersion(getVersion())
                .endpoint(UpdateChecker.Endpoint.SPIGOT)
                .resource(Integer.toString(SPIGOT_RESOURCE_ID))
                .build();
    }

    default void checkForUpdates() {
        if (getSettings().doCheckForUpdates()) {
            getUpdateChecker().check().thenAccept(checked -> {
                if (!checked.isUpToDate()) {
                    log(Level.WARNING, "A new version of HuskChat is available: v"
                            + checked.getLatestVersion() + " (running v" + getVersion() + ")");
                }
            });
        }
    }

    void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... throwable);

}
