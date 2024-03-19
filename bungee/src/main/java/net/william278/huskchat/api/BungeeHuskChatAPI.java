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

package net.william278.huskchat.api;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.william278.huskchat.BungeeHuskChat;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.user.BungeeUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BungeeHuskChatAPI extends HuskChatAPI {

    private BungeeHuskChatAPI(@NotNull HuskChat plugin) {
        super(plugin);
    }

    @NotNull
    public static BungeeHuskChatAPI getInstance() {
        return (BungeeHuskChatAPI) instance;
    }

    /**
     * @hidden
     */
    @ApiStatus.Internal
    public static void register(@NotNull BungeeHuskChat plugin) {
        HuskChatAPI.instance = new BungeeHuskChatAPI(plugin);
    }

    /**
     * Adapts a platform-specific Player object to a cross-platform Player object
     *
     * @param player Must be a platform-specific Player object, e.g. a Velocity Player
     * @return {@link BungeeUser}
     */
    @NotNull
    public BungeeUser adaptPlayer(@NotNull ProxiedPlayer player) {
        return BungeeUser.adapt(player, plugin);
    }
}
