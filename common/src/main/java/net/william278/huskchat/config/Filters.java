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

package net.william278.huskchat.config;

import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.william278.huskchat.filter.ChatFilter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for loading and storing Chat Filters
 */
@SuppressWarnings("FieldMayBeFinal")
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Filters {

    static final String CONFIG_HEADER = """
            ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
            ┃      HuskChat - Filters      ┃
            ┃    Developed by William278   ┃
            ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
            ┣╸ Information: https://william278.net/project/huskchat/
            ┗╸ Channels Help: https://william278.net/docs/huskchat/filters-and-replacers/""";

    private Map<ChatFilter.Type, ChatFilter.FilterSettings> filters = new HashMap<>(Map.of(
            ChatFilter.Type.ADVERTISING, ChatFilter.Type.ADVERTISING.getDefaultSettings(),
            ChatFilter.Type.CAPS, ChatFilter.Type.CAPS.getDefaultSettings(),
            ChatFilter.Type.REPEAT, ChatFilter.Type.REPEAT.getDefaultSettings(),
            ChatFilter.Type.SPAM, ChatFilter.Type.SPAM.getDefaultSettings(),
            ChatFilter.Type.PROFANITY, ChatFilter.Type.PROFANITY.getDefaultSettings(),
            ChatFilter.Type.ASCII, ChatFilter.Type.ASCII.getDefaultSettings(),
            ChatFilter.Type.REGEX, ChatFilter.Type.REGEX.getDefaultSettings()
    ));

    private Map<ChatFilter.Type, ChatFilter.FilterSettings> replacers = new HashMap<>(Map.of(
            ChatFilter.Type.EMOJI, ChatFilter.Type.EMOJI.getDefaultSettings()
    ));

    public boolean isFilterEnabled(@NotNull ChatFilter.Type type) {
        return filters.get(type).isEnabled();
    }

    public boolean isReplacerEnabled(@NotNull ChatFilter.Type type) {
        return replacers.get(type).isEnabled();
    }


}
