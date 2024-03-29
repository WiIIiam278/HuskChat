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

package net.william278.huskchat.listener;

import lombok.AllArgsConstructor;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public abstract class PlayerListener {

    protected final HuskChat plugin;

    // Handle server switches
    public final void handlePlayerSwitchServer(@NotNull OnlineUser player, @NotNull String newServer) {
        // Switch to the default channel for the server if there is one
        final Map<String, String> defaultChannels = plugin.getChannels().getServerDefaultChannels();
        if (defaultChannels.containsKey(newServer)) {
            plugin.editUserCache(c -> c.switchPlayerChannel(player, defaultChannels.get(newServer), plugin));
            return;
        }

        // Switch channels to the default one if they don't have one
        final Optional<String> currentChannel = plugin.getUserCache().getPlayerChannel(player.getUuid());
        if (currentChannel.isEmpty()) {
            plugin.editUserCache(c -> c.switchPlayerChannel(player, plugin.getChannels().getDefaultChannel(), plugin));
            return;
        }

        // Switch the player's channel away if their current channel is now restricted
        plugin.getChannels().getChannels().stream()
                .filter(channel -> channel.getId().equalsIgnoreCase(currentChannel.get()))
                .findFirst().flatMap(channel -> channel.getRestrictedServers().stream()
                        .filter(restrictedServer -> restrictedServer.equalsIgnoreCase(newServer)).findFirst())
                .ifPresent(restricted -> plugin.editUserCache(c -> c
                        .switchPlayerChannel(player, plugin.getChannels().getDefaultChannel(), plugin)));
    }

    // Handle player joins
    public final void handlePlayerJoin(@NotNull OnlineUser player) {
        handlePlayerSwitchServer(player, player.getServerName());
        if (plugin.getSettings().getJoinAndQuitMessages().getBroadcastScope() == Channel.BroadcastScope.PASSTHROUGH) {
            return;
        }
        if (plugin.getSettings().getJoinAndQuitMessages().getJoin().isEnabled()) {
            plugin.getLocales().sendJoinMessage(player, plugin);
        }
    }

    // Handle player quits
    public final void handlePlayerQuit(@NotNull OnlineUser player) {
        if (plugin.getSettings().getJoinAndQuitMessages().getBroadcastScope() == Channel.BroadcastScope.PASSTHROUGH) {
            return;
        }
        if (plugin.getSettings().getJoinAndQuitMessages().getQuit().isEnabled()) {
            plugin.getLocales().sendQuitMessage(player, plugin);
        }
    }

}