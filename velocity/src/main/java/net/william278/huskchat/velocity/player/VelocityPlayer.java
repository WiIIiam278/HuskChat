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

package net.william278.huskchat.velocity.player;

import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.audience.Audience;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.velocity.VelocityHuskChat;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Velocity implementation of a cross-platform {@link Player}
 */
public class VelocityPlayer implements Player {

    private static final VelocityHuskChat plugin = VelocityHuskChat.getInstance();
    private final com.velocitypowered.api.proxy.Player player;

    private VelocityPlayer(com.velocitypowered.api.proxy.Player player) {
        this.player = player;
    }

    @Override
    @NotNull
    public String getName() {
        return player.getUsername();
    }

    @NotNull
    @Override
    public UUID getUuid() {
        return player.getUniqueId();
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
        final Optional<ServerConnection> connection = player.getCurrentServer();
        if (connection.isPresent()) {
            return connection.get().getServer().getPlayersConnected().size();
        }
        return 0;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return player;
    }

    /**
     * Adapts a cross-platform {@link Player} to a Velocity {@link com.velocitypowered.api.proxy.Player} object
     *
     * @param player {@link Player} to adapt
     * @return The {@link com.velocitypowered.api.proxy.Player} object, {@code null} if they are offline
     */
    public static Optional<com.velocitypowered.api.proxy.Player> toVelocity(@NotNull Player player) {
        return plugin.getProxyServer().getPlayer(player.getUuid());
    }

    /**
     * Adapts a Velocity {@link com.velocitypowered.api.proxy.Player} to a cross-platform {@link Player} object
     *
     * @param player {@link com.velocitypowered.api.proxy.Player} to adapt
     * @return The {@link Player} object
     */
    @NotNull
    public static VelocityPlayer adapt(com.velocitypowered.api.proxy.Player player) {
        return new VelocityPlayer(player);
    }
}
