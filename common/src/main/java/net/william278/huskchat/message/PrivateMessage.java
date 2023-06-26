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

package net.william278.huskchat.message;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.filter.ChatFilter;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.replacer.ReplacerFilter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * Represents a private message to be sent to a target user
 */
public class PrivateMessage {
    private final HuskChat plugin;
    private final List<String> targetUsernames;
    private final String message;
    private Player sender;

    public PrivateMessage(@NotNull Player sender, @NotNull List<String> targetUsernames, @NotNull String message,
                          @NotNull HuskChat plugin) {
        this.sender = sender;
        this.targetUsernames = targetUsernames;
        this.message = message;
        this.plugin = plugin;
    }

    /**
     * Dispatch the private message to be sent
     */
    public void dispatch() {
        // Verify that the player is not sending a message from a server where channel access is restricted
        for (String restrictedServer : plugin.getSettings().getMessageCommandRestrictedServers()) {
            if (restrictedServer.equalsIgnoreCase(sender.getServerName())) {
                plugin.getLocales().sendMessage(sender, "error_message_restricted_server");
                return;
            }
        }

        // Verify that the player is not sending a group message when they are turned off
        if (targetUsernames.size() > 1 && !plugin.getSettings().doGroupMessages()) {
            plugin.getLocales().sendMessage(sender, "error_group_messages_disabled");
            return;
        }

        // Validate message targets
        final ArrayList<Player> targetPlayers = new ArrayList<>();
        final HashSet<UUID> targetUUIDs = new HashSet<>();
        for (String targetUsername : targetUsernames) {
            Optional<Player> targetPlayer;
            if (ConsolePlayer.isConsolePlayer(targetUsername)) {
                targetPlayer = Optional.of(ConsolePlayer.adaptConsolePlayer(plugin));
            } else {
                targetPlayer = plugin.findPlayer(targetUsername);
            }

            if (targetPlayer.isPresent()) {
                // Prevent sending messages to yourself
                if (targetPlayer.get().getUuid().equals(sender.getUuid())) {
                    plugin.getLocales().sendMessage(sender, "error_cannot_message_self");
                    return;
                }

                // Remove duplicate users from array
                if (targetUUIDs.contains(targetPlayer.get().getUuid())) {
                    continue;
                }

                targetPlayers.add(targetPlayer.get());
                targetUUIDs.add(targetPlayer.get().getUuid());
            }
        }

        // Validate that there aren't too many users
        final int maxGroupMembers = plugin.getSettings().getMaxGroupMessageSize();
        if (targetPlayers.size() > maxGroupMembers) {
            plugin.getLocales().sendMessage(sender, "error_group_messages_max", Integer.toString(maxGroupMembers));
            return;
        }

        // Validate that the message has recipients
        if (targetPlayers.isEmpty()) {
            if (targetUsernames.size() > 1) {
                plugin.getLocales().sendMessage(sender, "error_players_not_found");
            } else {
                plugin.getLocales().sendMessage(sender, "error_player_not_found");
            }
            return;
        }

        AtomicReference<String> finalMessage = new AtomicReference<>(message);

        // If the message is to be filtered, then perform filter checks (unless they have the bypass permission)
        if (!sender.hasPermission("huskchat.bypass_filters")) {
            for (ChatFilter filter : plugin.getSettings().getChatFilters().get("private_messages")) {
                if (sender.hasPermission(filter.getFilterIgnorePermission())) {
                    continue;
                }
                if (!filter.isAllowed(sender, finalMessage.get())) {
                    plugin.getLocales().sendMessage(sender, filter.getFailureErrorMessageId());
                    return;
                }
                if (filter instanceof ReplacerFilter replacer) {
                    finalMessage.set(replacer.replace(finalMessage.get()));
                }
            }
        }

        plugin.getEventDispatcher().dispatchPrivateMessageEvent(sender, targetPlayers, finalMessage.get()).thenAccept(event -> {
            if (event.isCancelled()) return;

            sender = event.getSender();
            final List<Player> receivers = event.getRecipients();
            finalMessage.set(event.getMessage());

            // Show that the message has been sent
            PlayerCache.setLastMessenger(sender.getUuid(), receivers);
            plugin.getLocales().sendOutboundPrivateMessage(sender, receivers, finalMessage.get());

            // Show the received message
            for (Player target : receivers) {
                final ArrayList<Player> receivedMessageFrom = new ArrayList<>(receivers);
                receivedMessageFrom.removeIf(player -> player.getUuid().equals(target.getUuid()));
                receivedMessageFrom.add(0, sender);

                PlayerCache.setLastMessenger(target.getUuid(), receivedMessageFrom);
            }
            plugin.getLocales().sendInboundPrivateMessage(receivers, sender, finalMessage.get());

            // Show a message to social spies
            if (plugin.getSettings().doSocialSpyCommand()) {
                if (!(sender.hasPermission("huskchat.command.socialspy.bypass") || receivers.stream()
                        .findFirst().orElseThrow(() -> new IllegalStateException("No receivers available for message"))
                        .hasPermission("huskchat.command.socialspy.bypass"))) {
                    final Map<Player, PlayerCache.SpyColor> spies = plugin.getPlayerCache().getSocialSpyMessageReceivers(receivers);
                    for (Player spy : spies.keySet()) {
                        if (spy.getUuid().equals(sender.getUuid())) {
                            continue;
                        }
                        if (!spy.hasPermission("huskchat.command.socialspy")) {
                            try {
                                plugin.getPlayerCache().removeSocialSpy(spy);
                            } catch (IOException e) {
                                plugin.log(Level.SEVERE, "Failed to remove social spy after failed permission check", e);
                            }
                            continue;
                        }
                        final PlayerCache.SpyColor color = spies.get(spy);
                        plugin.getLocales().sendSocialSpy(spy, color, sender, receivers, finalMessage.get());
                    }
                }

            }

            // Log the private message to console if that is enabled
            if (plugin.getSettings().doLogPrivateMessages()) {
                // Log all recipients of the message
                final StringJoiner formattedPlayers = new StringJoiner(", ");
                for (Player player : receivers) {
                    formattedPlayers.add(player.getName());
                }

                final String logFormat = plugin.getSettings().getMessageLogFormat()
                        .replaceAll("%sender%", sender.getName())
                        .replaceAll("%receiver%", formattedPlayers.toString());
                plugin.log(Level.INFO, logFormat + finalMessage);
            }
        });
    }

}