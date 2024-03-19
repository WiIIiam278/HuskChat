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

import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The default Data Getter if LuckPerms is not installed
 */
public class DefaultDataGetter extends DataGetter {

    public DefaultDataGetter() {
        super();
    }

    @Override
    public String getPlayerFullName(@NotNull OnlineUser player) {
        return player.getName();
    }

    @Override
    public String getPlayerName(@NotNull OnlineUser player) {
        return player.getName();
    }

    @Override
    public Optional<String> getPlayerPrefix(@NotNull OnlineUser player) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getPlayerSuffix(@NotNull OnlineUser player) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getPlayerGroupName(@NotNull OnlineUser player) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getPlayerGroupDisplayName(@NotNull OnlineUser player) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getTextFromNode(@NotNull OnlineUser player, @NotNull String nodePrefix) {
        return Optional.empty();
    }

}
