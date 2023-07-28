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

package net.william278.huskchat.placeholders;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.player.Player;
import net.william278.papiproxybridge.api.PlaceholderAPI;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class PAPIProxyBridgeReplacer implements PlaceholderReplacer {

    private final PlaceholderAPI instance;

    public PAPIProxyBridgeReplacer(@NotNull HuskChat plugin) {
        this.instance = PlaceholderAPI.getInstance();
        instance.setCacheExpiry(plugin.getSettings().getPapiProxyBridgeCacheTime());
    }

    @Override
    public CompletableFuture<String> formatPlaceholders(@NotNull String message, @NotNull Player player) {
        return instance.formatPlaceholders(message, player.getUuid());
    }
}
