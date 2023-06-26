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
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HuskChatCommand extends CommandBase {

    private final static String[] COMMAND_TAB_ARGUMENTS = {"about", "reload"};

    private final String pluginInformation;

    public HuskChatCommand(@NotNull HuskChat plugin) {
        super(List.of("huskchat"), "[about|reload]", plugin);
        this.pluginInformation = "[HuskChat](#00fb9a bold) [| " + plugin.getMetaPlatform() + " Version " + plugin.getMetaVersion() + "](#00fb9a)\n" +
                                 "[" + plugin.getMetaDescription() + "](gray)\n" +
                                 "[• Author:](white) [William278](gray show_text=&7Click to visit website open_url=https://william278.net)\n" +
                                 "[• Contributors:](white) [TrueWinter](gray show_text=&7Code), [Ironboundred](gray show_text=&7Code)\n" +
                                 "[• Translators:](white) [xF3d3](gray show_text=&7Italian, it-it), [MalzSmith](gray show_text=&7Hungarian, hu-hu), [Ceddix](gray show_text=&7German, de-de), [Pukejoy_1](gray show_text=&7Bulgarian, bg-bg)\n" +
                                 "[• Help Wiki:](white) [[Link]](#00fb9a show_text=&7Click to open link open_url=https://william278.net/docs/huskchat/Home)\n" +
                                 "[• Report Issues:](white) [[Link]](#00fb9a show_text=&7Click to open link open_url=https://github.com/WiIIiam278/HuskChat/issues)\n" +
                                 "[• Support Discord:](white) [[Link]](#00fb9a show_text=&7Click to join open_url=https://discord.gg/tVYhJfyDWG)";
    }

    @Override
    public void onExecute(@NotNull Player player, @NotNull String[] args) {
        if (player.hasPermission(getPermission())) {
            if (args.length == 1) {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "about", "info" -> sendAboutInformation(player);
                    case "reload" -> {
                        plugin.loadConfig();
                        plugin.getLocales().sendCustomMessage(player, "[HuskChat](#00fb9a bold) &#00fb9a&| Reloaded config & message files.");
                    }
                    default ->
                            plugin.getLocales().sendMessage(player, "error_invalid_syntax", "/huskchat <about/reload>");
                }
            } else {
                sendAboutInformation(player);
            }
        } else {
            plugin.getLocales().sendMessage(player, "error_no_permission");
        }
    }

    /**
     * Send the plugin information to the {@link Player}
     *
     * @param player The {@link Player} to send plugin information to
     */
    private void sendAboutInformation(Player player) {
        plugin.getLocales().sendCustomMessage(player, pluginInformation);
    }

    @Override
    public List<String> onTabComplete(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission(getPermission())) {
            return Collections.emptyList();
        }
        if (args.length <= 1) {
            return Arrays.stream(COMMAND_TAB_ARGUMENTS).filter(val -> val.toLowerCase().startsWith((args.length >= 1) ? args[0].toLowerCase() : ""))
                    .sorted().collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
