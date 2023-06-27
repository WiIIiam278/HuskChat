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
import net.william278.huskchat.config.Webhook;
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

    @NotNull
    EventDispatcher getEventDispatcher();

    @NotNull
    PlayerCache getPlayerCache();

    @NotNull
    List<PlaceholderReplacer> getPlaceholderReplacers();

    default CompletableFuture<String> replacePlaceholders(@NotNull Player player, @NotNull String message) {
        return getPlaceholderReplacers().stream()
                .map(replacer -> replacer.formatPlaceholders(message, player))
                .reduce(
                        CompletableFuture.completedFuture(message),
                        (a, b) -> a.thenCombine(b, (x, y) -> y)
                );
    }

    @NotNull
    DataGetter getDataGetter();

    Optional<Webhook> getWebhook();

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

    void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... throwable);

}
