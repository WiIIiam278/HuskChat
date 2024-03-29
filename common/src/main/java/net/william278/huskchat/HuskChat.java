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

import net.william278.desertwell.util.UpdateChecker;
import net.william278.desertwell.util.Version;
import net.william278.huskchat.config.ConfigProvider;
import net.william278.huskchat.discord.DiscordHook;
import net.william278.huskchat.discord.SpicordHook;
import net.william278.huskchat.discord.WebHook;
import net.william278.huskchat.event.EventProvider;
import net.william278.huskchat.filter.FilterProvider;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.placeholders.PlaceholderReplacer;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.util.AudiencesProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public interface HuskChat extends AudiencesProvider, ConfigProvider, FilterProvider, EventProvider {

    int SPIGOT_RESOURCE_ID = 94496;

    // Initialize webhook dispatcher
    default void loadDiscordHook() {
        if (getSettings().getDiscord().isEnabled()) {
            setDiscordHook(getSettings().getDiscord().getSpicord().isEnabled() && isPluginPresent("Spicord")
                    ? new SpicordHook(this) : new WebHook(this));
        }
    }

    @NotNull
    List<PlaceholderReplacer> getPlaceholderReplacers();

    default CompletableFuture<String> replacePlaceholders(@NotNull OnlineUser player, @NotNull String message) {
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

    Optional<OnlineUser> getPlayer(@NotNull UUID uuid);

    Optional<OnlineUser> findPlayer(@NotNull String username);

    @NotNull
    Collection<OnlineUser> getOnlinePlayers();

    @NotNull
    Collection<OnlineUser> getOnlinePlayersOnServer(@NotNull OnlineUser player);

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
        if (getSettings().isCheckForUpdates()) {
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
