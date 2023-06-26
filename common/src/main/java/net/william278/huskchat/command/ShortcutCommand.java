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
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;

public class ShortcutCommand extends CommandBase {
    private final String channelId;

    public ShortcutCommand(@NotNull String command, @NotNull String channelId, @NotNull HuskChat plugin) {
        super(List.of(command), "[message]", plugin);
        this.channelId = channelId;
    }

    @Override
    public void onExecute(@NotNull Player player, @NotNull String[] args) {
        if (player.hasPermission(getPermission())) {
            if (args.length == 0) {
                // Console can't chat in the same way as players can, it can only use commands.
                // So no need to allow it to switch channels.
                if (player instanceof ConsolePlayer) {
                    plugin.log(Level.INFO, plugin.getLocales().getRawLocale("error_console_switch_channels"));
                    return;
                }
                plugin.getPlayerCache().switchPlayerChannel(player, channelId);
            } else {
                StringJoiner message = new StringJoiner(" ");
                for (String arg : args) {
                    message.add(arg);
                }

                Channel channel = plugin.getSettings().getChannels().get(channelId);

                if (channel.getBroadcastScope().isPassThrough) {
                    plugin.getLocales().sendMessage(player, "passthrough_shortcut_command_error");
                    return;
                }

                new ChatMessage(channelId, player, message.toString(), plugin).dispatch();
            }
        } else {
            plugin.getLocales().sendMessage(player, "error_no_permission");
        }
    }

    @Override
    @NotNull
    public String getPermission() {
        return "huskchat.command.channel";
    }

    @Override
    public List<String> onTabComplete(@NotNull Player player, @NotNull String[] args) {
        return Collections.emptyList();
    }

}
