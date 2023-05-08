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
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

public abstract class PlayerListener {

    /**
     * Handle a player switching server
     *
     * @param player      The player changing server
     * @param newServer   The name of the server they are changing to
     * @param implementor The implementing plugin
     */
    public final void handlePlayerSwitchServer(Player player, String newServer, HuskChat implementor) {
        if (Settings.serverDefaultChannels.containsKey(newServer)) {
            PlayerCache.switchPlayerChannel(player, Settings.serverDefaultChannels.get(newServer),
                    implementor.getMessageManager());
        } else {
            for (Channel channel : Settings.channels.values()) {
                if (channel.id.equalsIgnoreCase(PlayerCache.getPlayerChannel(player.getUuid()))) {
                    for (String restrictedServer : channel.restrictedServers) {
                        if (restrictedServer.equalsIgnoreCase(newServer)) {
                            PlayerCache.switchPlayerChannel(player, Settings.defaultChannel,
                                    implementor.getMessageManager());
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

}