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

package net.william278.huskchat.discord;

import net.william278.huskchat.config.Settings;
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

    public void dispatchWebhook(@NotNull ChatMessage message) {
        CompletableFuture.runAsync(() -> getChannelWebhook(message.targetChannelId).ifPresent(webhookUrl -> {
            try {
                final HttpURLConnection webhookConnection = (HttpURLConnection) webhookUrl.openConnection();
                webhookConnection.setRequestMethod("POST");
                webhookConnection.setDoOutput(true);

                final byte[] jsonMessage = getChatMessageJson(Settings.webhookMessageFormat, message);
                final int messageLength = jsonMessage.length;
                webhookConnection.setFixedLengthStreamingMode(messageLength);
                webhookConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                webhookConnection.connect();
                try (OutputStream messageOutputStream = webhookConnection.getOutputStream()) {
                    messageOutputStream.write(jsonMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * Get the discord chat message json for the given message.
     *
     * @param format  The discord message format to use
     * @param message The message to format
     * @return the json message as a byte array
     * @implNote Quotes message will be escaped before dispatch
     */
    private byte[] getChatMessageJson(@NotNull DiscordMessageFormat format, @NotNull ChatMessage message) {
        return format.postMessageFormat
                .replace("{SENDER_UUID}", message.sender.getUuid().toString())
                .replace("{SENDER_CHANNEL}", message.targetChannelId)
                .replace("{CURRENT_TIMESTAMP}", ZonedDateTime.now()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .replace("{SENDER_USERNAME}", message.sender.getName())
                .replace("{CHAT_MESSAGE}", message.message
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\""))
                .getBytes(StandardCharsets.UTF_8);
    }

}
