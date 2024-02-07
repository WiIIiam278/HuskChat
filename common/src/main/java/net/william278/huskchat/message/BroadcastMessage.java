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
import net.william278.huskchat.replacer.ReplacerFilter;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Represents a broadcast message to be sent to everyone
 */
public class BroadcastMessage {
    private final OnlineUser sender;
    private String message;
    private final HuskChat plugin;

    public BroadcastMessage(@NotNull OnlineUser sender, @NotNull String message, @NotNull HuskChat plugin) {
        this.sender = sender;
        this.message = message;
        this.plugin = plugin;
    }

    /**
     * Dispatch the broadcast message to be sent
     */
    public void dispatch() {
        plugin.getEventDispatcher().fireBroadcastMessageEvent(sender, message).thenAccept(event -> {
            if (event.isCancelled()) return;

            message = event.getMessage();

            // If the message is to be filtered, then perform filter checks (unless they have the bypass permission)
            if (!sender.hasPermission("huskchat.bypass_filters")) {
                for (ChatFilter filter : plugin.getSettings().getChatFilters().get("broadcast_messages")) {
                    if (sender.hasPermission(filter.getFilterIgnorePermission())) {
                        continue;
                    }
                    if (!filter.isAllowed(sender, message)) {
                        plugin.getLocales().sendMessage(sender, filter.getFailureErrorMessageId());
                        return;
                    }
                    if (filter instanceof ReplacerFilter replacer) {
                        message = replacer.replace(message);
                    }
                }
            }

            // Dispatch the broadcast to all players
            for (OnlineUser player : plugin.getOnlinePlayers()) {
                plugin.getLocales().sendFormattedBroadcastMessage(player, message);
            }

            // Log to console if enabled
            if (plugin.getSettings().doLogBroadcasts()) {
                plugin.log(Level.INFO, plugin.getSettings().getBroadcastLogFormat() + message);
            }
        });
    }

}