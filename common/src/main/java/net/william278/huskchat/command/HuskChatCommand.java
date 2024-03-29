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

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.william278.desertwell.about.AboutMenu;
import net.william278.desertwell.util.UpdateChecker;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HuskChatCommand extends CommandBase {

    private final static String[] COMMAND_TAB_ARGUMENTS = {"about", "reload", "update"};

    private final UpdateChecker updateChecker;
    private final AboutMenu aboutMenu;

    public HuskChatCommand(@NotNull HuskChat plugin) {
        super(List.of("huskchat"), "[about|reload|update]", plugin);
        this.updateChecker = plugin.getUpdateChecker();
        this.aboutMenu = AboutMenu.builder()
                .title(Component.text("HuskChat"))
                .description(Component.text(plugin.getPluginDescription()))
                .version(plugin.getVersion())
                .credits("Author",
                        AboutMenu.Credit.of("William278").description("Click to visit website").url("https://william278.net"))
                .credits("Contributors",
                        AboutMenu.Credit.of("TrueWinter").description("Code"),
                        AboutMenu.Credit.of("Ironboundred").description("Code"))
                .credits("Translators",
                        AboutMenu.Credit.of("xF3d3").description("Italian (it-it)"),
                        AboutMenu.Credit.of("MalzSmith").description("Hungarian (hu-hu)"),
                        AboutMenu.Credit.of("Ceddix").description("German (de-de)"),
                        AboutMenu.Credit.of("Pukejoy_1").description("Bulgarian (bg-bg)"),
                        AboutMenu.Credit.of("XeroLe1er").description("French (fr-fr)"),
                        AboutMenu.Credit.of("Wirayuda5620").description("Bahasa Indonesia (id-id)"))
                .buttons(
                        AboutMenu.Link.of("https://william278.net/docs/huskchat").text("Documentation").icon("⛏"),
                        AboutMenu.Link.of("https://github.com/WiIIiam278/HuskChat/issues").text("Issues").icon("❌").color(TextColor.color(0xff9f0f)),
                        AboutMenu.Link.of("https://discord.gg/tVYhJfyDWG").text("Discord").icon("⭐").color(TextColor.color(0x6773f5)))
                .build();
        this.operatorOnly = true;
    }

    @Override
    public void onExecute(@NotNull OnlineUser player, @NotNull String[] args) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "about", "info" -> player.sendMessage(aboutMenu.toComponent());
                case "update" -> updateChecker.check().thenAccept(checked -> {
                    if (checked.isUpToDate()) {
                        plugin.getLocales().sendMessage(player, "up_to_date", plugin.getVersion().toString());
                        return;
                    }
                    plugin.getLocales().sendMessage(player, "update_available",
                            checked.getLatestVersion().toString(), plugin.getVersion().toString());
                });
                case "reload" -> {
                    plugin.loadConfig();
                    player.sendMessage(new MineDown("[HuskChat](#00fb9a bold) &#00fb9a&| Reloaded config & message files."));
                }
                default -> plugin.getLocales().sendMessage(player, "error_invalid_syntax", getUsage());
            }
            return;
        }

        player.sendMessage(aboutMenu.toComponent());
    }

    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull OnlineUser player, @NotNull String[] args) {
        if (args.length <= 1) {
            return Arrays.stream(COMMAND_TAB_ARGUMENTS)
                    .filter(i -> i.toLowerCase().startsWith((args.length == 1) ? args[0].toLowerCase() : ""))
                    .sorted().toList();
        }
        return List.of();
    }

}
