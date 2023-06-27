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
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.filter.ChatFilter;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.replacer.ReplacerFilter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * Represents a message to be sent in a chat channel
 */
public class ChatMessage {

    public final HuskChat plugin;
    public final String targetChannelId;
    public Player sender;

    public String message;

    public ChatMessage(String targetChannelId, Player sender, String message, HuskChat plugin) {
        this.targetChannelId = targetChannelId;
        this.sender = sender;
        this.message = message;
        this.plugin = plugin;
    }

    /**
     * Dispatch the message to be sent
     *
     * @return true if the (Player)ChatEvent should be canceled in the proxy-specific code
     */
    public boolean dispatch() {
        AtomicReference<Channel> channel = new AtomicReference<>(plugin.getSettings().getChannels().get(targetChannelId));

        if (channel.get() == null) {
            plugin.getLocales().sendMessage(sender, "error_no_channel");
            return true;
        }

        // Verify that the player has permission to send in the channel
        if (channel.get().getSendPermission() != null) {
            if (!sender.hasPermission(channel.get().getSendPermission())) {
                plugin.getLocales().sendMessage(sender, "error_no_permission_send", channel.get().getId());
                return true;
            }
        }

        // Verify that the player is not sending a message from a server where channel access is restricted
        for (String restrictedServer : channel.get().getRestrictedServers()) {
            if (restrictedServer.equalsIgnoreCase(sender.getServerName())) {
                plugin.getLocales().sendMessage(sender, "error_channel_restricted_server", channel.get().getId());
                return true;
            }
        }

        // Determine the players who will receive the message;
        Channel.BroadcastScope broadcastScope = channel.get().getBroadcastScope();

        // There's no point in allowing the console to send to local chat as it's not actually in any servers;
        // the message won't get sent to anyone
        if (sender instanceof ConsolePlayer && (broadcastScope == Channel.BroadcastScope.LOCAL ||
                                                broadcastScope == Channel.BroadcastScope.LOCAL_PASSTHROUGH)) {
            plugin.getLocales().sendMessage(sender, "error_console_local_scope");
            return true;
        }

        StringBuilder msg = new StringBuilder(message);
        if (!ChatMessage.passesFilters(plugin, sender, msg, channel.get())) {
            return true;
        }
        message = msg.toString();

        HashSet<Player> messageRecipients = new HashSet<>();
        switch (broadcastScope) {
            case GLOBAL, GLOBAL_PASSTHROUGH -> messageRecipients.addAll(plugin.getOnlinePlayers());
            case LOCAL, LOCAL_PASSTHROUGH -> messageRecipients.addAll(plugin.getOnlinePlayersOnServer(sender));
            default -> {
            } // No message recipients if the channel is exclusively passed through; let the backend handle it
        }

        // The events API has no effect on messages in passthrough channels.
        // Local/global passthrough channels will have their proxy-side message affected,
        // and non-passthrough messages will also be affected by the API.
        plugin.getEventDispatcher().dispatchChatMessageEvent(sender, message, targetChannelId).thenAccept(event -> {
            if (event.isCancelled()) return;

            sender = event.getSender();

            if (!event.getChannelId().equals(channel.get().getId())) {
                if (plugin.getSettings().getChannels().containsKey(event.getChannelId())) {
                    channel.set(plugin.getSettings().getChannels().get(event.getChannelId()));
                }
            }

            message = event.getMessage();

            // Dispatch message to all applicable users in the scope with permission who are not on a restricted server
            MESSAGE_DISPATCH:
            for (Player recipient : messageRecipients) {
                if (channel.get().getReceivePermission() != null) {
                    if (!recipient.hasPermission(channel.get().getReceivePermission()) && !(recipient.getUuid().equals(sender.getUuid()))) {
                        continue;
                    }
                }

                for (String restrictedServer : channel.get().getRestrictedServers()) {
                    if (restrictedServer.equalsIgnoreCase(recipient.getServerName())) {
                        continue MESSAGE_DISPATCH;
                    }
                }

                plugin.getLocales().sendChannelMessage(recipient, sender, channel.get(), message);
            }

            // If the message is on a local channel, dispatch local spy messages to appropriate spies.
            if (broadcastScope == Channel.BroadcastScope.LOCAL || broadcastScope == Channel.BroadcastScope.LOCAL_PASSTHROUGH) {
                if (plugin.getSettings().doLocalSpyCommand()) {
                    if (!plugin.getSettings().isLocalSpyChannelExcluded(channel.get())) {
                        final Map<Player, PlayerCache.SpyColor> spies = plugin.getPlayerCache().getLocalSpyMessageReceivers(sender.getServerName(), plugin);
                        for (Player spy : spies.keySet()) {
                            if (spy.getUuid().equals(sender.getUuid())) {
                                continue;
                            }
                            if (!spy.hasPermission("huskchat.command.localspy")) {
                                try {
                                    plugin.getPlayerCache().removeLocalSpy(spy);
                                } catch (IOException e) {
                                    plugin.log(Level.SEVERE, "Failed to remove local spy after failed permission check", e);
                                }
                                continue;
                            }
                            final PlayerCache.SpyColor color = spies.get(spy);
                            plugin.getLocales().sendLocalSpy(spy, color, sender, channel.get(), message);
                        }
                    }
                }
            }

            // Log a message to console if enabled on the channel
            if (channel.get().doLogMessages()) {
                final String logFormat = plugin.getSettings().getChannelLogFormat()
                        .replaceAll("%channel%", channel.get().getId().toUpperCase())
                        .replaceAll("%sender%", sender.getName());
                plugin.log(Level.INFO, logFormat + message);
            }

            // Dispatch message to a Discord webhook if enabled
            if (plugin.getSettings().doDiscordIntegration()) {
                plugin.getWebhook().ifPresent(dispatcher -> dispatcher.dispatchWebhook(this));
            }
        });

        // Non-passthrough messages should always be canceled in the proxy-specific code
        return !broadcastScope.isPassThrough;
    }

    // This is a static method to allow for filters to be applied to passthrough channels due to those not going through this class
    // The StringBuilder allows us to modify the message if a replacer requires it.
    // Returns true if it passes all chat filters.
    public static boolean passesFilters(@NotNull HuskChat plugin, @NotNull Player sender, @NotNull StringBuilder message, @NotNull Channel channel) {
        // If the message is to be filtered, then perform filter checks (unless they have the bypass permission)
        if (channel.isFilter() && !sender.hasPermission("huskchat.bypass_filters")) {
            for (ChatFilter filter : plugin.getSettings().getChatFilters().getOrDefault(channel.getId(), List.of())) {
                if (sender.hasPermission(filter.getFilterIgnorePermission())) {
                    continue;
                }
                if (!filter.isAllowed(sender, message.toString())) {
                    plugin.getLocales().sendMessage(sender, filter.getFailureErrorMessageId());
                    return false;
                }

                if (filter instanceof ReplacerFilter replacer && !channel.getBroadcastScope().isPassThrough) {
                    String msg = message.toString();
                    message.delete(0, message.length());
                    message.append(replacer.replace(msg));
                }
            }
        }

        return true;
    }
}