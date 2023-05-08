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

package net.william278.huskchat.bungeecord.util;

import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.util.Logger;

import java.util.logging.Level;

public class BungeeLogger implements Logger {

    private static final HuskChatBungee plugin = HuskChatBungee.getInstance();
    private static BungeeLogger instance;

    public static BungeeLogger get() {
        if (instance == null) {
            instance = new BungeeLogger();
        }
        return instance;
    }

    private BungeeLogger() {
    }

    @Override
    public void log(Level level, String s, Exception e) {
        plugin.getLogger().log(level, s, e);
    }

    @Override
    public void log(Level level, String s) {
        plugin.getLogger().log(level, s);
    }

    @Override
    public void info(String s) {
        plugin.getLogger().info(s);
    }

    @Override
    public void severe(String s) {
        plugin.getLogger().severe(s);
    }

    @Override
    public void config(String s) {
        plugin.getLogger().config(s);
    }
}
