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

package net.william278.huskchat.bungeecord.event;

import net.william278.huskchat.event.IPrivateMessageEvent;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PrivateMessageEvent extends BungeeEvent implements IPrivateMessageEvent {
    private Player sender;
    private ArrayList<Player> recipients;
    private String message;

    public PrivateMessageEvent(Player sender, ArrayList<Player> recipients, String message) {
        this.sender = sender;
        this.recipients = recipients;
        this.message = message;
    }

    @Override
    public Player getSender() {
        return sender;
    }

    @Override
    public ArrayList<Player> getRecipients() {
        return recipients;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setSender(@NotNull Player sender) {
        this.sender = sender;
    }

    @Override
    public void setRecipients(@NotNull ArrayList<Player> recipients) {
        this.recipients = recipients;
    }

    @Override
    public void setMessage(@NotNull String message) {
        this.message = message;
    }
}
