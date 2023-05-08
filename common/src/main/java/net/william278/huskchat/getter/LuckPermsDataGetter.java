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
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.model.user.User;
import net.william278.huskchat.player.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * A Data Getter that hooks with the LuckPerms API to fetch user prefixes / suffixes
 */
public class LuckPermsDataGetter extends DataGetter {

    private final LuckPerms api;

    public LuckPermsDataGetter() {
        super();
        api = LuckPermsProvider.get();
    }

    @Override
    public String getPlayerFullName(Player player) {
        User user = getUser(player.getUuid());
        if (user == null) return player.getName();
        final CachedDataManager cachedData = user.getCachedData();

        StringBuilder fullName = new StringBuilder();

        final String prefix = cachedData.getMetaData().getPrefix();
        if (prefix != null) {
            fullName.append(prefix);
        }
        fullName.append(player.getName());
        final String suffix = cachedData.getMetaData().getSuffix();
        if (suffix != null) {
            fullName.append(suffix);
        }
        return fullName.toString();
    }

    @Override
    public String getPlayerName(Player player) {
        return player.getName();
    }

    @Override
    public Optional<String> getPlayerPrefix(Player player) {
        User user = getUser(player.getUuid());
        if (user == null) return Optional.empty();

        final String prefix = user.getCachedData().getMetaData().getPrefix();
        if (prefix != null) {
            return Optional.of(prefix);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getPlayerSuffix(Player player) {
        User user = getUser(player.getUuid());
        if (user == null) return Optional.empty();

        final String suffix = user.getCachedData().getMetaData().getSuffix();
        if (suffix != null) {
            return Optional.of(suffix);
        }
        return Optional.empty();
    }

    private User getUser(UUID uuid) {
        return api.getUserManager().getUser(uuid);
    }
}
