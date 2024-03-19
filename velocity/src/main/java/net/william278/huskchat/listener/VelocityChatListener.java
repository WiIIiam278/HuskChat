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

import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.user.VelocityUser;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface VelocityChatListener {

    default boolean handlePlayerChat(PlayerChatEvent e) {
        final VelocityUser player = VelocityUser.adapt(e.getPlayer(), plugin());
        final Optional<Channel> channel = plugin().getChannels().getChannel(
                plugin().getUserCache().getPlayerChannel(player.getUuid())
        );
        if (channel.isEmpty()) {
            plugin().getLocales().sendMessage(player, "error_no_channel");
            return false;
        }

        // Send the chat message, determine if the event should be canceled
        return !new ChatMessage(channel.get(), player, e.getMessage(), plugin()).dispatch();
    }

    @NotNull
    HuskChat plugin();

}
