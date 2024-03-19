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

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * A Data Getter that hooks with the LuckPerms API to fetch user prefixes / suffixes
 */
public class LuckPermsDataGetter extends DataGetter {

    private final LuckPerms api;

    public LuckPermsDataGetter() {
        super();
        this.api = LuckPermsProvider.get();
    }

    @Override
    public String getPlayerFullName(@NotNull OnlineUser player) {
        return getUser(player.getUuid())
                .map(User::getCachedData).map(data -> {
                    final StringBuilder fullName = new StringBuilder();

                    final String prefix = data.getMetaData().getPrefix();
                    if (prefix != null) {
                        fullName.append(prefix);
                    }

                    fullName.append(player.getName());

                    final String suffix = data.getMetaData().getSuffix();
                    if (suffix != null) {
                        fullName.append(suffix);
                    }

                    return fullName.toString();
                })
                .orElse(player.getName());
    }

    @Override
    public String getPlayerName(@NotNull OnlineUser player) {
        return player.getName();
    }

    @Override
    public Optional<String> getPlayerPrefix(@NotNull OnlineUser player) {
        return getUser(player.getUuid()).flatMap(user -> Optional.ofNullable(
                user.getCachedData().getMetaData().getPrefix()
        ));
    }

    @Override
    public Optional<String> getPlayerSuffix(@NotNull OnlineUser player) {
        return getUser(player.getUuid()).flatMap(user -> Optional.ofNullable(
                user.getCachedData().getMetaData().getSuffix()
        ));
    }

    @Override
    public Optional<String> getPlayerGroupName(@NotNull OnlineUser player) {
        return getUser(player.getUuid()).flatMap(user -> {
            final Group group = api.getGroupManager().getGroup(user.getPrimaryGroup());
            if (group == null) {
                return Optional.empty();
            }
            return Optional.of(group.getName());
        });
    }

    @Override
    public Optional<String> getPlayerGroupDisplayName(@NotNull OnlineUser player) {
        return getUser(player.getUuid()).flatMap(user -> {
            final Group group = api.getGroupManager().getGroup(user.getPrimaryGroup());
            if (group == null) {
                return Optional.empty();
            }
            if (group.getDisplayName() == null) {
                return Optional.of(group.getName());
            }
            return Optional.of(group.getDisplayName());
        });
    }

    @Override
    public Optional<String> getTextFromNode(@NotNull OnlineUser player, @NotNull String nodePrefix) {
        return getUser(player.getUuid()).flatMap(user -> Optional.ofNullable(
                user.getCachedData().getMetaData().getMetaValue(nodePrefix)
        ));
    }

    private Optional<User> getUser(@NotNull UUID uuid) {
        return Optional.ofNullable(api.getUserManager().getUser(uuid));
    }
}
