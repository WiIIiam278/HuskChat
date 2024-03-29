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
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MessageCommand extends CommandBase {

    public MessageCommand(@NotNull HuskChat plugin) {
        super(
                plugin.getSettings().getMessageCommand().getMsgAliases(),
                plugin.getSettings().getMessageCommand().getGroupMessages().isEnabled()
                        ? "<player(s)> <message>" : "<player> <message>",
                plugin
        );
    }

    @Override
    public void onExecute(@NotNull OnlineUser player, @NotNull String[] args) {
        if (args.length >= 2) {
            StringJoiner message = new StringJoiner(" ");
            int messageWordCount = 0;
            for (String arg : args) {
                if (messageWordCount >= 1) {
                    message.add(arg);
                }
                messageWordCount++;
            }
            final List<String> targetPlayers = getTargetPlayers(args[0]);
            final String messageToSend = message.toString();
            new PrivateMessage(player, targetPlayers, messageToSend, plugin).dispatch();
        } else {
            plugin.getLocales().sendMessage(player, "error_invalid_syntax", getUsage());
        }
    }

    // Parses a string-separated list of target players
    private List<String> getTargetPlayers(String playerList) {
        if (!playerList.contains(",")) {
            return Collections.singletonList(playerList);
        }
        return List.of(playerList.split(","));
    }

    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull OnlineUser player, @NotNull String[] args) {
        if (args.length <= 1) {
            final ArrayList<String> userNames = new ArrayList<>();
            for (OnlineUser connectedPlayer : plugin.getOnlinePlayers()) {
                if (!player.getUuid().equals(connectedPlayer.getUuid())) {
                    userNames.add(connectedPlayer.getName());
                }
            }
            String currentText = (args.length == 1) ? args[0] : "";
            String precursoryText = "";
            String[] names = new String[0];
            if (currentText.contains(",")) {
                names = currentText.split(",");
                currentText = names[names.length - 1];

                // Names array without the last name
                String[] previousNames = Arrays.copyOf(names, names.length - 1);
                precursoryText = String.join(",", previousNames) + (previousNames.length != 0 ? "," : "");
            }

            final String completionFilter = currentText;
            final ArrayList<String> prependedUsernames = new ArrayList<>();
            for (String username : userNames.stream().filter(val -> val.toLowerCase().startsWith(completionFilter.toLowerCase())).sorted().toList()) {
                // If the name was added already, don't suggest it again
                if (names.length != 0 && Arrays.stream(names).anyMatch(name -> name.equalsIgnoreCase(username))) {
                    continue;
                }
                prependedUsernames.add(precursoryText + username);
            }
            return prependedUsernames;
        }
        return List.of();
    }

}
