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
import net.william278.huskchat.message.BroadcastMessage;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

public class BroadcastCommand extends CommandBase {

    public BroadcastCommand(@NotNull HuskChat plugin) {
        super(plugin.getSettings().getBroadcastCommand().getBroadcastAliases(), "<message>", plugin);
        this.operatorOnly = true;
    }

    @Override
    public void onExecute(@NotNull OnlineUser player, @NotNull String[] args) {
        if (args.length >= 1) {
            StringJoiner message = new StringJoiner(" ");
            for (String argument : args) {
                message.add(argument);
            }
            new BroadcastMessage(player, message.toString(), plugin).dispatch();
        } else {
            plugin.getLocales().sendMessage(player, "error_invalid_syntax", getUsage());
        }
    }

}
