package net.william278.huskchat.message;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.HashMap;
import java.util.Map;

public abstract class MessageManager {

    private static final Map<String, String> messages = new HashMap<>();

    public MessageManager(YamlDocument messagesConfig) {
        load(messagesConfig);
    }

    private void load(YamlDocument messagesConfig) {
        messages.clear();
        for (Object messageKeyObject : messagesConfig.getKeys()) {
            final String messageId = (String) messageKeyObject;
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

    // Send a message to the correct channel
    public abstract void sendMessage(Player player, String messageID, String... placeholderReplacements);

    public abstract void sendMessage(Player player, String messageId);

    public abstract void sendCustomMessage(Player player, String message);

    public abstract void sendFormattedChannelMessage(Player target, Player sender, Channel channel, String message);

    public abstract void sendFormattedOutboundPrivateMessage(Player recipient, Player sender, String message);

    public abstract void sendFormattedInboundPrivateMessage(Player recipient, Player sender, String message);

    public abstract void sendFormattedLocalSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender, Channel channel, String message);

    public abstract void sendFormattedSocialSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender, Player receiver, String message);

    public abstract void sendFormattedBroadcastMessage(Player recipient, String message);
}
