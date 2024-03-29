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

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.util.TriState;
import net.william278.huskchat.HuskChat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Velocity implementation of a cross-platform {@link OnlineUser}
 */
public class VelocityUser extends OnlineUser {

    private final com.velocitypowered.api.proxy.Player player;

    private VelocityUser(@NotNull Player player, @NotNull HuskChat plugin) {
        super(player.getUsername(), player.getUniqueId(), plugin);
        this.player = player;
    }

    @NotNull
    public static VelocityUser adapt(@NotNull Player player, @NotNull HuskChat plugin) {
        return new VelocityUser(player, plugin);
    }

    @Override
    public int getPing() {
        return (int) player.getPing();
    }

    @Override
    @NotNull
    public String getServerName() {
        final Optional<ServerConnection> connection = player.getCurrentServer();
        if (connection.isPresent()) {
            return connection.get().getServerInfo().getName();
        }
        return "";
    }

    @Override
    public int getPlayersOnServer() {
        return player.getCurrentServer().map(conn -> conn.getServer().getPlayersConnected().size()).orElse(0);
    }

    @Override
    public boolean hasPermission(@Nullable String permission, boolean allowByDefault) {
        if (permission == null) {
            return allowByDefault;
        }
        final TriState state = player.getPermissionValue(permission).toAdventureTriState();
        if (state == TriState.NOT_SET) {
            return allowByDefault;
        }
        return state == TriState.TRUE;
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

}
