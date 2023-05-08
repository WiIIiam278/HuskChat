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
import net.william278.huskchat.filter.ChatFilter;
import net.william278.huskchat.filter.replacer.ReplacerFilter;
import net.william278.huskchat.player.Player;

import java.util.logging.Level;

/**
 * Represents a broadcast message to be sent to everyone
 */
public class BroadcastMessage {
    private Player sender;
    private String message;
    private HuskChat implementor;

    public BroadcastMessage(Player sender, String message, HuskChat implementor) {
        this.sender = sender;
        this.message = message;
        this.implementor = implementor;
    }

    /**
     * Dispatch the broadcast message to be sent
     */
    public void dispatch() {
        implementor.getEventDispatcher().dispatchBroadcastMessageEvent(sender, message).thenAccept(event -> {
            if (event.isCancelled()) return;

            message = event.getMessage();

            // If the message is to be filtered, then perform filter checks (unless they have the bypass permission)
            if (!sender.hasPermission("huskchat.bypass_filters")) {
                for (ChatFilter filter : Settings.chatFilters.get("broadcast_messages")) {
                    if (sender.hasPermission(filter.getFilterIgnorePermission())) {
                        continue;
                    }
                    if (!filter.isAllowed(sender, message)) {
                        implementor.getMessageManager().sendMessage(sender, filter.getFailureErrorMessageId());
                        return;
                    }
                    if (filter instanceof ReplacerFilter replacer) {
                        message = replacer.replace(message);
                    }
                }
            }

            // Dispatch message to all players
            for (Player player : implementor.getOnlinePlayers()) {
                implementor.getMessageManager().sendFormattedBroadcastMessage(player, message);
            }

            // Log to console if enabled
            if (Settings.logBroadcasts) {
                implementor.getLoggingAdapter().log(Level.INFO, Settings.broadcastLogFormat + message);
            }
        });
    }

}