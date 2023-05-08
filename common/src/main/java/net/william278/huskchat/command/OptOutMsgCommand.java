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
import net.william278.huskchat.player.PlayerCache;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OptOutMsgCommand extends CommandBase {
    private final static String PERMISSION = "huskchat.command.optoutmsg";

    public OptOutMsgCommand(HuskChat implementor) {
        super("optoutmsg", PERMISSION, implementor);
    }

    @Override
    public void onExecute(Player player, String[] args) {
        PlayerCache.getLastMessengers(player.getUuid()).ifPresentOrElse(lastMessengers -> {
            if (lastMessengers.size() <= 1) {
                implementor.getMessageManager().sendMessage(player, "error_last_message_not_group");
                return;
            }

            for (UUID uuid : lastMessengers) {
                PlayerCache.getLastMessengers(uuid).ifPresent(last -> {
                    last.remove(player.getUuid());
                });
            }

            String playerList = lastMessengers.stream().flatMap(u -> implementor.getPlayer(u).stream())
                    .map(Player::getName).collect(Collectors.joining(", "));
            StringBuilder builder = new StringBuilder();
            int lastComma = playerList.lastIndexOf(',');
            builder.append(playerList, 0, lastComma);
            builder.append(" ").append(implementor.getMessageManager().getRawMessage("list_conjunction"));
            builder.append(playerList.substring(lastComma + 1));

            implementor.getMessageManager().sendMessage(player, "removed_from_group_message", builder.toString());
            lastMessengers.clear();
        }, () -> {
            implementor.getMessageManager().sendMessage(player, "error_no_messages_opt_out");
        });
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return new ArrayList<>();
    }
}
