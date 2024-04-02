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
import de.exlll.configlib.Polymorphic;
import de.exlll.configlib.PolymorphicTypes;
import lombok.*;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * An abstract representation of a chat filterer
 */
@SuppressWarnings("FieldMayBeFinal")
@Getter
@Setter
@AllArgsConstructor
public abstract class ChatFilter {

    protected final FilterSettings settings;

    /**
     * Takes a user's message and returns true if the message passes the filter
     *
     * @param message The user's message
     * @return {@code true} if the filter allows the message to pass; {@code false} otherwise
     */
    public abstract boolean isAllowed(@NotNull OnlineUser sender, @NotNull String message);

    /**
     * The ID of the locale to send the player if their message fails the filter
     *
     * @return the failure message ID
     */
    @NotNull
    public abstract String getDisallowedLocale();

    /**
     * The permission node users can have to bypass this filter
     *
     * @return filter bypass permission node
     */
    @NotNull
    public abstract String getIgnorePermission();

    @SuppressWarnings("FieldMayBeFinal")
    @Getter
    @Configuration
    @Polymorphic
    @PolymorphicTypes({
            @PolymorphicTypes.Type(type = FilterSettings.class, alias = "filter"),
            @PolymorphicTypes.Type(type = CapsFilter.CapsFilterSettings.class, alias = "caps"),
            @PolymorphicTypes.Type(type = ProfanityFilterer.ProfanityFilterSettings.class, alias = "profanity"),
            @PolymorphicTypes.Type(type = SpamFilter.SpamFilterSettings.class, alias = "spam"),
            @PolymorphicTypes.Type(type = RepeatFilter.RepeatFilterSettings.class, alias = "repeat"),
            @PolymorphicTypes.Type(type = RegexFilter.RegexFilterSettings.class, alias = "regex"),
            @PolymorphicTypes.Type(type = EmojiReplacer.EmojiReplacerSettings.class, alias = "emoji"),
    })
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class FilterSettings {
        protected boolean enabled = true;
        private List<String> channels = List.of("global", "local");
        private boolean privateMessages = true;
        private boolean broadcastMessages = false;
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        // Filters
        ADVERTISING(AdvertisingFilterer.getDefaultSettings(), AdvertisingFilterer::new),
        CAPS(CapsFilter.getDefaultSettings(), CapsFilter::new),
        SPAM(SpamFilter.getDefaultSettings(), SpamFilter::new),
        PROFANITY(ProfanityFilterer.getDefaultSettings(), ProfanityFilterer::new),
        REPEAT(RepeatFilter.getDefaultSettings(), RepeatFilter::new),
        ASCII(AsciiFilter.getDefaultSettings(), AsciiFilter::new),
        REGEX(RegexFilter.getDefaultSettings(), RegexFilter::new),

        // Replacers
        EMOJI(EmojiReplacer.getDefaultSettings(), EmojiReplacer::new);

        private final FilterSettings defaultSettings;
        private final Function<FilterSettings, ChatFilter> creator;

        @NotNull
        public String toString() {
            return this.name().toLowerCase(Locale.ENGLISH);
        }
    }

    /**
     * A special kind of {@link ChatFilter} that can modify the contents of a message
     */
    public abstract static class ReplacerFilter extends ChatFilter {

        public ReplacerFilter(@NotNull FilterSettings settings) {
            super(settings);
        }

        /**
         * Replace the input message from the user
         *
         * @param message The input message
         * @return The output - replaced message
         */
        @NotNull
        public abstract String replace(@NotNull String message);

    }
}
