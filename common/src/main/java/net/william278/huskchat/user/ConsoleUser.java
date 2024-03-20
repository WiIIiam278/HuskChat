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
import net.william278.huskchat.HuskChat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ConsoleUser extends OnlineUser {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    private static final String CONSOLE_USERNAME = "[CONSOLE]";

    private ConsoleUser(@NotNull HuskChat plugin) {
        super(CONSOLE_USERNAME, CONSOLE_UUID, plugin);
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    @NotNull
    public String getServerName() {
        return plugin.getPlatform();
    }

    @Override
    public int getPlayersOnServer() {
        return plugin.getOnlinePlayers().size();
    }

    @Override
    public boolean hasPermission(@Nullable String node, boolean allowByDefault) {
        return true;
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return plugin.getConsole();
    }

    /**
     * Adapt the proxy console player into a cross-platform one
     *
     * @param plugin The implementing HuskChat plugin
     * @return The ConsolePlayer
     */
    @NotNull
    public static ConsoleUser wrap(@NotNull HuskChat plugin) {
        return new ConsoleUser(plugin);
    }

    /**
     * Returns true if the UUID is that of the console player
     *
     * @param uuid UUID to check
     * @return {@code true} if the UUID is the console
     */
    public static boolean isConsolePlayer(@NotNull UUID uuid) {
        return uuid.equals(CONSOLE_UUID);
    }

    /**
     * Returns true if the username is that of the console player
     *
     * @param username username to check
     * @return {@code true} if the username is the console
     */
    public static boolean isConsolePlayer(@NotNull String username) {
        return username.equalsIgnoreCase(CONSOLE_USERNAME);
    }

}
