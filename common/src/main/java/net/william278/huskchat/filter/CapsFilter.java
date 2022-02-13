package net.william278.huskchat.filter;

import net.william278.huskchat.player.Player;

/**
 * A {@link ChatFilter} that filters against players using too many caps in their message
 */
public class CapsFilter extends ChatFilter {

    private final double capsPercentage;

    public CapsFilter(double capsPercentage) {
        this.capsPercentage = capsPercentage;
    }

    @Override
    public boolean isAllowed(Player player, String message) {
        double messageLength = message.length();
        if (messageLength <= 5) {
            return true;
        }
        int capsLetters = 0;
        for (char messageChar : message.toCharArray()) {
            if (Character.isUpperCase(messageChar)) {
                capsLetters++;
            }
        }
        double capsProportion = (double) capsLetters / messageLength;
        return !(capsProportion > capsPercentage);
    }

    @Override
    public String getFailureErrorMessageId() {
        return "error_chat_filter_caps";
    }

    @Override
    public String getFilterIgnorePermission() {
        return "huskchat.ignore_filters.caps";
    }

}
