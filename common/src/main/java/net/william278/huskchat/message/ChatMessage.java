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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.user.ConsoleUser;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.UserCache;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * Represents a message to be sent in a chat channel
 */
@Getter
public class ChatMessage {

    @Getter(AccessLevel.PRIVATE)
    private final HuskChat plugin;

    @Setter
    private Channel channel;
    @Setter
    private OnlineUser sender;
    @Setter
    private String message;

    public ChatMessage(@NotNull Channel channel, @NotNull OnlineUser sender, @NotNull String message,
                       @NotNull HuskChat plugin) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        this.plugin = plugin;
    }

    /**
     * Dispatch the message to be sent
     *
     * @return true if the message should be canceled (thus not passed through)
     */
    public boolean dispatch() {
        final AtomicReference<Channel> channel = new AtomicReference<>(this.getChannel());
        if (!getChannel().canUserSend(getSender())) {
            getPlugin().getLocales().sendMessage(getSender(), "error_no_permission_send", channel.get().getId());
            return true;
        }

        // Verify that the player is not sending a message from a server where channel access is restricted
        for (String restrictedServer : channel.get().getRestrictedServers()) {
            if (restrictedServer.equalsIgnoreCase(getSender().getServerName())) {
                getPlugin().getLocales().sendMessage(getSender(), "error_channel_restricted_server", channel.get().getId());
                return true;
            }
        }

        // Determine the players who will receive the message;
        Channel.BroadcastScope scope = channel.get().getBroadcastScope();

        // There's no point in allowing the console to send to local chat as it's not actually in any servers;
        // the message won't get sent to anyone
        if (getSender() instanceof ConsoleUser && (scope == Channel.BroadcastScope.LOCAL ||
                scope == Channel.BroadcastScope.LOCAL_PASSTHROUGH)) {
            getPlugin().getLocales().sendMessage(getSender(), "error_console_local_scope");
            return true;
        }

        final Optional<String> formatted = getPlugin().filter(getSender(), getMessage(), getPlugin().getChannelFilters(channel.get()));
        if (formatted.isEmpty()) {
            return true;
        }
        setMessage(formatted.get());

        HashSet<OnlineUser> messageRecipients = new HashSet<>();
        switch (scope) {
            case GLOBAL, GLOBAL_PASSTHROUGH -> messageRecipients.addAll(getPlugin().getOnlinePlayers());
            case LOCAL, LOCAL_PASSTHROUGH ->
                    messageRecipients.addAll(getPlugin().getOnlinePlayersOnServer(getSender()));
            default -> {
            } // No message recipients if the channel is exclusively passed through; let the backend handle it
        }

        // The events API has no effect on messages in passthrough channels.
        // Local/global passthrough channels will have their proxy-side message affected,
        // and non-passthrough messages will also be affected by the API.
        getPlugin().fireChatMessageEvent(getSender(), getMessage(), channel.get().getId()).thenAccept(event -> {
            if (event.isCancelled()) {
                return;
            }

            // Handle event changes
            setSender(event.getSender());
            setMessage(event.getMessage());
            if (!event.getChannelId().equals(channel.get().getId())) {
                getPlugin().getChannels().getChannel(event.getChannelId()).ifPresent(channel::set);
            }

            // Dispatch message to all applicable users in the scope with permission who are not on a restricted server
            messageRecipients.forEach(recipient -> {
                boolean isSender = recipient.getUuid().equals(getSender().getUuid());
                if (!isSender && !getChannel().canUserReceive(recipient)) {
                    return;
                }

                if (channel.get().isServerRestricted(recipient.getServerName())) {
                    return;
                }
                getPlugin().getLocales().sendChannelMessage(recipient, getSender(), channel.get(), getMessage(), getPlugin());


                // If the message is on a local channel, dispatch local spy messages to appropriate spies.
                if (getPlugin().getSettings().getLocalSpy().isEnabled()
                        && !getPlugin().getSettings().getLocalSpy().getExcludedLocalChannels().contains(channel.get().getId())
                        && scope.isOneOf(Channel.BroadcastScope.LOCAL, Channel.BroadcastScope.LOCAL_PASSTHROUGH)) {
                    final Map<OnlineUser, UserCache.SpyColor> spies = getPlugin().getUserCache()
                            .getLocalSpies(getSender().getServerName(), getPlugin());
                    for (OnlineUser spy : spies.keySet()) {
                        if (spy.getUuid().equals(getSender().getUuid())) {
                            continue;
                        }
                        if (!spy.hasPermission("huskchat.command.localspy", false)) {
                            plugin.editUserCache(c -> c.removeLocalSpy(spy));
                            continue;
                        }
                        final UserCache.SpyColor color = spies.get(spy);
                        getPlugin().getLocales().sendLocalSpy(spy, color, getSender(), channel.get(), getMessage(), getPlugin());
                    }
                }
            });

            // Log a message to console if enabled on the channel
            if (channel.get().isLogToConsole()) {
                final String logFormat = getPlugin().getChannels().getChannelLogFormat()
                        .replaceAll("%channel%", channel.get().getId().toUpperCase())
                        .replaceAll("%sender%", getSender().getName());
                getPlugin().log(Level.INFO, logFormat + getMessage());
            }

            // Dispatch message to a Discord webhook if enabled
            if (getPlugin().getSettings().getDiscord().isEnabled()) {
                getPlugin().getDiscordHook().ifPresent(hook -> hook.postMessage(this));
            }
        });

        // Non-passthrough messages should always be canceled in the proxy-specific code
        return !scope.isPassThrough();
    }

}