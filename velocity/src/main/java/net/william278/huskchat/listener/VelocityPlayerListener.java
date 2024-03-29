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

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.user.VelocityUser;
import org.jetbrains.annotations.NotNull;

public class VelocityPlayerListener extends PlayerListener {

    public VelocityPlayerListener(@NotNull HuskChat plugin) {
        super(plugin);
    }

    @Subscribe
    public void onPlayerChangeServer(ServerConnectedEvent e) {
        if (e.getPreviousServer().isEmpty()) {
            handlePlayerJoin(VelocityUser.adapt(e.getPlayer(), plugin));
        }
        final String server = e.getServer().getServerInfo().getName();
        final VelocityUser player = VelocityUser.adapt(e.getPlayer(), plugin);
        handlePlayerSwitchServer(player, server);
    }

    @Subscribe
    public void onPlayerQuitNetwork(DisconnectEvent e) {
        if (e.getLoginStatus() == DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) {
            handlePlayerQuit(VelocityUser.adapt(e.getPlayer(), plugin));
        }
    }

}
