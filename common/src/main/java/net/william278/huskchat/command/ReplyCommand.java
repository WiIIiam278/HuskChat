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
import net.william278.huskchat.message.PrivateMessage;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.*;
import java.util.logging.Level;

public class ReplyCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.msg.reply";

    public ReplyCommand(HuskChat implementor) {
        super(Settings.replyCommandAliases.get(0), PERMISSION, implementor, Settings.getAliases(Settings.replyCommandAliases));
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (player.hasPermission(permission)) {
            if (args.length >= 1) {
                final Optional<HashSet<UUID>> lastMessengers = PlayerCache.getLastMessengers(player.getUuid());
                if (lastMessengers.isEmpty()) {
                    implementor.getMessageManager().sendMessage(player, "error_reply_no_messages");
                    return;
                }

                final ArrayList<String> lastPlayers = new ArrayList<>();
                for (UUID lastMessenger : lastMessengers.get()) {
                    if (ConsolePlayer.isConsolePlayer(lastMessenger)) {
                        lastPlayers.add(ConsolePlayer.adaptConsolePlayer(implementor).getName());
                    } else {
                        implementor.getPlayer(lastMessenger).ifPresent(onlineMessenger -> lastPlayers.add(onlineMessenger.getName()));
                    }
                }

                if (lastPlayers.isEmpty()) {
                    if (lastMessengers.get().size() > 1) {
                        implementor.getMessageManager().sendMessage(player, "error_reply_none_online");
                    } else {
                        implementor.getMessageManager().sendMessage(player, "error_reply_not_online");
                    }
                    return;
                }

                StringJoiner message = new StringJoiner(" ");
                for (String arg : args) {
                    message.add(arg);
                }

                final String messageToSend = message.toString();
                new PrivateMessage(player, lastPlayers, messageToSend, implementor).dispatch();
            } else {
                implementor.getMessageManager().sendMessage(player, "error_invalid_syntax", "/r <message>");
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