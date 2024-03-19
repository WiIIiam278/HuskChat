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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RegexFilter extends ChatFilter {

    public RegexFilter(@NotNull FilterSettings settings) {
        super(settings);
    }

    @Override
    public boolean isAllowed(@NotNull OnlineUser sender, @NotNull String message) {
        if (!settings.isEnabled()) {
            return true;
        }
        for (String pattern : ((RegexFilterSettings) settings).getPatterns()) {
            if (message.matches(pattern)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @NotNull
    public String getDisallowedLocale() {
        return "error_chat_filter_regex";
    }

    @Override
    @NotNull
    public String getIgnorePermission() {
        return "huskchat.ignore_filters.regex";
    }

    @NotNull
    public static FilterSettings getDefaultSettings() {
        return new RegexFilterSettings();
    }

    @Getter
    @Configuration
    public static class RegexFilterSettings extends FilterSettings {
        private List<String> patterns = new ArrayList<>();

        private RegexFilterSettings() {
            this.enabled = false;
        }
    }

}
