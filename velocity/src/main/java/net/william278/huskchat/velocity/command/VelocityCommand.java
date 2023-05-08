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

package net.william278.huskchat.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.william278.huskchat.command.CommandBase;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.velocity.HuskChatVelocity;
import net.william278.huskchat.velocity.player.VelocityPlayer;

import java.util.Collections;
import java.util.List;

public class VelocityCommand implements SimpleCommand {

    private static final HuskChatVelocity plugin = HuskChatVelocity.getInstance();

    private final CommandBase implementer;

    public VelocityCommand(CommandBase command) {
        this.implementer = command;
        plugin.getProxyServer().getCommandManager().register(command.command, this, command.aliases);
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player player) {
            implementer.onExecute(VelocityPlayer.adaptCrossPlatform(player), invocation.arguments());
        } else {
            implementer.onExecute(ConsolePlayer.adaptConsolePlayer(plugin), invocation.arguments());
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        if (invocation.source() instanceof Player player) {
            return implementer.onTabComplete(VelocityPlayer.adaptCrossPlatform(player), invocation.arguments());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        //return invocation.source().hasPermission(implementer.permission);
        return true; // We return true here because Velocity's implementation of this is to pretend to the user the command doesn't exist, which is really dumb and not user-friendly.
    }
}