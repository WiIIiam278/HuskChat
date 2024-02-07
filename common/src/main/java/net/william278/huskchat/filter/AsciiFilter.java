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

import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * A {@link ChatFilter} that filters against unicode (non-ASCII) characters
 */
public class AsciiFilter extends ChatFilter {

    /**
     * Regex pattern matching only ascii characters
     */
    private final Pattern asciiPattern = Pattern.compile("^[\\u0000-\\u007F]*$");

    public AsciiFilter(@NotNull FilterSettings settings) {
        super(settings);
    }

    @NotNull
    public static FilterSettings getDefaultSettings() {
        return new FilterSettings();
    }

    @Override
    public boolean isAllowed(@NotNull OnlineUser player, @NotNull String message) {
        return asciiPattern.matcher(message).matches();
    }

    @Override
    @NotNull
    public String getDisallowedLocale() {
        return "error_chat_filter_ascii";
    }

    @Override
    @NotNull
    public String getIgnorePermission() {
        return "huskchat.ignore_filters.ascii";
    }

}
