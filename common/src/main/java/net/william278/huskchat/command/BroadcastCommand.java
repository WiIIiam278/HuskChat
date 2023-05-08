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
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.BroadcastMessage;
import net.william278.huskchat.player.Player;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class BroadcastCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.broadcast";

    public BroadcastCommand(HuskChat implementor) {
        super(Settings.broadcastCommandAliases.get(0), PERMISSION, implementor, Settings.getAliases(Settings.broadcastCommandAliases));
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (player.hasPermission(permission)) {
            if (args.length >= 1) {
                StringJoiner message = new StringJoiner(" ");
                for (String argument : args) {
                    message.add(argument);
                }
                new BroadcastMessage(player, message.toString(), implementor).dispatch();
            } else {
                implementor.getMessageManager().sendMessage(player, "error_invalid_syntax", "/broadcast <message>");
            }
        } else {
            implementor.getMessageManager().sendMessage(player, "error_no_permission");
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}
