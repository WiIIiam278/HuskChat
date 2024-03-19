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

import net.william278.huskchat.user.OnlineUser;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BukkitChatMessageEvent extends BukkitEvent implements ChatMessageEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private String message;
    private String channelId;

    protected BukkitChatMessageEvent(@NotNull OnlineUser player,
                                     @NotNull String message,
                                     @NotNull String channelId) {
        super(player);
        this.message = message;
        this.channelId = channelId;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    @NotNull
    @Override
    public OnlineUser getSender() {
        return player;
    }

    @NotNull
    @Override
    public String getMessage() {
        return message;
    }

    @NotNull
    @Override
    public String getChannelId() {
        return channelId;
    }

    @Override
    public void setSender(@NotNull OnlineUser sender) {
        this.player = sender;
    }

    @Override
    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    @Override
    public void setChannelId(@NotNull String channelId) {
        this.channelId = channelId;
    }

}
