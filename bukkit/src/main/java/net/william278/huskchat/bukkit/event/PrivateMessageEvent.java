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

package net.william278.huskchat.bukkit.event;

import net.william278.huskchat.event.IPrivateMessageEvent;
import net.william278.huskchat.player.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PrivateMessageEvent extends BukkitEvent implements IPrivateMessageEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private List<Player> recipients;
    private String message;

    public PrivateMessageEvent(@NotNull Player player, @NotNull List<Player> recipients, @NotNull String message) {
        super(player);
        this.recipients = recipients;
        this.message = message;
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

    @Override
    @NotNull
    public Player getSender() {
        return player;
    }

    @NotNull
    @Override
    public List<Player> getRecipients() {
        return recipients;
    }

    @NotNull
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setSender(@NotNull Player sender) {
        this.player = sender;
    }

    @Override
    public void setRecipients(@NotNull List<Player> recipients) {
        this.recipients = recipients;
    }

    @Override
    public void setMessage(@NotNull String message) {
        this.message = message;
    }

}
