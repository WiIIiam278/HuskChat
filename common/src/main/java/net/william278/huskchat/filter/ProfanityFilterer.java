package net.william278.huskchat.filter;

import net.william278.huskchat.player.Player;
import net.william278.profanitycheckerapi.ProfanityChecker;
import net.william278.profanitycheckerapi.ProfanityCheckerBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link ChatFilter} that filters against profanity using machine learning
 * Uses <a href="https://github.com/WiIIiam278/ProfanityCheckerAPI/">ProfanityCheckerAPI</a>, which uses jep to run a python
 * machine learning algorithm to determine the probability that a string contains profanity
 */
public class ProfanityFilterer extends ChatFilter {
    
    @NotNull
    private final ProfanityCheckerBuilder builder;

    public ProfanityFilterer(@NotNull ProfanityFilterMode filterMode, double thresholdValue,
                             @Nullable String libraryPath) {
        this.builder = ProfanityChecker.builder();
        if (libraryPath != null && !libraryPath.isBlank()) {
            builder.withLibraryPath(libraryPath);
        }
        if (filterMode == ProfanityFilterMode.TOLERANCE) {
            builder.withThresholdChecking(thresholdValue);
        }
        initialize();
    }

    /**
     * Pre-initializes the {@link ProfanityChecker}, by generating an instance of the class
     */
    private void initialize() {
        try (final ProfanityChecker ignored = builder.build()) {
            System.out.println("Initialized the profanity checker and hooked into the jep interpreter");
        } catch (UnsatisfiedLinkError | IllegalStateException e) {
            throw new RuntimeException("Failed to initialize ProfanityChecker (" + e.getMessage() + ")" +
                                       "Please ensure that the jep library is installed and the library path is correct. " +
                                       "Consult the HuskChat docs for more information on this error.", e);
        }
    }

    @Override
    public boolean isAllowed(Player player, String message) {
        try (final ProfanityChecker checker = builder.build()) {
            return !checker.isProfane(message);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getFailureErrorMessageId() {
        return "error_chat_filter_profanity";
    }

    @Override
    public String getFilterIgnorePermission() {
        return "huskchat.ignore_filters.profanity";
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
