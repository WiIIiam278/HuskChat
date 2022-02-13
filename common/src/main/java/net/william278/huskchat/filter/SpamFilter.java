package net.william278.huskchat.filter;

import net.william278.huskchat.player.Player;

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
        userMessageQueues = new HashMap<>();
    }

    @Override
    public boolean isAllowed(Player player, String message) {
        if (!userMessageQueues.containsKey(player.getUuid())) {
            userMessageQueues.put(player.getUuid(), new LinkedList<>());
        }
        final long currentTimestamp = Instant.now().getEpochSecond();
        if (!userMessageQueues.get(player.getUuid()).isEmpty()) {
            if (userMessageQueues.get(player.getUuid()).getLast() > currentTimestamp + periodLength) {
                userMessageQueues.get(player.getUuid()).removeLast();
            }
            if (userMessageQueues.get(player.getUuid()).size() >= maxMessagesPerPeriod) {
                return false;
            }
        }
        userMessageQueues.get(player.getUuid()).addFirst(currentTimestamp);
        return true;
    }

    @Override
    public String getFailureErrorMessageId() {
        return "error_chat_filter_spam";
    }

    @Override
    public String getFilterIgnorePermission() {
        return "huskchat.ignore_filters.spam";
    }
}
