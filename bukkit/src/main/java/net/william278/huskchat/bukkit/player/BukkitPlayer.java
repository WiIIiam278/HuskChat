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

package net.william278.huskchat.bukkit.player;

import net.kyori.adventure.audience.Audience;
import net.william278.huskchat.bukkit.BukkitHuskChat;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BukkitPlayer implements Player {

    private final BukkitHuskChat plugin = BukkitHuskChat.getInstance();
    private final org.bukkit.entity.Player player;

    private BukkitPlayer(@NotNull org.bukkit.entity.Player player) {
        this.player = player;
    }

    @NotNull
    public static BukkitPlayer adapt(@NotNull org.bukkit.entity.Player player) {
        return new BukkitPlayer(player);
    }

    @NotNull
    @Override
    public String getName() {
        return player.getName();
    }

    @NotNull
    @Override
    public UUID getUuid() {
        return player.getUniqueId();
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
    public boolean hasPermission(String node) {
        return player.hasPermission(node);
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return plugin.getAudience().player(player);
    }

    @NotNull
    public org.bukkit.entity.Player getBukkitPlayer() {
        return player;
    }

}
