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

package net.william278.huskchat.user;

import de.exlll.configlib.Configuration;
import lombok.NoArgsConstructor;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A cache for persisting player data
 */
@Configuration
@NoArgsConstructor
public class UserCache {

    // Non-persisted data
    protected transient final Map<UUID, Set<UUID>> lastMessagePlayers = new HashMap<>();

    // Persisted data
    protected LinkedHashMap<UUID, String> playerChannels = new LinkedHashMap<>();
    protected LinkedHashMap<UUID, SpyColor> localSpies = new LinkedHashMap<>();
    protected LinkedHashMap<UUID, SpyColor> socialSpies = new LinkedHashMap<>();

    @NotNull
    public Optional<String> getPlayerChannel(@NotNull UUID uuid) {
        return Optional.ofNullable(playerChannels.get(uuid));
    }

    public Optional<Set<UUID>> getLastMessengers(@NotNull UUID uuid) {
        if (lastMessagePlayers.containsKey(uuid)) {
            return Optional.of(lastMessagePlayers.get(uuid));
        }
        return Optional.empty();
    }

    @NotNull
    public Map<OnlineUser, SpyColor> getSocialSpies(@NotNull List<OnlineUser> recipients, @NotNull HuskChat plugin) {
        final Map<OnlineUser, SpyColor> receivers = new LinkedHashMap<>();

        calculateSpies:
        for (UUID player : socialSpies.keySet()) {
            final SpyColor color = socialSpies.get(player);
            final Optional<OnlineUser> spy = plugin.getPlayer(player);
            if (spy.isEmpty()) {
                continue;
            }
            for (OnlineUser messageRecipient : recipients) {
                if (player.equals(messageRecipient.getUuid())) {
                    continue calculateSpies;
                }
            }
            receivers.put(spy.get(), color);
        }
        return receivers;
    }

    public boolean isSocialSpying(@NotNull OnlineUser player) {
        return socialSpies.containsKey(player.getUuid());
    }

    @NotNull
    public Map<OnlineUser, SpyColor> getLocalSpies(@NotNull String server, @NotNull HuskChat plugin) {
        final Map<OnlineUser, SpyColor> receivers = new LinkedHashMap<>();
        for (UUID player : localSpies.keySet()) {
            final SpyColor color = localSpies.get(player);
            final Optional<OnlineUser> spy = plugin.getPlayer(player);
            if (spy.isEmpty()) {
                continue;
            }
            if (spy.get().getServerName().equals(server)) {
                continue;
            }
            receivers.put(spy.get(), color);
        }
        return receivers;
    }

    public boolean isLocalSpying(OnlineUser player) {
        return localSpies.containsKey(player.getUuid());
    }

    /**
     * Editor wrapper for the {@link UserCache}
     */
    public static class Editor extends UserCache {

        public void setLastMessenger(@NotNull UUID playerToSet, @NotNull List<OnlineUser> lastMessengers) {
            final HashSet<UUID> uuidPlayers = new HashSet<>();
            for (OnlineUser player : lastMessengers) {
                uuidPlayers.add(player.getUuid());
            }
            lastMessagePlayers.put(playerToSet, uuidPlayers);
        }

        public void setPlayerChannel(@NotNull UUID uuid, @NotNull String channelId) {
            playerChannels.put(uuid, channelId);
        }

        /**
         * Switch the {@link OnlineUser}'s channel
         *
         * @param user    {@link OnlineUser} to switch the channel of
         * @param channelId ID of the channel to switch to
         */
        public void switchPlayerChannel(@NotNull OnlineUser user, @NotNull String channelId, @NotNull HuskChat plugin) {
            final Optional<Channel> optionalChannel = plugin.getChannels().getChannel(channelId);
            if (optionalChannel.isEmpty()) {
                plugin.getLocales().sendMessage(user, "error_invalid_channel");
                return;
            }

            final Channel channel = optionalChannel.get();
            if (!channel.canUserSend(user)) {
                plugin.getLocales().sendMessage(user, "error_no_permission_send", channel.getId());
                return;
            }
            setPlayerChannel(user.getUuid(), channel.getId());
            plugin.getLocales().sendMessage(user, "channel_switched", channel.getId());
        }

        public void setSocialSpy(@NotNull User user) {
            socialSpies.put(user.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
        }

        public void setSocialSpy(@NotNull User user, @NotNull SpyColor spyColor) {
            socialSpies.put(user.getUuid(), spyColor);
        }

        public void removeSocialSpy(@NotNull User user) {
            socialSpies.remove(user.getUuid());
        }

        public void setLocalSpy(@NotNull User user) {
            localSpies.put(user.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
        }

        public void setLocalSpy(@NotNull User user, @NotNull SpyColor spyColor) {
            localSpies.put(user.getUuid(), spyColor);
        }

        public void removeLocalSpy(@NotNull User user) {
            localSpies.remove(user.getUuid());
        }
    }

    /**
     * Color used for displaying chat
     */
    public enum SpyColor {
        DARK_RED("&4"),
        RED("&c"),
        GOLD("&6"),
        YELLOW("&e"),
        DARK_GREEN("&2"),
        GREEN("&a"),
        AQUA("&b"),
        DARK_AQUA("&3"),
        DARK_BLUE("&1"),
        BLUE("&9"),
        LIGHT_PURPLE("&d"),
        DARK_PURPLE("&5"),
        WHITE("&f"),
        GRAY("&7"),
        DARK_GRAY("&8"),
        BLACK("&9");

        public static final SpyColor DEFAULT_SPY_COLOR = DARK_GRAY;
        public final String colorCode;

        SpyColor(@NotNull String colorCode) {
            this.colorCode = colorCode;
        }

        @NotNull
        public static List<String> getColorStrings() {
            List<String> colors = new ArrayList<>();
            for (SpyColor color : SpyColor.values()) {
                colors.add(color.name().toLowerCase());
            }
            return colors;
        }

        public static Optional<SpyColor> getColor(@NotNull String colorInput) {
            for (SpyColor color : SpyColor.values()) {
                if (color.colorCode.replace("&", "").equals(colorInput.replace("&", ""))
                        || color.name().equalsIgnoreCase(colorInput.toUpperCase())) {
                    return Optional.of(color);
                }
            }
            return Optional.empty();
        }
    }

}
