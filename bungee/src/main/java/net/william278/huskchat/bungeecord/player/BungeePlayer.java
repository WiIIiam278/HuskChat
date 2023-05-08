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

package net.william278.huskchat.bungeecord.player;

import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Bungee implementation of a cross-platform {@link Player}
 */
public class BungeePlayer implements Player {

    private BungeePlayer() {
    }

    private ProxiedPlayer player;

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUuid() {
        return player.getUniqueId();
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @Override
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
    @Override
    public Audience getAudience() {
        return HuskChatBungee.getInstance().getAudience().player(player);
    }

    /**
     * Adapts a cross-platform {@link Player} to a bungee {@link CommandSender} object
     *
     * @param player {@link Player} to adapt
     * @return The {@link ProxiedPlayer} object, {@code null} if they are offline
     */
    public static Optional<ProxiedPlayer> adaptBungee(Player player) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getUuid());
        if (proxiedPlayer != null) {
            return Optional.of(proxiedPlayer);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Adapts a bungee {@link ProxiedPlayer} to a cross-platform {@link Player} object
     *
     * @param player {@link ProxiedPlayer} to adapt
     * @return The {@link Player} object
     */
    public static BungeePlayer adaptCrossPlatform(ProxiedPlayer player) {
        BungeePlayer bungeePlayer = new BungeePlayer();
        bungeePlayer.player = player;
        return bungeePlayer;
    }
}
