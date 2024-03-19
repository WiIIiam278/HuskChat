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

package net.william278.huskchat.placeholders;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class DefaultReplacer implements PlaceholderReplacer {

    private final HuskChat plugin;

    public DefaultReplacer(@NotNull HuskChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<String> formatPlaceholders(@NotNull String message, @NotNull OnlineUser player) {
        return Placeholder.replace(message, plugin, player);
    }

    /**
     * The default set of placeholders
     */
    public enum Placeholder {
        NAME(
                (plugin, player) -> plugin.getDataGetter().getPlayerName(player),
                "username"
        ),
        FULL_NAME(
                (plugin, player) -> plugin.getDataGetter().getPlayerFullName(player),
                "fullname"
        ),
        PREFIX(
                (plugin, player) -> plugin.getDataGetter().getPlayerPrefix(player).isPresent()
                ? plugin.getDataGetter().getPlayerPrefix(player).get() : "",
                "role_prefix", "roleprefix"
        ),
        SUFFIX(
                (plugin, player) -> plugin.getDataGetter().getPlayerSuffix(player).isPresent()
                ? plugin.getDataGetter().getPlayerSuffix(player).get() : "",
                "role_suffix", "rolesuffix"
        ),
        ROLE(
                (plugin, player) -> plugin.getDataGetter().getPlayerGroupName(player).isPresent()
                ? plugin.getDataGetter().getPlayerGroupName(player).get() : "",
                "role_name", "rolename"
        ),
        ROLE_DISPLAY_NAME(
                (plugin, player) -> plugin.getDataGetter().getPlayerGroupDisplayName(player).isPresent()
                ? plugin.getDataGetter().getPlayerGroupDisplayName(player).get() : "",
                "roledisplayname"
        ),
        PING(
                (plugin, player) -> Integer.toString(player.getPing())
        ),
        UUID(
                (plugin, player) -> player.getUuid().toString()
        ),
        SERVER(
                (plugin, player) -> plugin.getSettings().getServerNameReplacement()
                .getOrDefault(player.getServerName(), player.getServerName()),
                "server_name", "servername"
        ),
        LOCAL_PLAYERS_ONLINE(
                (plugin, player) -> Integer.toString(player.getPlayersOnServer()),
                "server_player_count", "serverplayercount"
        ),
        TIMESTAMP(
                (plugin, player) -> new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()),
                "timestamp"
        ),
        CURRENT_TIME(
                (plugin, player) -> new SimpleDateFormat("HH:mm:ss").format(new Date()),
                "time"
        ),
        CURRENT_TIME_SHORT(
                (plugin, player) -> new SimpleDateFormat("HH:mm").format(new Date()),
                "short_time"
        ),
        CURRENT_DATE(
                (plugin, player) -> new SimpleDateFormat("yyyy/MM/dd").format(new Date()),
                "date"
        ),
        CURRENT_DATE_UK(
                (plugin, player) -> new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                "british_date"
        ),
        CURRENT_DATE_DAY(
                (plugin, player) -> new SimpleDateFormat("dd").format(new Date()),
                "day"
        ),
        CURRENT_MONTH(
                (plugin, player) -> new SimpleDateFormat("MM").format(new Date()),
                "month"
        ),
        CURRENT_YEAR(
                (plugin, player) -> new SimpleDateFormat("yyyy").format(new Date()),
                "year"
        );

        /**
         * Function to replace placeholders with a real value
         */
        private final BiFunction<HuskChat, OnlineUser, String> replacer;
        private final Set<String> aliases = new HashSet<>();

        Placeholder(@NotNull BiFunction<HuskChat, OnlineUser, String> replacer, @NotNull String... aliases) {
            this.replacer = replacer;
            this.aliases.add(this.name().toLowerCase(Locale.ENGLISH));
            this.aliases.addAll(Set.of(aliases));
        }

        /**
         * Replace all placeholders in a string with their real values
         *
         * @param format The string to replace placeholders in
         * @param plugin The HuskChat plugin instance
         * @param player The player to replace placeholders for
         * @return The string with placeholders replaced
         */
        private static CompletableFuture<String> replace(@NotNull String format, @NotNull HuskChat plugin, @NotNull OnlineUser player) {
            for (Placeholder placeholder : values()) {
                for (String alias : placeholder.aliases) {
                    format = format.replace("%" + alias + "%", placeholder.replacer.apply(plugin, player));
                }
            }
            return CompletableFuture.completedFuture(escape(format));
        }

        // Just escaping __ should suffice as the only special character allowed in Minecraft usernames is the underscore.
        // By placing the escape character in the middle, the MineDown parser no longer sees this as a formatting code.
        @NotNull
        private static String escape(@NotNull String string) {
            return string.replace("__", "_\\_");
        }

    }

}
