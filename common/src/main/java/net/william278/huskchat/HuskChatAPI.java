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

import net.william278.huskchat.message.BroadcastMessage;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.message.PrivateMessage;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class HuskChatAPI {
    protected static HuskChatAPI instance;
    protected final HuskChat plugin;

    protected HuskChatAPI(@NotNull HuskChat plugin) {
        this.plugin = plugin;
    }

    public static HuskChatAPI getInstance() {
        return instance;
    }

    /**
     * Returns the player's current channel
     */
    public String getPlayerChannel(@NotNull Player player) {
        return plugin.getPlayerCache().getPlayerChannel(player.getUuid());
    }

    /**
     * Sets the player's channel
     */
    public void setPlayerChannel(@NotNull Player player, @NotNull String channel) {
        plugin.getPlayerCache().setPlayerChannel(player.getUuid(), channel);
    }

    /**
     * Sends a chat message on behalf of a player
     */
    public void sendChatMessage(@NotNull String targetChannelId, @NotNull Player sender, @NotNull String message) {
        new ChatMessage(targetChannelId, sender, message, plugin).dispatch();
    }

    /**
     * Sends a broadcast message
     */
    public void sendBroadcastMessage(@NotNull Player sender, @NotNull String message) {
        new BroadcastMessage(sender, message, plugin).dispatch();
    }

    /**
     * Sends a private message on behalf of a player
     */
    public void sendPrivateMessage(@NotNull Player sender, @NotNull List<String> targetUsernames, @NotNull String message) {
        new PrivateMessage(sender, targetUsernames, message, plugin).dispatch();
    }
}
