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

package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.user.ConsoleUser;
import net.william278.huskchat.user.OnlineUser;
import net.william278.huskchat.user.UserCache;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SocialSpyCommand extends CommandBase {

    public SocialSpyCommand(@NotNull HuskChat plugin) {
        super(plugin.getSettings().getSocialSpy().getSocialspyAliases(), "[color]", plugin);
        this.operatorOnly = true;
    }

    @Override
    public void onExecute(@NotNull OnlineUser player, @NotNull String[] args) {
        if (player instanceof ConsoleUser) {
            plugin.getLocales().sendMessage(player, "error_in_game_only");
            return;
        }

        // Set with color
        if (args.length == 1) {
            final Optional<UserCache.SpyColor> selectedColor = UserCache.SpyColor.getColor(args[0]);
            if (selectedColor.isEmpty()) {
                plugin.getLocales().sendMessage(player, "error_invalid_syntax", getUsage());
                return;
            }

            final UserCache.SpyColor color = selectedColor.get();
            plugin.editUserCache(c -> c.setSocialSpy(player, color));
            plugin.getLocales().sendMessage(player, "social_spy_toggled_on_color",
                    color.colorCode, color.name().toLowerCase().replaceAll("_", " "));
            return;
        }

        // Toggle without specifying color
        if (!plugin.getUserCache().isSocialSpying(player)) {
            plugin.editUserCache(c -> c.setSocialSpy(player));
            plugin.getLocales().sendMessage(player, "social_spy_toggled_on");
            return;
        }
        plugin.editUserCache(c -> c.removeSocialSpy(player));
        plugin.getLocales().sendMessage(player, "social_spy_toggled_off");
    }

    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull OnlineUser player, @NotNull String[] args) {
        return Arrays.stream(UserCache.SpyColor.values())
                .map(UserCache.SpyColor::name).map(String::toLowerCase)
                .toList();
    }

}
