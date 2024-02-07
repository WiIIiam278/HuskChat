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

package net.william278.huskchat.replacer;

import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * A {@link ReplacerFilter} that replaces chat emoji with the character emote
 */
public class EmojiReplacer extends ReplacerFilter {

    private final HashMap<String, String> emoticons;
    private final boolean caseInsensitive;

    public EmojiReplacer(HashMap<String, String> emoticons, boolean caseInsensitive) {
        this.emoticons = emoticons;
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    @NotNull
    public String replace(@NotNull String message) {
        String[] words = message.split(" ");
        StringJoiner replacedMessage = new StringJoiner(" ");
        for (String word : words) {
            for (String emoteFormat : emoticons.keySet()) {
                if (!caseInsensitive) {
                    if (word.equals(emoteFormat)) {
                        word = emoticons.get(emoteFormat);
                        break;
                    }
                } else {
                    if (word.toLowerCase(Locale.ROOT).equals(emoteFormat)) {
                        word = emoticons.get(emoteFormat);
                        break;
                    }
                }
            }
            replacedMessage.add(word);
        }
        return replacedMessage.toString();
    }

    @Override
    public boolean isAllowed(@NotNull OnlineUser sender, @NotNull String message) {
        return true;
    }

    @Override
    @NotNull
    public String getFailureErrorMessageId() {
        throw new UnsupportedOperationException("EmojiReplacer does not support failure messages");
    }

    @Override
    @NotNull
    public String getFilterIgnorePermission() {
        return "huskchat.ignore_filters.emoji_replacer";
    }

}
