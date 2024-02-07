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

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.message.ChatMessage;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * Represents a discord webhook
 */
public class WebHook implements DiscordHook {

    private final HuskChat plugin;

    // Get the webhook URL for a channel by its ID
    private Optional<URL> getWebhookUrl(@NotNull String channelId) {
        final Map<String, URL> urls = plugin.getSettings().getDiscord().getChannelWebhooks();
        if (urls.containsKey(channelId)) {
            return Optional.of(urls.get(channelId));
        }
        return Optional.empty();
    }

    public WebHook(@NotNull HuskChat plugin) {
        this.plugin = plugin;
    }

    /**
     * Dispatch a {@link ChatMessage} to a discord webhook
     *
     * @param message The message to dispatch
     */
    @Override
    public void postMessage(@NotNull ChatMessage message) {
        CompletableFuture.runAsync(() -> getWebhookUrl(message.getChannel().getId()).ifPresent(webhookUrl -> {
            try {
                final HttpURLConnection webhookConnection = (HttpURLConnection) webhookUrl.openConnection();
                webhookConnection.setRequestMethod("POST");
                webhookConnection.setDoOutput(true);

                final byte[] jsonMessage = DiscordHook.getDiscordMessageJson(plugin, message);
                final int messageLength = jsonMessage.length;
                webhookConnection.setFixedLengthStreamingMode(messageLength);
                webhookConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                webhookConnection.connect();
                try (OutputStream messageOutputStream = webhookConnection.getOutputStream()) {
                    messageOutputStream.write(jsonMessage);
                }
            } catch (Throwable e) {
                plugin.log(Level.WARNING, "Unable to send message to Discord webhook", e);
            }
        }));
    }

}
