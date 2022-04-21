package net.william278.huskchat.message;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MessageManager {

    private static final Map<String, String> messages = new HashMap<>();

    public MessageManager(YamlDocument messagesConfig) {
        load(messagesConfig);
    }

    private void load(YamlDocument messagesConfig) {
        messages.clear();
        for (String messageId : messagesConfig.getRoutesAsStrings(false)) {
            messages.put(messageId, messagesConfig.getString(messageId, ""));
        }
    }

    public String getRawMessage(String messageID) {
        return messages.get(messageID);
    }

    public String getRawMessage(String messageID, String... placeholderReplacements) {
        String message = messages.get(messageID);
        int replacementIndexer = 1;

        // Replace placeholders
        for (String replacement : placeholderReplacements) {
            String replacementString = "%" + replacementIndexer + "%";
            message = message.replace(replacementString, replacement);
            replacementIndexer = replacementIndexer + 1;
        }
        return message;
    }

    public String extractMineDownLinks(String string) {
        String[] in = string.split("\n");
        String out = "";
        // This regex extracts the text and url, only supports one link per line.
        // Definitely not a great solution, but it's better than any alternatives I can think of.
        String regex = "[^\\[\\]\\(\\) ]*\\[([^\\(\\)]+)\\]\\([^\\(\\)]+open_url=(\\S+).*\\)";

        for (int i = 0; i < in.length; i++) {
            Pattern pattern = Pattern.compile(regex);
            Matcher m = pattern.matcher(in[i]);

            if (m.find()) {
                // match 0 is the whole match, 1 is the text, 2 is the url
                out += in[i].replace(m.group(0), "");
                out += m.group(2);
            } else {
                out += in[i];
            }

            if (i + 1 != in.length) {
                out += "\n";
            }
        }

        return out;
    }

    // Send a message to the correct channel
    public abstract void sendMessage(Player player, String messageID, String... placeholderReplacements);

    public abstract void sendMessage(Player player, String messageId);

    public abstract void sendCustomMessage(Player player, String message);

    public abstract void sendFormattedChannelMessage(Player target, Player sender, Channel channel, String message);

    public abstract void sendFormattedOutboundPrivateMessage(Player messageSender, Player messageRecipient, String message);

    public abstract void sendFormattedInboundPrivateMessage(Player messageRecipient, Player messageSender, String message);

    public abstract void sendFormattedLocalSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender, Channel channel, String message);

    public abstract void sendFormattedSocialSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender, Player receiver, String message);

    public abstract void sendFormattedBroadcastMessage(Player recipient, String message);
}
