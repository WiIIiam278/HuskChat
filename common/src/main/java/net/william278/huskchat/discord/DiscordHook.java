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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface DiscordHook {

    void postMessage(@NotNull ChatMessage message);

    /**
     * Get the discord chat message json for the given message.
     *
     * @param plugin  The discord message format to use
     * @param message The message to format
     * @return the json message as a byte array
     */
    static byte[] getDiscordMessageJson(@NotNull HuskChat plugin, @NotNull ChatMessage message) {
        return plugin.getSettings().getDiscord().getFormatStyle()
                .getPostMessageFormat(plugin)
                .replace("{SENDER_UUID}", message.getSender().getUuid().toString())
                .replace("{SENDER_CHANNEL}", message.getChannel().getId())
                .replace("{CURRENT_TIMESTAMP}", ZonedDateTime.now()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .replace("{SENDER_USERNAME}", message.getSender().getName())
                .replace("{CHAT_MESSAGE}", message.getMessage()
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\""))
                .getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Message format definitions for Discord webhooks
     */
    enum Format {

        EMBEDDED,
        INLINE;

        private String format;

        @NotNull
        private String getPostMessageFormat(@NotNull HuskChat plugin) {
            if (this.format != null) {
                return this.format;
            }
            try {
                return this.format = new String(plugin.getResource(String.format(
                        "discord/%s_message.json", name().toLowerCase(Locale.ENGLISH)
                )).readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load \"" + name() + "\" Discord message format", e);
            }
        }

    }

}
