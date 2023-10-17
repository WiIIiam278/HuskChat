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

package net.william278.huskchat.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.listener.PlayerListener;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.velocity.player.VelocityPlayer;
import org.jetbrains.annotations.NotNull;

public class VelocityListener extends PlayerListener {

    public VelocityListener(@NotNull HuskChat plugin) {
        super(plugin);
    }

    @Subscribe(order = PostOrder.LATE)
    public void onPlayerChat(PlayerChatEvent e) {
        if (!e.getResult().isAllowed()) {
            return;
        }

        final Player player = VelocityPlayer.adapt(e.getPlayer());
        final boolean shouldCancel = new ChatMessage(plugin.getPlayerCache().getPlayerChannel(player.getUuid()),
                player, e.getMessage(), plugin)
                .dispatch();

        if (shouldCancel) {
            e.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Subscribe
    public void onPlayerChangeServer(ServerConnectedEvent e) {
        final String server = e.getServer().getServerInfo().getName();
        final VelocityPlayer player = VelocityPlayer.adapt(e.getPlayer());
        handlePlayerSwitchServer(player, server);
    }

    @Subscribe
    public void onPlayerJoinNetwork(PostLoginEvent e) {
        handlePlayerJoin(VelocityPlayer.adapt(e.getPlayer()));
    }

    @Subscribe
    public void onPlayerQuitNetwork(DisconnectEvent e) {
        handlePlayerQuit(VelocityPlayer.adapt(e.getPlayer()));
    }

}
