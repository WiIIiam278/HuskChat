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

package net.william278.huskchat.event;

import net.md_5.bungee.api.ProxyServer;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BungeeEventProvider extends EventProvider {

    // To keep compatibility with the Velocity implementation, the Bungee events also return CompletableFuture
    @Override
    default CompletableFuture<ChatMessageEvent> fireChatMessageEvent(@NotNull OnlineUser sender, @NotNull String message, @NotNull String channelId) {
        final CompletableFuture<ChatMessageEvent> completableFuture = new CompletableFuture<>();
        completableFuture.complete(getProxy().getPluginManager().callEvent(new BungeeChatMessageEvent(sender, message, channelId)));
        return completableFuture;
    }

    @Override
    default CompletableFuture<PrivateMessageEvent> firePrivateMessageEvent(@NotNull OnlineUser sender, @NotNull List<OnlineUser> receivers, @NotNull String message) {
        final CompletableFuture<PrivateMessageEvent> completableFuture = new CompletableFuture<>();
        completableFuture.complete(getProxy().getPluginManager().callEvent(new BungeePrivateMessageEvent(sender, receivers, message)));
        return completableFuture;
    }

    @Override
    default CompletableFuture<BroadcastMessageEvent> fireBroadcastMessageEvent(@NotNull OnlineUser sender, @NotNull String message) {
        final CompletableFuture<BroadcastMessageEvent> completableFuture = new CompletableFuture<>();
        completableFuture.complete(getProxy().getPluginManager().callEvent(new BungeeBroadcastMessageEvent(sender, message)));
        return completableFuture;
    }

    ProxyServer getProxy();

}
