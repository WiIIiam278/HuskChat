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

package net.william278.huskchat.placeholderparser;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class DefaultParser implements Placeholders {

    private HuskChat implementingPlugin;
    public DefaultParser(HuskChat implementingPlugin) {
        this.implementingPlugin = implementingPlugin;
    }

    @Override
    public CompletableFuture<String> parsePlaceholders(String message, Player player) {

        final HashMap<String, String> placeholders = new HashMap<>();

        // Player related placeholders
        placeholders.put("%name%", escape(implementingPlugin.getDataGetter().getPlayerName(player)));
        placeholders.put("%fullname%", escape(implementingPlugin.getDataGetter().getPlayerFullName(player)));
        placeholders.put("%prefix%", implementingPlugin.getDataGetter().getPlayerPrefix(player).isPresent() ? implementingPlugin.getDataGetter().getPlayerPrefix(player).get() : "");
        placeholders.put("%suffix%", implementingPlugin.getDataGetter().getPlayerSuffix(player).isPresent() ? implementingPlugin.getDataGetter().getPlayerSuffix(player).get() : "");
        placeholders.put("%ping%", Integer.toString(player.getPing()));
        placeholders.put("%uuid%", player.getUuid().toString());
        placeholders.put("%servername%", Settings.serverNameReplacement.getOrDefault(player.getServerName(), player.getServerName()));
        placeholders.put("%serverplayercount%", Integer.toString(player.getPlayersOnServer()));

        // Time related placeholders
        Date date = new Date();
        placeholders.put("%timestamp%", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date));
        placeholders.put("%time%", new SimpleDateFormat("HH:mm:ss").format(date));
        placeholders.put("%short_time%", new SimpleDateFormat("HH:mm").format(date));
        placeholders.put("%date%", new SimpleDateFormat("yyyy/MM/dd").format(date));
        placeholders.put("%british_date%", new SimpleDateFormat("dd/MM/yyyy").format(date));
        placeholders.put("%day%", new SimpleDateFormat("dd").format(date));
        placeholders.put("%month%", new SimpleDateFormat("MM").format(date));
        placeholders.put("%year%", new SimpleDateFormat("yyyy").format(date));

        for (String placeholder : placeholders.keySet()) {
            final String replacement = placeholders.get(placeholder);
            message = message.replace(placeholder, replacement);
        }

        return CompletableFuture.completedFuture(message);
    }

    private String escape(String string) {
        // Just escaping __ should suffice as the only special character
        // allowed in Minecraft usernames is the underscore.
        // By placing the escape character in the middle, the MineDown
        // parser no longer sees this as a formatting code.
        return string.replace("__", "_\\_");
    }
}
