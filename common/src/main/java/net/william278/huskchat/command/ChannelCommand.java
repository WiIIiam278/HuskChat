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
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChannelCommand extends CommandBase {

    public ChannelCommand(@NotNull HuskChat plugin) {
        super(plugin.getSettings().getChannelCommandAliases(), "<channel>", plugin);
    }

    @Override
    public void onExecute(@NotNull Player player, @NotNull String[] args) {
        if (player instanceof ConsolePlayer) {
            plugin.getLocales().sendMessage(player, "error_in_game_only");
            return;
        }
        if (player.hasPermission(getPermission())) {
            if (args.length == 1) {
                plugin.getPlayerCache().switchPlayerChannel(player, args[0]);
            } else {
                plugin.getLocales().sendMessage(player, "error_invalid_syntax", getUsage());
            }
        } else {
            plugin.getLocales().sendMessage(player, "error_no_permission");
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission(getPermission())) {
            return List.of();
        }
        if (args.length <= 1) {
            return getChannelIdsWithSendPermission(player).stream().filter(val ->
                            val.toLowerCase().startsWith((args.length >= 1) ? args[0].toLowerCase() : ""))
                    .sorted().collect(Collectors.toList());
        }
        return List.of();
    }

    @NotNull
    public Set<String> getChannelIdsWithSendPermission(Player player) {
        final Set<String> channelsWithPermission = new HashSet<>();
        plugin.getSettings().getChannels().forEach((id, channel) -> {
            if (channel.getSendPermission() == null || player.hasPermission(channel.getSendPermission())) {
                channelsWithPermission.add(channel.getId());
            }
        });
        return channelsWithPermission;
    }

}
