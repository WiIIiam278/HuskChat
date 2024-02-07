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

import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * A {@link ChatFilter} that filters against users sending too many messages into the chat
 */
public class SpamFilter extends ChatFilter {

    /**
     * Map of user {@link UUID}s to a queue recording the timestamps of when that user
     * last sent a message, used to calculate if the user is sending messages too quickly.
     */
    private final HashMap<UUID, LinkedList<Long>> userMessageQueues;

    private final int periodLength;
    private final int maxMessagesPerPeriod;

    public SpamFilter(int periodLength, int maxMessagesPerPeriod) {
        this.periodLength = periodLength;
        this.maxMessagesPerPeriod = maxMessagesPerPeriod;
        this.userMessageQueues = new HashMap<>();
    }

    @Override
    public boolean isAllowed(@NotNull OnlineUser player, @NotNull String message) {
        if (!userMessageQueues.containsKey(player.getUuid())) {
            userMessageQueues.put(player.getUuid(), new LinkedList<>());
        }
        final long currentTimestamp = Instant.now().getEpochSecond();
        if (!userMessageQueues.get(player.getUuid()).isEmpty()) {
            if (currentTimestamp > userMessageQueues.get(player.getUuid()).getLast() + periodLength) {
                userMessageQueues.get(player.getUuid()).removeLast();
            }
            if (userMessageQueues.get(player.getUuid()).size() > maxMessagesPerPeriod) {
                return false;
            }
        }
        userMessageQueues.get(player.getUuid()).addFirst(currentTimestamp);
        return true;
    }

    @Override
    @NotNull
    public String getFailureErrorMessageId() {
        return "error_chat_filter_spam";
    }

    @Override
    @NotNull
    public String getFilterIgnorePermission() {
        return "huskchat.ignore_filters.spam";
    }
}
