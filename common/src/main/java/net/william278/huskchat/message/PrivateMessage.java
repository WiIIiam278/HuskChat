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
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.user.ConsoleUser;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.UserCache;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * Represents a private message to be sent to a target user
 */
public class PrivateMessage {
    private final HuskChat plugin;
    private final Settings.MessageSettings settings;
    private final List<String> targetUsernames;
    private final String message;
    private OnlineUser sender;

    public PrivateMessage(@NotNull OnlineUser sender, @NotNull List<String> targetUsernames, @NotNull String message,
                          @NotNull HuskChat plugin) {
        this.sender = sender;
        this.targetUsernames = targetUsernames;
        this.message = message;
        this.plugin = plugin;
        this.settings = plugin.getSettings().getMessageCommand();
    }

    /**
     * Dispatch the private message to be sent
     */
    public void dispatch() {
        // Verify that the player is not sending a message from a server where channel access is restricted
        for (String restrictedServer : settings.getRestrictedServers()) {
            if (restrictedServer.equalsIgnoreCase(sender.getServerName())) {
                plugin.getLocales().sendMessage(sender, "error_message_restricted_server");
                return;
            }
        }

        // Verify that the player is not sending a group message when they are turned off
        if (targetUsernames.size() > 1 && !settings.getGroupMessages().isEnabled()) {
            plugin.getLocales().sendMessage(sender, "error_group_messages_disabled");
            return;
        }

        // Validate message targets
        final ArrayList<OnlineUser> targetPlayers = new ArrayList<>();
        final HashSet<UUID> targetUUIDs = new HashSet<>();
        for (String targetUsername : targetUsernames) {
            Optional<OnlineUser> targetPlayer;
            if (ConsoleUser.isConsolePlayer(targetUsername)) {
                targetPlayer = Optional.of(ConsoleUser.wrap(plugin));
            } else {
                targetPlayer = plugin.findPlayer(targetUsername);
            }

            // Remove duplicate users from the array
            if (targetPlayer.isPresent()) {
                if (targetUUIDs.contains(targetPlayer.get().getUuid())) {
                    continue;
                }

                targetPlayers.add(targetPlayer.get());
                targetUUIDs.add(targetPlayer.get().getUuid());
            }
        }

        // Ensure no self-messages
        targetUUIDs.remove(sender.getUuid());
        if (targetUUIDs.isEmpty()) {
            plugin.getLocales().sendMessage(sender, "error_cannot_message_self");
            return;
        }

        // Validate that there aren't too many users
        final int maxGroupMembers = settings.getGroupMessages().getMaxSize();
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

        // If the message is to be filtered, then perform filter checks (unless they have the bypass permission)
        final Optional<String> filtered = plugin.filter(sender, message, plugin.getMessageFilters());
        if (filtered.isEmpty()) {
            return;
        }
        final AtomicReference<String> finalMessage = new AtomicReference<>(filtered.get());

        plugin.firePrivateMessageEvent(sender, targetPlayers, finalMessage.get()).thenAccept(event -> {
            if (event.isCancelled()) return;

            sender = event.getSender();
            final List<OnlineUser> receivers = event.getRecipients();
            finalMessage.set(event.getMessage());

            // Show that the message has been sent
            plugin.editUserCache(c -> c.setLastMessenger(sender.getUuid(), receivers));
            plugin.getLocales().sendOutboundPrivateMessage(sender, receivers, finalMessage.get(), plugin);

            // Show the received message
            plugin.editUserCache(c -> receivers.forEach(target -> {
                final ArrayList<OnlineUser> receivedMessageFrom = new ArrayList<>(receivers);
                receivedMessageFrom.removeIf(player -> player.getUuid().equals(target.getUuid()));
                receivedMessageFrom.add(0, sender);
                c.setLastMessenger(target.getUuid(), receivedMessageFrom);
            }));
            plugin.getLocales().sendInboundPrivateMessage(receivers, sender, finalMessage.get(), plugin);

            // Show a message to social spies
            if (plugin.getSettings().getSocialSpy().isEnabled()) {
                if (!(sender.hasPermission("huskchat.command.socialspy.bypass", false) || receivers.stream()
                        .findFirst().orElseThrow(() -> new IllegalStateException("No receivers available for message"))
                        .hasPermission("huskchat.command.socialspy.bypass", false))) {
                    final Map<OnlineUser, UserCache.SpyColor> spies = plugin.getUserCache().getSocialSpies(receivers, plugin);
                    for (OnlineUser spy : spies.keySet()) {
                        if (spy.getUuid().equals(sender.getUuid())) {
                            continue;
                        }
                        if (!spy.hasPermission("huskchat.command.socialspy", false)) {
                            plugin.editUserCache(c -> c.removeSocialSpy(spy));
                            continue;
                        }
                        final UserCache.SpyColor color = spies.get(spy);
                        plugin.getLocales().sendSocialSpy(spy, color, sender, receivers, finalMessage.get(), plugin);
                    }
                }

            }

            // Log the private message to console if that is enabled
            if (settings.isLogToConsole()) {
                // Log all recipients of the message
                final StringJoiner formattedPlayers = new StringJoiner(", ");
                for (OnlineUser player : receivers) {
                    formattedPlayers.add(player.getName());
                }

                final String logFormat = settings.getLogFormat()
                        .replaceAll("%sender%", sender.getName())
                        .replaceAll("%receiver%", formattedPlayers.toString());
                plugin.log(Level.INFO, logFormat + finalMessage);
            }
        });
    }

}