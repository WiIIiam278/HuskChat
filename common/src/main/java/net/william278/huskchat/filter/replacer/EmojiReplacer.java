package net.william278.huskchat.filter.replacer;

import net.william278.huskchat.player.Player;

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
    public String replace(String message) {
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
    public boolean isAllowed(Player sender, String message) {
        return true;
    }

    @Override
    public String getFailureErrorMessageId() {
        return null;
    }

    @Override
    public String getFilterIgnorePermission() {
        return "huskchat.ignore_filters.emoji_replacer";
    }

}
