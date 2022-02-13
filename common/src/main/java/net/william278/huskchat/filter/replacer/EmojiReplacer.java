package net.william278.huskchat.filter.replacer;

import net.william278.huskchat.player.Player;

import java.util.HashMap;
import java.util.StringJoiner;

/**
 * A {@link ReplacerFilter} that replaces chat emoji with the character emote
 */
public class EmojiReplacer extends ReplacerFilter {

    private final HashMap<String, String> emoticons;

    public EmojiReplacer(HashMap<String, String> emoticons) {
        this.emoticons = emoticons;
    }

    @Override
    public String replace(String message) {
        String[] words = message.split(" ");
        StringJoiner replacedMessage = new StringJoiner(" ");
        for (String word : words) {
            for (String emoteFormat : emoticons.keySet()) {
                if (word.equals(emoteFormat)) {
                    word = emoticons.get(emoteFormat);
                    break;
                }
            }
            replacedMessage.add(word);
        }
        return null;
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
