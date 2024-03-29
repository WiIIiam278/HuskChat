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
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.user.ConsoleUser;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChannelCommand extends CommandBase {

    public ChannelCommand(@NotNull HuskChat plugin) {
        super(plugin.getChannels().getChannelCommandAliases(), "<channel>", plugin);
    }

    @Override
    public void onExecute(@NotNull OnlineUser player, @NotNull String[] args) {
        if (player instanceof ConsoleUser) {
            plugin.getLocales().sendMessage(player, "error_in_game_only");
            return;
        }
        if (args.length == 1) {
            plugin.editUserCache(c -> c.switchPlayerChannel(player, args[0], plugin));
        } else {
            plugin.getLocales().sendMessage(player, "error_invalid_syntax", getUsage());
        }
    }

    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull OnlineUser player, @NotNull String[] args) {
        if (args.length <= 1) {
            return getUsableChannels(player).stream()
                    .filter(val -> val.toLowerCase().startsWith((args.length == 1) ? args[0].toLowerCase() : ""))
                    .sorted().toList();
        }
        return List.of();
    }

    @NotNull
    @Unmodifiable
    public Set<String> getUsableChannels(@NotNull OnlineUser player) {
        return plugin.getChannels().getChannels().stream()
                .filter(c -> c.canUserSend(player))
                .map(Channel::getId)
                .collect(Collectors.toSet());
    }

}
