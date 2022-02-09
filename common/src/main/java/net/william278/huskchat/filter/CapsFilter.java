package net.william278.huskchat.filter;

/**
 * An abstract representation of a chat filterer
 */
public class CapsFilter extends ChatFilter {

    private final double capsPercentage;

    public CapsFilter(double capsPercentage) {
        this.capsPercentage = capsPercentage;
    }

    @Override
    public boolean isAllowed(String message) {
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
}
