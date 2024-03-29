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

import net.william278.huskchat.HuskChat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitUser extends OnlineUser {
    private final Player player;

    private BukkitUser(@NotNull Player player, @NotNull HuskChat plugin) {
        super(player.getName(), player.getUniqueId(), plugin);
        this.player = player;
    }

    @NotNull
    public static BukkitUser adapt(@NotNull Player player, @NotNull HuskChat plugin) {
        return new BukkitUser(player, plugin);
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @NotNull
    @Override
    public String getServerName() {
        return "server";
    }

    @Override
    public int getPlayersOnServer() {
        return player.getServer().getOnlinePlayers().size();
    }

    @Override
    public boolean hasPermission(@Nullable String node, boolean allowByDefault) {
        if (node != null && player.isPermissionSet(node)) {
            return player.hasPermission(node);
        } else {
            return allowByDefault || player.isOp();
        }
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

}
