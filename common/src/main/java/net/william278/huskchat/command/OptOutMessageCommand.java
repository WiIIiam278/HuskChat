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
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OptOutMessageCommand extends CommandBase {

    public OptOutMessageCommand(@NotNull HuskChat plugin) {
        super(List.of("optoutmsg"), "", plugin);
    }

    @Override
    public void onExecute(@NotNull OnlineUser player, @NotNull String[] args) {
        plugin.getUserCache().getLastMessengers(player.getUuid()).ifPresentOrElse(lastMessengers -> {
            if (lastMessengers.size() <= 1) {
                plugin.getLocales().sendMessage(player, "error_last_message_not_group");
                return;
            }

            for (UUID uuid : lastMessengers) {
                plugin.getUserCache().getLastMessengers(uuid).ifPresent(last -> last.remove(player.getUuid()));
            }

            String playerList = lastMessengers.stream().flatMap(u -> plugin.getPlayer(u).stream())
                    .map(OnlineUser::getName).collect(Collectors.joining(", "));
            StringBuilder builder = new StringBuilder();
            int lastComma = playerList.lastIndexOf(',');
            builder.append(playerList, 0, lastComma);
            builder.append(" ").append(plugin.getLocales().getRawLocale("list_conjunction"));
            builder.append(playerList.substring(lastComma + 1));

            plugin.getLocales().sendMessage(player, "removed_from_group_message", builder.toString());
            lastMessengers.clear();
        }, () -> plugin.getLocales().sendMessage(player, "error_no_messages_opt_out"));
    }

}
