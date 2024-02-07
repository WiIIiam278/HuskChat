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
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.user.BukkitUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitListener extends PlayerListener implements Listener {

    public BukkitListener(@NotNull HuskChat plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        final Player player = e.getPlayer();
        boolean shouldCancel = new ChatMessage(
                plugin.getPlayerCache().getPlayerChannel(player.getUniqueId()),
                BukkitUser.adapt(player, plugin),
                e.getMessage(),
                plugin
        ).dispatch();
        if (shouldCancel) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        final BukkitUser player = BukkitUser.adapt(e.getPlayer(), plugin);
        super.handlePlayerSwitchServer(player, player.getServerName());
        if (plugin.getSettings().doJoinMessages() || !plugin.getSettings().getJoinQuitBroadcastScope().isPassThrough) {
            e.setJoinMessage(null);
        }
        super.handlePlayerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (plugin.getSettings().doQuitMessages() || !plugin.getSettings().getJoinQuitBroadcastScope().isPassThrough) {
            e.setQuitMessage(null);
        }
        super.handlePlayerQuit(BukkitUser.adapt(e.getPlayer(), plugin));
    }

}
