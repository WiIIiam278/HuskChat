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

package net.william278.huskchat.filter;

import net.william278.huskchat.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * A {@link ChatFilter} that filters against users sending the same message too many times
 */
public class RepeatFilter extends ChatFilter {

    /**
     * Map of user {@link UUID}s to a queue recording the previous messages the user has sent
     */
    private final HashMap<UUID, LinkedList<String>> userMessageQueues;

    private final int previousMessagesToCheck;

    public RepeatFilter(int previousMessagesToCheck) {
        this.previousMessagesToCheck = previousMessagesToCheck;
        this.userMessageQueues = new HashMap<>();
    }

    @Override
    public boolean isAllowed(Player player, String message) {
        if (!userMessageQueues.containsKey(player.getUuid())) {
            userMessageQueues.put(player.getUuid(), new LinkedList<>());
        }
        if (!userMessageQueues.get(player.getUuid()).isEmpty()) {
            for (String previousMessage : userMessageQueues.get(player.getUuid())) {
                if (message.equalsIgnoreCase(previousMessage)) {
                    return false;
                }
            }
            if (userMessageQueues.get(player.getUuid()).size() + 1 > previousMessagesToCheck) {
                userMessageQueues.get(player.getUuid()).removeLast();
            }
        }
        userMessageQueues.get(player.getUuid()).addFirst(message);
        return true;
    }

    @Override
    public String getFailureErrorMessageId() {
        return "error_chat_filter_repeat";
    }

    @Override
    public String getFilterIgnorePermission() {
        return "huskchat.ignore_filters.spam";
    }
}
