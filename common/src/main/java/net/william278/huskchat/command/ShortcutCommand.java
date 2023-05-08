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
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;

public class ShortcutCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.channel";

    private final String channelId;

    public ShortcutCommand(String command, String channelId, HuskChat implementor) {
        super(command, PERMISSION, implementor);
        this.channelId = channelId;
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (player.hasPermission(permission)) {
            if (args.length == 0) {
                // Console can't chat in the same way as players can, it can only use commands.
                // So no need to allow it to switch channels.
                if (player instanceof ConsolePlayer) {
                    implementor.getLoggingAdapter().log(Level.INFO, implementor.getMessageManager().getRawMessage("error_console_switch_channels"));
                    return;
                }
                PlayerCache.switchPlayerChannel(player, channelId, implementor.getMessageManager());
            } else {
                StringJoiner message = new StringJoiner(" ");
                for (String arg : args) {
                    message.add(arg);
                }

                Channel channel = Settings.channels.get(channelId);

                if (channel.broadcastScope.isPassThrough) {
                    implementor.getMessageManager().sendMessage(player, "passthrough_shortcut_command_error");
                    return;
                }

                new ChatMessage(channelId, player, message.toString(), implementor).dispatch();
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
