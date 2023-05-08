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

package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.player.Player;

import java.util.List;

/**
 * Abstract, cross-platform representation of a plugin command
 */
public abstract class CommandBase {

    /**
     * Command string
     */
    public final String command;

    /**
     * Command permission node
     */
    public final String permission;

    /**
     * Command aliases
     */
    public final String[] aliases;

    /**
     * Instance of the proxy plugin implementor
     */
    public final HuskChat implementor;


    public CommandBase(String command, String permission, HuskChat implementingPlugin, String... aliases) {
        this.command = command;
        this.permission = permission;
        this.implementor = implementingPlugin;
        this.aliases = aliases;
    }

    /**
     * Fires when the command is executed
     *
     * @param player {@link Player} executing the command
     * @param args   Command arguments
     */
    public abstract void onExecute(Player player, String[] args);

    /**
     * What should be returned when the player attempts to TAB complete the command
     *
     * @param player {@link Player} doing the TAB completion
     * @param args   Current command arguments
     * @return List of String arguments to offer TAB suggestions
     */
    public abstract List<String> onTabComplete(Player player, String[] args);

}
