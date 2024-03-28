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

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TestOnlineUser extends OnlineUser {

    private final static int TEST_PLAYER_PING = 5;
    private final static String TEST_PLAYER_SERVER = "test";
    private final static int TEST_PLAYER_SERVER_PLAYER_COUNT = 1;

    public TestOnlineUser() {
        super(UUID.randomUUID().toString().split("-")[0], UUID.randomUUID());
    }

    @Override
    public int getPing() {
        return TEST_PLAYER_PING;
    }

    @Override
    @NotNull
    public String getServerName() {
        return TEST_PLAYER_SERVER;
    }

    @Override
    public int getPlayersOnServer() {
        return TEST_PLAYER_SERVER_PLAYER_COUNT;
    }

    @Override
    public boolean hasPermission(@Nullable String permission, boolean allowByDefault) {
        return true;
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return Audience.empty();
    }
}
