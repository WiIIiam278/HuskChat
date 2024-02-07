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

import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public SpamFilter(@NotNull FilterSettings settings) {
        super(settings);
        this.userMessageQueues = new HashMap<>();
    }

    @NotNull
    public static FilterSettings getDefaultSettings() {
        return new SpamFilterSettings();
    }

    @Override
    public boolean isAllowed(@NotNull OnlineUser player, @NotNull String message) {
        if (!userMessageQueues.containsKey(player.getUuid())) {
            userMessageQueues.put(player.getUuid(), new LinkedList<>());
        }
        final long currentTimestamp = Instant.now().getEpochSecond();
        if (!userMessageQueues.get(player.getUuid()).isEmpty()) {
            final SpamFilterSettings spam = (SpamFilterSettings) settings;
            if (currentTimestamp > userMessageQueues.get(player.getUuid()).getLast() + spam.getPeriodSeconds()) {
                userMessageQueues.get(player.getUuid()).removeLast();
            }
            if (userMessageQueues.get(player.getUuid()).size() > spam.getMessagesPerPeriod()) {
                return false;
            }
        }
        userMessageQueues.get(player.getUuid()).addFirst(currentTimestamp);
        return true;
    }

    @Override
    @NotNull
    public String getDisallowedLocale() {
        return "error_chat_filter_spam";
    }

    @Override
    @NotNull
    public String getIgnorePermission() {
        return "huskchat.ignore_filters.spam";
    }


    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SpamFilterSettings extends FilterSettings {
        public int periodSeconds = 4;
        public int messagesPerPeriod = 3;
    }
    
}
