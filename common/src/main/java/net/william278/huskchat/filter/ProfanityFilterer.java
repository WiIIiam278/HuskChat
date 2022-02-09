package net.william278.huskchat.filter;

import net.william278.profanitycheckerapi.ProfanityChecker;

/**
 * A {@link ChatFilter} that filters against profanity using machine learning
 * Uses <a href="https://github.com/WiIIiam278/ProfanityCheckerAPI/">ProfanityCheckerAPI</a>, which uses jep to run a python
 * machine learning algorithm to determine the probability that a string contains profanity
 */
public class ProfanityFilterer extends ChatFilter {

    private ProfanityChecker profanityChecker;
    private final ProfanityFilterMode filterMode;
    private final double thresholdValue;

    public ProfanityFilterer(ProfanityFilterMode filterMode, double thresholdValue) {
        this.filterMode = filterMode;
        this.thresholdValue = thresholdValue;
        new Thread(() -> profanityChecker = new ProfanityChecker()).start();
    }

    @Override
    public boolean isAllowed(String message) {
        return filterProfanity(message);
    }

    @Override
    public String getFailureErrorMessageId() {
        return "error_chat_filter_profanity";
    }

    /**
     * Returns if the message contains profanity as per the parameters
     *
     * @param message The message to check
     * @return {@code true} if the message is profane
     */
    private boolean filterProfanity(String message) {
        if (profanityChecker == null) return false;
        return switch (filterMode) {
            case AUTOMATIC -> profanityChecker.isTextProfane(message);
            case TOLERANCE -> profanityChecker.getTextProfanityLikelihood(message) >= thresholdValue;
        };
    }

    /**
     * Dispose of the ProfanityChecker instance
     */
    public void dispose() {
        if (profanityChecker != null) {
            profanityChecker.dispose();
        }
    }

    /**
     * Determines the profanity filtering mode to use
     */
    public enum ProfanityFilterMode {
        /**
         * The filter will automatically determine if text is profane
         */
        AUTOMATIC,

        /**
         * The filter will assign a probability score that text is profane and check against a tolerance threshold
         */
        TOLERANCE
    }

}
