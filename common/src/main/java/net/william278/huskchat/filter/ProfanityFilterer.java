/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.filter;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.william278.huskchat.user.OnlineUser;
import net.william278.profanitycheckerapi.ProfanityChecker;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ChatFilter} that filters against profanity using machine learning
 * Uses <a href="https://github.com/WiIIiam278/ProfanityCheckerAPI/">ProfanityCheckerAPI</a>, which uses jep to run a python
 * machine learning algorithm to determine the probability that a string contains profanity
 */
public class ProfanityFilterer extends ChatFilter {

    @NotNull
    private final ProfanityChecker.ProfanityCheckerBuilder builder;

    public ProfanityFilterer(@NotNull FilterSettings settings) {
        super(settings);

        final ProfanityFilterSettings profanitySettings = (ProfanityFilterSettings) settings;
        this.builder = ProfanityChecker.builder();
        if (profanitySettings.getLibraryPath() != null && !profanitySettings.getLibraryPath().isBlank()) {
            builder.libraryPath(profanitySettings.getLibraryPath());
        }
        if (profanitySettings.getMode() == ProfanityFilterMode.TOLERANCE) {
            builder.useThreshold(true);
            builder.threshold(profanitySettings.getTolerance());
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

    @NotNull
    public static FilterSettings getDefaultSettings() {
        return new ProfanityFilterSettings();
    }

    @Override
    public boolean isAllowed(@NotNull OnlineUser player, @NotNull String message) {
        try (final ProfanityChecker checker = builder.build()) {
            return !checker.isProfane(message);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @NotNull
    public String getDisallowedLocale() {
        return "error_chat_filter_profanity";
    }

    @Override
    @NotNull
    public String getIgnorePermission() {
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

    @Getter
    @Configuration
    public static class ProfanityFilterSettings extends FilterSettings {
        public String libraryPath = "";
        public ProfanityFilterMode mode = ProfanityFilterMode.AUTOMATIC;
        public double tolerance = 0.78d;

        private ProfanityFilterSettings() {
            this.enabled = false;
        }

        protected ProfanityFilterSettings(@NotNull String libraryPath, @NotNull ProfanityFilterMode mode,
                                          double tolerance) {
            this.libraryPath = libraryPath;
            this.mode = mode;
            this.tolerance = tolerance;
        }
    }

}
