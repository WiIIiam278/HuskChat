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

package net.william278.huskchat.bukkit.listener;

import net.william278.huskchat.bukkit.BukkitHuskChat;
import net.william278.huskchat.bukkit.player.BukkitPlayer;
import net.william278.huskchat.message.ChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitListener implements Listener {

    private final BukkitHuskChat plugin;

    public BukkitListener(@NotNull BukkitHuskChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        final Player player = e.getPlayer();
        boolean shouldCancel = new ChatMessage(
                plugin.getPlayerCache().getPlayerChannel(player.getUniqueId()),
                BukkitPlayer.adapt(player),
                e.getMessage(),
                plugin
        ).dispatch();
        if (shouldCancel) {
            e.setCancelled(true);
        }
    }

}
