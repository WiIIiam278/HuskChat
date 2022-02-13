package net.william278.huskchat.filter;

import net.william278.huskchat.player.Player;

import java.util.regex.Pattern;

/**
 * A {@link ChatFilter} that filters against unicode (non-ASCII) characters
 */
public class AsciiFilter extends ChatFilter {

    /**
     * Regex pattern matching only ascii characters
     */
    private final Pattern asciiPattern = Pattern.compile("/[\\x20-\\x7E\\x80-\\xFF]/");

    @Override
    public boolean isAllowed(Player player, String message) {
        return !asciiPattern.matcher(message).matches();
    }

    @Override
    public String getFailureErrorMessageId() {
        return "error_chat_filter_ascii";
    }

    @Override
    public String getFilterIgnorePermission() {
        return "huskchat.ignore_filters.ascii";
    }

}
