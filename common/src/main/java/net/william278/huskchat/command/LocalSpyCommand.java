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

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class LocalSpyCommand extends CommandBase {

    public LocalSpyCommand(@NotNull HuskChat plugin) {
        super(plugin.getSettings().getLocalSpy().getLocalspyAliases(), "[color]", plugin);
        this.operatorOnly = true;
    }

    @Override
    public void onExecute(@NotNull OnlineUser player, @NotNull String[] args) {
        if (player instanceof ConsoleUser) {
            plugin.getLocales().sendMessage(player, "error_in_game_only");
            return;
        }
        if (args.length == 1) {
            UserCache.SpyColor color;
            Optional<UserCache.SpyColor> selectedColor = UserCache.SpyColor.getColor(args[0]);
            if (selectedColor.isPresent()) {
                try {
                    color = selectedColor.get();
                    plugin.getUserCache().setLocalSpy(player, color);
                    plugin.getLocales().sendMessage(player, "local_spy_toggled_on_color",
                            color.colorCode, color.name().toLowerCase().replaceAll("_", " "));
                } catch (IOException e) {
                    plugin.log(Level.SEVERE, "Failed to save local spy state to spies file");
                }
                return;
            }
        }
        if (!plugin.getUserCache().isLocalSpying(player)) {
            try {
                plugin.getUserCache().setLocalSpy(player);
                plugin.getLocales().sendMessage(player, "local_spy_toggled_on");
            } catch (IOException e) {
                plugin.log(Level.SEVERE, "Failed to save local spy state to spies file");
            }
        } else {
            try {
                plugin.getUserCache().removeLocalSpy(player);
                plugin.getLocales().sendMessage(player, "local_spy_toggled_off");
            } catch (IOException e) {
                plugin.log(Level.SEVERE, "Failed to save local spy state to spies file");
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull OnlineUser player, @NotNull String[] args) {
        return List.of();
    }

}
