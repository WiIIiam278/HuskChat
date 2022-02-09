package net.william278.huskchat.filter;

/**
 * An abstract representation of a chat filterer
 */
public abstract class ChatFilter {

    /**
     * Takes a user's message and returns true if the message passes the filter
     *
     * @param message The user's message
     * @return {@code true} if the filter allows the message to pass; {@code false} otherwise
     */
    public abstract boolean isAllowed(String message);

    /**
     * The ID of the locale to send the player if their message fails the filter
     *
     * @return the failure message ID
     */
    public abstract String getFailureErrorMessageId();

}
