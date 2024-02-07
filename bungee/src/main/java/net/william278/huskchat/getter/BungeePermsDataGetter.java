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

package net.william278.huskchat.getter;

import net.alpenblock.bungeeperms.BungeePerms;
import net.alpenblock.bungeeperms.PermissionsManager;
import net.alpenblock.bungeeperms.User;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A Data Getter that hooks with the BungeePerms API to fetch user prefixes / suffixes
 */
public class BungeePermsDataGetter extends DataGetter {

    private final PermissionsManager permissionsManager;

    public BungeePermsDataGetter() {
        super();
        permissionsManager = BungeePerms.getInstance().getPermissionsManager();
    }

    @Override
    public String getPlayerFullName(@NotNull OnlineUser player) {
        final Optional<String> prefix = getPlayerPrefix(player);
        final Optional<String> suffix = getPlayerSuffix(player);
        return (prefix.isPresent() ? prefix : "") + player.getName()
                + (suffix.isPresent() ? suffix : "");
    }

    @Override
    public String getPlayerName(@NotNull OnlineUser player) {
        return player.getName();
    }

    @Override
    public Optional<String> getPlayerPrefix(@NotNull OnlineUser player) {
        try {
            return Optional.of(permissionsManager.getMainGroup(getUser(player)).getPrefix());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getPlayerSuffix(@NotNull OnlineUser player) {
        try {
            return Optional.of(permissionsManager.getMainGroup(getUser(player)).getSuffix());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getPlayerGroupName(@NotNull OnlineUser player) {
        try {
            return Optional.of(permissionsManager.getMainGroup(getUser(player)).getName());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getPlayerGroupDisplayName(@NotNull OnlineUser player) {
        try {
            return Optional.of(permissionsManager.getMainGroup(getUser(player)).getDisplay());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getTextFromNode(@NotNull OnlineUser player, @NotNull String nodePrefix) {
        final String prefix = nodePrefix.endsWith(".") ? nodePrefix : nodePrefix + ".";
        return getUser(player).getPerms().stream().filter(node -> node.startsWith(prefix)).findFirst()
                .map(node -> node.length() > prefix.length() ? node.substring(prefix.length()) : "");
    }

    private User getUser(OnlineUser player) {
        return permissionsManager.getUser(player.getUuid());
    }
}
