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

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.william278.huskchat.HuskChat;
import org.jetbrains.annotations.NotNull;

/**
 * Bungee implementation of a cross-platform {@link OnlineUser}
 */
public class BungeeUser extends OnlineUser {
    private final ProxiedPlayer player;

    private BungeeUser(@NotNull ProxiedPlayer player, @NotNull HuskChat plugin) {
        super(player.getName(), player.getUniqueId(), plugin);
        this.player = player;
    }

    /**
     * Adapts a bungee {@link ProxiedPlayer} to a cross-platform {@link OnlineUser} object
     *
     * @param player {@link ProxiedPlayer} to adapt
     * @param plugin the plugin instance
     * @return The {@link OnlineUser} object
     */
    @NotNull
    public static BungeeUser adapt(@NotNull ProxiedPlayer player, @NotNull HuskChat plugin) {
        return new BungeeUser(player, plugin);
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @Override
    @NotNull
    public String getServerName() {
        return player.getServer().getInfo().getName();
    }

    @Override
    public int getPlayersOnServer() {
        return player.getServer().getInfo().getPlayers().size();
    }

    @Override
    public boolean hasPermission(String s) {
        return player.hasPermission(s);
    }

    @NotNull
    public ProxiedPlayer getPlayer() {
        return player;
    }

}
