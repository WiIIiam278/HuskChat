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

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.message.ChatMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a discord webhook
 */
public class Webhook {

    private final HuskChat plugin;

    // Get the webhook URL for a channel by its ID
    private Optional<URL> getWebhookUrl(@NotNull String channelId) {
        final Map<String, URL> urls = plugin.getSettings().getWebhookUrls();
        if (urls.containsKey(channelId)) {
            return Optional.of(urls.get(channelId));
        }
        return Optional.empty();
    }

    public Webhook(@NotNull HuskChat plugin) {
        this.plugin = plugin;
    }

    /**
     * Dispatch a {@link ChatMessage} to a discord webhook
     *
     * @param message The message to dispatch
     */
    public void dispatchWebhook(@NotNull ChatMessage message) {
        CompletableFuture.runAsync(() -> getWebhookUrl(message.targetChannelId).ifPresent(webhookUrl -> {
            try {
                final HttpURLConnection webhookConnection = (HttpURLConnection) webhookUrl.openConnection();
                webhookConnection.setRequestMethod("POST");
                webhookConnection.setDoOutput(true);

                final byte[] jsonMessage = getChatMessageJson(plugin.getSettings().getWebhookMessageFormat(), message);
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
     */
    private byte[] getChatMessageJson(@NotNull Webhook.Format format, @NotNull ChatMessage message) {
        return format.getPostMessageFormat(plugin)
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

    /**
     * Message format definitions for Discord webhooks
     */
    public enum Format {

        EMBEDDED,
        INLINE;

        /**
         * Get the discord message format by name if it exists
         *
         * @param formatName The name of the message format
         * @return the {@link Format}
         */
        public static Optional<Format> getMessageFormat(@NotNull String formatName) {
            for (Format format : Format.values()) {
                if (format.name().equalsIgnoreCase(formatName)) {
                    return Optional.of(format);
                }
            }
            return Optional.empty();
        }

        @NotNull
        public String getPostMessageFormat(@NotNull HuskChat plugin) {
            try {
                return new String(plugin.getResource(
                        "discord/" + name().toLowerCase(Locale.ENGLISH) + "_message.json"
                ).readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load \"" + name() + "\" Discord message format", e);
            }
        }

    }
}
