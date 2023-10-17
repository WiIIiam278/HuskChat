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

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class PlayerListener {

    protected final HuskChat plugin;

    public PlayerListener(@NotNull HuskChat plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle a player switching server
     *
     * @param player    The player changing server
     * @param newServer The name of the server they are changing to
     */
    public final void handlePlayerSwitchServer(@NotNull Player player, @NotNull String newServer) {
        final Map<String, String> defaultChannels = plugin.getSettings().getServerDefaultChannels();
        if (defaultChannels.containsKey(newServer)) {
            plugin.getPlayerCache().switchPlayerChannel(player, defaultChannels.get(newServer));
        } else {
            for (Channel channel : plugin.getSettings().getChannels().values()) {
                if (channel.getId().equalsIgnoreCase(plugin.getPlayerCache().getPlayerChannel(player.getUuid()))) {
                    for (String restrictedServer : channel.getRestrictedServers()) {
                        if (restrictedServer.equalsIgnoreCase(newServer)) {
                            plugin.getPlayerCache().switchPlayerChannel(player, plugin.getSettings().getDefaultChannel());
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public final void handlePlayerJoin(@NotNull Player player) {
        if (plugin.getSettings().getJoinQuitBroadcastScope() == Channel.BroadcastScope.PASSTHROUGH) {
            return;
        }
        if (plugin.getSettings().doJoinMessages()) {
            plugin.getLocales().sendJoinMessage(player);
        }
    }

    public final void handlePlayerQuit(@NotNull Player player) {
        if (plugin.getSettings().getJoinQuitBroadcastScope() == Channel.BroadcastScope.PASSTHROUGH) {
            return;
        }
        if (plugin.getSettings().doQuitMessages()) {
            plugin.getLocales().sendQuitMessage(player);
        }
    }

}