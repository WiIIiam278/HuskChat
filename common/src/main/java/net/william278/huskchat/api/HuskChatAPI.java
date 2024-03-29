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

package net.william278.huskchat.api;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.message.BroadcastMessage;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.message.PrivateMessage;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * The HuskChat API
 *
 * @since 3.0
 */
@SuppressWarnings("unused")
public class HuskChatAPI {
    protected static HuskChatAPI instance;
    protected final HuskChat plugin;

    protected HuskChatAPI(@NotNull HuskChat plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public static HuskChatAPI getInstance() {
        return instance;
    }

    /**
     * Returns the player's current channel if they are in one
     *
     * @param player The player to get the channel for
     * @return The player's current channel, optionally, if they are in one
     * @since 3.0
     */
    @NotNull
    public Optional<String> getPlayerChannel(@NotNull OnlineUser player) {
        return plugin.getUserCache().getPlayerChannel(player.getUuid());
    }

    /**
     * Sets the player's channel
     *
     * @param player  The player to set the channel for
     * @param channel The channel to set
     * @since 3.0
     */
    public void setPlayerChannel(@NotNull OnlineUser player, @NotNull String channel) {
        plugin.editUserCache(c -> c.setPlayerChannel(player.getUuid(), channel));
    }

    /**
     * Sends a chat message on behalf of a player
     *
     * @param targetChannelId The ID of the channel to send the message to
     * @param sender          The player sending the message
     * @param message         The message to send
     * @throws IllegalArgumentException if the target channel does not exist
     * @since 3.0
     */
    public void sendChatMessage(@NotNull String targetChannelId, @NotNull OnlineUser sender, @NotNull String message) {
        final Channel channel = plugin.getChannels().getChannel(targetChannelId).orElseThrow(
                () -> new IllegalArgumentException("The target channel does not exist")
        );
        new ChatMessage(channel, sender, message, plugin).dispatch();
    }

    /**
     * Sends a broadcast message
     *
     * @param sender  The player sending the message
     * @param message The message to send
     * @since 3.0
     */
    public void sendBroadcastMessage(@NotNull OnlineUser sender, @NotNull String message) {
        new BroadcastMessage(sender, message, plugin).dispatch();
    }

    /**
     * Sends a private message on behalf of a player
     *
     * @param sender          The player sending the message
     * @param targetUsernames The usernames of the players to send the message to
     * @param message         The message to send
     * @since 3.0
     */
    public void sendPrivateMessage(@NotNull OnlineUser sender, @NotNull List<String> targetUsernames,
                                   @NotNull String message) {
        new PrivateMessage(sender, targetUsernames, message, plugin).dispatch();
    }
}
