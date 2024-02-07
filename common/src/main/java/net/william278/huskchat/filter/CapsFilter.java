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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ChatFilter} that filters against players using too many caps in their message
 */
public class CapsFilter extends ChatFilter {

    public CapsFilter(FilterSettings settings) {
        super(settings);
    }

    @NotNull
    public static FilterSettings getDefaultSettings() {
        return new CapsFilterSettings();
    }

    @Override
    public boolean isAllowed(@NotNull OnlineUser player, @NotNull String message) {
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
        return !(capsProportion > ((CapsFilterSettings) settings).getMaxCapsPercentage());
    }

    @Override
    @NotNull
    public String getDisallowedLocale() {
        return "error_chat_filter_caps";
    }

    @Override
    @NotNull
    public String getIgnorePermission() {
        return "huskchat.ignore_filters.caps";
    }


    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    public static class CapsFilterSettings extends FilterSettings {
        public double maxCapsPercentage = 0.4d;
    }

}
