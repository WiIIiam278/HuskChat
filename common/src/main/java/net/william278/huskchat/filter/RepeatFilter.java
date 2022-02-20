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
