package me.william278.huskchat;

import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MessageManager {

    private static final Map<String, String> messages = new HashMap<>();

    private static final HuskChat plugin = HuskChat.getInstance();

    public static void reloadMessages() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdir();
            }
            final String language = HuskChat.getConfig().getLanguage();
            File messagesFile = new File(plugin.getDataFolder(), "messages-" + language + ".yml");
            if (!messagesFile.exists()) {
                Files.copy(plugin.getResourceAsStream("locales/" + language + ".yml"), messagesFile.toPath());
            }
            fetchMessages(ConfigurationProvider.getProvider(YamlConfiguration.class).load(messagesFile));
        } catch (Exception e) {
            plugin.getLogger().log(Level.CONFIG, "An exception occurred loading the language message file", e);
        }
    }

    private static void fetchMessages(Configuration messagesConfig) {
        messages.clear();
        for (String messageKey : messagesConfig.getKeys()) {
            messages.put(messageKey, messagesConfig.getString(messageKey, ""));
        }
    }

    // Send a message to the correct channel
    public static void sendMessage(ProxiedPlayer p, String messageID, String... placeholderReplacements) {
        String message = getRawMessage(messageID);

        // Don't send empty messages
        if (message == null) {
            return;
        }
        if (message.isEmpty()) {
            return;
        }

        int replacementIndexer = 1;

        // Replace placeholders
        for (String replacement : placeholderReplacements) {
            String replacementString = "%" + replacementIndexer + "%";
            message = message.replace(replacementString, replacement);
            replacementIndexer = replacementIndexer + 1;
        }

        // Convert to baseComponents[] via MineDown formatting and send
        p.sendMessage(ChatMessageType.CHAT, new MineDown(message).replace().toComponent());
    }

    // Send a message with no placeholder parameters
    public static void sendMessage(ProxiedPlayer p, String messageID) {
        String message = getRawMessage(messageID);

        // Don't send empty messages
        if (message == null) {
            return;
        }
        if (message.isEmpty()) {
            return;
        }

        // Convert to baseComponents[] via MineDown formatting and send
        p.sendMessage(new MineDown(message).replace().toComponent());
    }

    public static String getRawMessage(String messageID) {
        return messages.get(messageID);
    }

    public static String getRawMessage(String messageID, String... placeholderReplacements) {
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

}
