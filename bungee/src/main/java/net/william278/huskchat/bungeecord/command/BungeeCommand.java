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

package net.william278.huskchat.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.bungeecord.player.BungeePlayer;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.command.CommandBase;

import java.util.Collections;

public class BungeeCommand extends Command implements TabExecutor {

    private static final HuskChatBungee plugin = HuskChatBungee.getInstance();

    private final CommandBase implementer;

    public BungeeCommand(CommandBase command) {
        super(command.command, command.permission, command.aliases);
        this.implementer = command;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            implementer.onExecute(BungeePlayer.adaptCrossPlatform(player), args);
        } else {
            implementer.onExecute(ConsolePlayer.adaptConsolePlayer(plugin), args);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            return implementer.onTabComplete(BungeePlayer.adaptCrossPlatform(player), args);
        }
        return Collections.emptyList();
    }
}
