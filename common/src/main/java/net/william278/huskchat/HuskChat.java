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

package net.william278.huskchat;

import net.kyori.adventure.audience.Audience;
import net.william278.huskchat.discord.WebhookDispatcher;
import net.william278.huskchat.event.EventDispatcher;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.placeholderparser.PlaceholderParser;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.util.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface HuskChat {

    @NotNull
    MessageManager getMessageManager();

    @NotNull
    EventDispatcher getEventDispatcher();

    Optional<WebhookDispatcher> getWebhookDispatcher();

    void reloadSettings();

    void reloadMessages();

    @NotNull
    String getMetaVersion();

    @NotNull
    String getMetaDescription();

    @NotNull
    String getMetaPlatform();

    PlaceholderParser getParser();

    DataGetter getDataGetter();

    Optional<Player> getPlayer(UUID uuid);

    Optional<Player> matchPlayer(String username);

    Collection<Player> getOnlinePlayers();

    Collection<Player> getOnlinePlayersOnServer(Player player);

    Audience getConsoleAudience();

    @NotNull
    Logger getLoggingAdapter();

    File getDataFolder();

    InputStream getResourceAsStream(String path);

}
