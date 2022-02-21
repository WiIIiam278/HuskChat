package net.william278.huskchat.filter;

import net.william278.huskchat.player.Player;
import net.william278.profanitycheckerapi.ProfanityChecker;

import java.util.concurrent.*;

/**
 * A {@link ChatFilter} that filters against profanity using machine learning
 * Uses <a href="https://github.com/WiIIiam278/ProfanityCheckerAPI/">ProfanityCheckerAPI</a>, which uses jep to run a python
 * machine learning algorithm to determine the probability that a string contains profanity
 */
public class ProfanityFilterer extends ChatFilter {

    private final double thresholdValue;

    private final ProfanityFilterMode profanityFilterMode;

    private final ProfanityFiltererRunnable profanityFiltererRunnable;

    public ProfanityFilterer(ProfanityFilterMode filterMode, double thresholdValue, String libraryPath) {
        this.thresholdValue = thresholdValue;
        this.profanityFilterMode = filterMode;
        this.profanityFiltererRunnable = new ProfanityFiltererRunnable(libraryPath);
        new Thread(profanityFiltererRunnable).start();
    }

    @Override
    public boolean isAllowed(Player player, String message) {
        try {
            return !getIsProfane(message);
        } catch (InterruptedException | ExecutionException e) {
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
     * Returns if the message contains profanity as per the parameters
     *
     * @param message The message to check
     * @return {@code true} if the message is profane
     */
    private boolean getIsProfane(String message) throws ExecutionException, InterruptedException {
        // Allow all messages if the filter is terminated
        if (profanityFiltererRunnable.threadState == ProfanityFiltererRunnable.ThreadState.TERMINATED) {
            return false;
        }
        return switch (profanityFilterMode) {
            case AUTOMATIC -> profanityFiltererRunnable.calculateIsProfane(message);
            case TOLERANCE -> profanityFiltererRunnable.calculateIsProfane(message, thresholdValue);
        };
    }

    /**
     * Dispose of the ProfanityChecker instance
     */
    public void dispose() {
        profanityFiltererRunnable.threadState = ProfanityFiltererRunnable.ThreadState.TERMINATED;
    }

    private static class ProfanityFiltererRunnable implements Runnable {

        private ProfanityChecker profanityChecker;

        private ThreadState threadState;
        private ProfanityFilterMode profanityFilterMode;
        private String input;
        private CompletableFuture<Object> output;

        private final String libraryPath;

        public ProfanityFiltererRunnable(String libraryPath) {
            this.libraryPath = libraryPath;
        }

        public boolean calculateIsProfane(String text) {
            try {
                return (boolean) calculate(text, ProfanityFilterMode.AUTOMATIC).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new CompletableFuture<>().complete(false);
            }
        }

        public boolean calculateIsProfane(String text, double threshold) {
            try {
                return ((double) calculate(text, ProfanityFilterMode.TOLERANCE).get()) > threshold;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new CompletableFuture<>().complete(false);
            }
        }

        private CompletableFuture<Object> calculate(String text, ProfanityFilterMode mode) {
            output = new CompletableFuture<>();
            profanityFilterMode = mode;
            input = text;
            threadState = ThreadState.CALCULATE;
            return output;
        }

        @Override
        public void run() {
            // Setup
            try {
                profanityChecker = libraryPath.isEmpty() ? new ProfanityChecker() : new ProfanityChecker(libraryPath);
                threadState = ThreadState.ASLEEP;
            } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                // Detect improperly installed jep and display slightly friendlier error
                System.out.println("""
                        \033[1;31m[HuskChat] Error initializing the profanity checking filter: UnsatisfiedLinkError
                        \033[1;31m• The profanity checking filter requires Python 3.8+ with the jep and alt-profanity-check dependencies installed on the system.
                        \033[1;31m• This error indicates that the plugin was unable to find the necessary jep driver file.
                        \033[1;31m• Please ensure the the jep library file path has been set correctly on your system.
                        \033[1;31m• Please consult the HuskChat README file for more information on properly setting up the profanity checking filter.
                        \033[1;31m• The profanity checking filter has been disabled.
                        \033[1;31mStack trace:""");
                unsatisfiedLinkError.printStackTrace();
                threadState = ThreadState.TERMINATED;
            }

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                switch (threadState) {
                    case CALCULATE -> {
                        if (profanityFilterMode == ProfanityFilterMode.AUTOMATIC) {
                            output.complete(profanityChecker.isTextProfane(input));
                        } else if (profanityFilterMode == ProfanityFilterMode.TOLERANCE) {
                            output.complete(profanityChecker.getTextProfanityLikelihood(input));
                        }
                        threadState = ThreadState.ASLEEP;
                    }
                    case TERMINATED -> {
                        if (profanityChecker != null) {
                            profanityChecker.close();
                        }
                        executor.shutdown();
                    }
                }
            }, 0, 20, TimeUnit.MILLISECONDS);
        }

        public enum ThreadState {
            ASLEEP,
            CALCULATE,
            TERMINATED
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
