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

import java.io.IOException;
import java.util.*;

/**
 * A cache for persisting player data
 */
@Configuration
@NoArgsConstructor
public class UserCache {

    private Map<UUID, String> playerChannels = new HashMap<>();
    private HashMap<UUID, SpyColor> localSpies = new HashMap<>();
    private HashMap<UUID, SpyColor> socialSpies = new HashMap<>();

    @NotNull
    public String getPlayerChannel(@NotNull UUID uuid) {
        return playerChannels.get(uuid);
    }

    public void setPlayerChannel(@NotNull UUID uuid, @NotNull String channelId) {
        playerChannels.put(uuid, channelId);
    }


    /**
     * Switch the {@link OnlineUser}'s channel
     *
     * @param player    {@link OnlineUser} to switch the channel of
     * @param channelId ID of the channel to switch to
     */
    public void switchPlayerChannel(@NotNull OnlineUser player, @NotNull String channelId, @NotNull HuskChat plugin) {
        final Optional<Channel> optionalChannel = plugin.getChannels().getChannel(channelId);
        if (optionalChannel.isEmpty()) {
            plugin.getLocales().sendMessage(player, "error_invalid_channel");
            return;
        }

        final Channel channel = optionalChannel.get();
        if (!channel.canUserSend(player)) {
            plugin.getLocales().sendMessage(player, "error_no_permission_send", channel.getId());
            return;
        }
        setPlayerChannel(player.getUuid(), channel.getId());
        plugin.getLocales().sendMessage(player, "channel_switched", channel.getId());
    }


    // Map of users last private message target for /reply command
    @NotNull
    private static final Map<UUID, Set<UUID>> lastMessagePlayers = new HashMap<>();

    public static Optional<Set<UUID>> getLastMessengers(@NotNull UUID uuid) {
        if (lastMessagePlayers.containsKey(uuid)) {
            return Optional.of(lastMessagePlayers.get(uuid));
        }
        return Optional.empty();
    }

    public static void setLastMessenger(@NotNull UUID playerToSet, @NotNull List<OnlineUser> lastMessengers) {
        final HashSet<UUID> uuidPlayers = new HashSet<>();
        for (OnlineUser player : lastMessengers) {
            uuidPlayers.add(player.getUuid());
        }
        lastMessagePlayers.put(playerToSet, uuidPlayers);
    }

    public boolean isSocialSpying(@NotNull OnlineUser player) {
        return socialSpies.containsKey(player.getUuid());
    }

    public void setSocialSpy(@NotNull OnlineUser player) throws IOException {
        socialSpies.put(player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public void setSocialSpy(@NotNull OnlineUser player, @NotNull SpyColor spyColor) throws IOException {
        socialSpies.put(player.getUuid(), spyColor);
    }

    public void removeSocialSpy(@NotNull OnlineUser player) throws IOException {
        socialSpies.remove(player.getUuid());
    }

    // Determines who is going to receive a spy message
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

    public boolean isLocalSpying(OnlineUser player) {
        return localSpies.containsKey(player.getUuid());
    }

    public void setLocalSpy(OnlineUser player) throws IOException {
        localSpies.put(player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public void setLocalSpy(OnlineUser player, SpyColor spyColor) throws IOException {
        localSpies.put(player.getUuid(), spyColor);
    }

    public void removeLocalSpy(OnlineUser player) throws IOException {
        localSpies.remove(player.getUuid());
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
