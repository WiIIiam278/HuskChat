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
import net.william278.huskchat.message.PrivateMessage;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ReplyCommand extends CommandBase {

    public ReplyCommand(@NotNull HuskChat plugin) {
        super(plugin.getSettings().getReplyCommandAliases(), "<message>", plugin);
    }

    @Override
    public void onExecute(@NotNull Player player, @NotNull String[] args) {
        if (player.hasPermission(getPermission())) {
            if (args.length >= 1) {
                final Optional<Set<UUID>> lastMessengers = PlayerCache.getLastMessengers(player.getUuid());
                if (lastMessengers.isEmpty()) {
                    plugin.getLocales().sendMessage(player, "error_reply_no_messages");
                    return;
                }

                final ArrayList<String> lastPlayers = new ArrayList<>();
                for (UUID lastMessenger : lastMessengers.get()) {
                    if (ConsolePlayer.isConsolePlayer(lastMessenger)) {
                        lastPlayers.add(ConsolePlayer.create(plugin).getName());
                    } else {
                        plugin.getPlayer(lastMessenger).ifPresent(onlineMessenger -> lastPlayers.add(onlineMessenger.getName()));
                    }
                }

                if (lastPlayers.isEmpty()) {
                    if (lastMessengers.get().size() > 1) {
                        plugin.getLocales().sendMessage(player, "error_reply_none_online");
                    } else {
                        plugin.getLocales().sendMessage(player, "error_reply_not_online");
                    }
                    return;
                }

                final StringJoiner message = new StringJoiner(" ");
                for (String arg : args) {
                    message.add(arg);
                }

                final String messageToSend = message.toString();
                new PrivateMessage(player, lastPlayers, messageToSend, plugin).dispatch();
            } else {
                plugin.getLocales().sendMessage(player, "error_invalid_syntax", "/r <message>");
            }
        } else {
            plugin.getLocales().sendMessage(player, "error_no_permission");
        }
    }

    @Override
    @NotNull
    public String getPermission() {
        return "huskchat.command.msg.reply";
    }

    @Override
    public List<String> onTabComplete(@NotNull Player player, @NotNull String[] args) {
        return List.of();
    }

}