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

package net.william278.huskchat.velocity.event;

import com.velocitypowered.api.proxy.ProxyServer;
import net.william278.huskchat.event.EventDispatcher;
import net.william278.huskchat.event.IBroadcastMessageEvent;
import net.william278.huskchat.event.IChatMessageEvent;
import net.william278.huskchat.event.IPrivateMessageEvent;
import net.william278.huskchat.player.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class VelocityEventDispatcher implements EventDispatcher {
    private final ProxyServer server;

    public VelocityEventDispatcher(ProxyServer server) {
        this.server = server;
    }

    @Override
    public CompletableFuture<IChatMessageEvent> dispatchChatMessageEvent(Player player, String message, String channelId) {
        return server.getEventManager().fire(new ChatMessageEvent(player, message, channelId));
    }

    @Override
    public CompletableFuture<IPrivateMessageEvent> dispatchPrivateMessageEvent(Player sender, ArrayList<Player> receivers, String message) {
        return server.getEventManager().fire(new PrivateMessageEvent(sender, receivers, message));
    }

    @Override
    public CompletableFuture<IBroadcastMessageEvent> dispatchBroadcastMessageEvent(Player sender, String message) {
        return server.getEventManager().fire(new BroadcastMessageEvent(sender, message));
    }
}
