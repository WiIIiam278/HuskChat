package net.william278.huskchat.filter;

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

    public ProfanityFilterer(ProfanityFilterMode filterMode, double thresholdValue) {
        this.thresholdValue = thresholdValue;
        this.profanityFilterMode = filterMode;
        this.profanityFiltererRunnable = new ProfanityFiltererRunnable();
        new Thread(profanityFiltererRunnable).start();
    }

    @Override
    public boolean isAllowed(String message) {
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

    /**
     * Returns if the message contains profanity as per the parameters
     *
     * @param message The message to check
     * @return {@code true} if the message is profane
     */
    private boolean getIsProfane(String message) throws ExecutionException, InterruptedException {
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

        public ProfanityFiltererRunnable() {
            threadState = ThreadState.SETUP;
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
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                switch (threadState) {
                    case SETUP -> {
                        profanityChecker = new ProfanityChecker();
                        threadState = ThreadState.ASLEEP;
                    }
                    case CALCULATE -> {
                        if (profanityFilterMode == ProfanityFilterMode.AUTOMATIC) {
                            output.complete(profanityChecker.isTextProfane(input));
                        } else if (profanityFilterMode == ProfanityFilterMode.TOLERANCE) {
                            output.complete(profanityChecker.getTextProfanityLikelihood(input));
                        }
                        threadState = ThreadState.ASLEEP;
                    }
                    case TERMINATED -> {
                        profanityChecker.dispose();
                        executor.shutdown();
                    }
                }
            }, 0, 20, TimeUnit.MILLISECONDS);
        }

        public enum ThreadState {
            ASLEEP,
            SETUP,
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
