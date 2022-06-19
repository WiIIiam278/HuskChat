package net.william278.huskchat.discord;

import net.william278.huskchat.message.ChatMessage;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class WebhookDispatcher {

    private final Map<String, URL> channelWebhooks;

    private Optional<URL> getChannelWebhook(@NotNull String channelId) {
        if (channelWebhooks.containsKey(channelId)) {
            return Optional.of(channelWebhooks.get(channelId));
        }
        return Optional.empty();
    }

    public WebhookDispatcher(@NotNull Map<String, URL> channelWebhooks) {
        this.channelWebhooks = channelWebhooks;
    }

    public CompletableFuture<Void> dispatchWebhook(@NotNull ChatMessage message) {
        return CompletableFuture.runAsync(() -> {
            getChannelWebhook(message.targetChannelId).ifPresent(webhookUrl -> {
                System.out.println("dispatching webhook to " + webhookUrl.toString());
                try {
                    final HttpURLConnection webhookConnection = (HttpURLConnection) webhookUrl.openConnection();
                    webhookConnection.setRequestMethod("POST");
                    webhookConnection.setDoOutput(true);

                    final byte[] jsonMessage = getChatMessageJson(message);
                    final int messageLength = jsonMessage.length;
                    webhookConnection.setFixedLengthStreamingMode(messageLength);
                    webhookConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    webhookConnection.connect();
                    try (OutputStream messageOutputStream = webhookConnection.getOutputStream()) {
                        messageOutputStream.write(jsonMessage);
                    }
                    System.out.println("webhook response code: " + webhookConnection.getResponseCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private byte[] getChatMessageJson(@NotNull ChatMessage message) {
        return """
                {
                  "avatar_url": "https://www.spigotmc.org/data/resource_icons/94/94496.jpg",
                  "username": "HuskChat",
                  "content": null,
                  "embeds": [
                    {
                      "description": "{CHAT_MESSAGE}",
                      "color": 64410,
                      "footer": {
                        "text": "{SENDER_USERNAME} • {SENDER_CHANNEL}",
                        "icon_url": "https://crafatar.com/avatars/{SENDER_UUID}?size=64"
                      },
                      "timestamp": "{CURRENT_TIMESTAMP}"
                    }
                  ]
                }"""
                .replaceAll(Pattern.quote("{SENDER_UUID}"), message.sender.getUuid().toString())
                .replaceAll(Pattern.quote("{SENDER_CHANNEL}"), message.targetChannelId)
                .replaceAll(Pattern.quote("{CURRENT_TIMESTAMP}"), ZonedDateTime.now()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .replaceAll(Pattern.quote("{SENDER_USERNAME}"), message.sender.getName())
                .replaceAll(Pattern.quote("{CHAT_MESSAGE}"), message.message)
                .getBytes(StandardCharsets.UTF_8);
    }

}
