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

package net.william278.huskchat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.william278.huskchat.HuskChat;
import org.jetbrains.annotations.NotNull;

public record VelocityEventChatListener(@NotNull HuskChat plugin) implements VelocityChatListener {

    @Subscribe(order = PostOrder.LATE)
    public void onPlayerChat(PlayerChatEvent e) {
        if (!e.getResult().isAllowed()) {
            return;
        }
        if (!this.handlePlayerChat(e)) {
            e.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Override
    @NotNull
    public HuskChat plugin() {
        return plugin;
    }

}
