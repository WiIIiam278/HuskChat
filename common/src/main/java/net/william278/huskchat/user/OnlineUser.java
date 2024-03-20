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

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.william278.huskchat.HuskChat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.UUID;

/**
 * Abstract cross-platform Player object
 */
public abstract class OnlineUser extends User {

    protected HuskChat plugin;

    protected OnlineUser(@NotNull String username, @NotNull UUID uuid, @NotNull HuskChat plugin) {
        super(username, uuid);
        this.plugin = plugin;
    }

    @TestOnly
    protected OnlineUser(@NotNull String username, @NotNull UUID uuid) {
        super(username, uuid);
    }

    /**
     * Return the player's ping
     *
     * @return the player's ping
     */
    public abstract int getPing();

    /**
     * Return the name of the server the player is connected to
     *
     * @return player's server name
     */
    @NotNull
    public abstract String getServerName();

    /**
     * Return the number of people on that player's server
     *
     * @return player count on the player's server
     */
    public abstract int getPlayersOnServer();

    /**
     * Check if the player has a permission
     *
     * @param permission     the permission to check
     * @param allowByDefault whether to allow the permission by default if it is not set
     * @return whether the player has the permission
     */
    public abstract boolean hasPermission(@Nullable String permission, boolean allowByDefault);

    @NotNull
    public Audience getAudience() {
        return plugin.getAudience(getUuid());
    }

    public void sendMessage(@NotNull Component message) {
        getAudience().sendMessage(message);
    }

    public void sendMessage(@NotNull MineDown mineDown) {
        sendMessage(mineDown.toComponent());
    }
}
