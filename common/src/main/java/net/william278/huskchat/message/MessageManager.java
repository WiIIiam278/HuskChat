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

package net.william278.huskchat.message;

import de.themoep.minedown.adventure.MineDown;
import de.themoep.minedown.adventure.MineDownParser;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.placeholderparser.Placeholders;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {
    @NotNull
    private final HuskChat plugin;
    @NotNull
    private final Map<String, String> messages = new HashMap<>();

    public MessageManager(@NotNull HuskChat plugin) {
        this.plugin = plugin;
        final String languagePath = "languages/" + Settings.language + ".yml";
        try (InputStream stream = Objects.requireNonNull(plugin.getResourceAsStream(languagePath))) {
            final YamlDocument document = YamlDocument.create(new File(plugin.getDataFolder(),
                    "messages-" + Settings.language + ".yml"), stream);
            this.load(document);
        } catch (IOException | NullPointerException e) {
            plugin.getLoggingAdapter().log(Level.SEVERE, "Failed to load messages file", e);
        }
    }

    private void load(YamlDocument document) {
        messages.clear();
        for (String messageId : document.getRoutesAsStrings(false)) {
            messages.put(messageId, document.getString(messageId, ""));
        }
    }

    public String getRawMessage(@NotNull String messageID) {
        return messages.get(messageID);
    }

    // todo use Adventure's provided serializer?
    public String extractMineDownLinks(String string) {
        String[] in = string.split("\n");
        StringBuilder out = new StringBuilder();

        // This regex extracts the text and url, only supports one link per line.
        String regex = "[^\\[\\]() ]*\\[([^()]+)]\\([^()]+open_url=(\\S+).*\\)";

        for (int i = 0; i < in.length; i++) {
            Pattern pattern = Pattern.compile(regex);
            Matcher m = pattern.matcher(in[i]);

            if (m.find()) {
                // match 0 is the whole match, 1 is the text, 2 is the url
                out.append(in[i].replace(m.group(0), ""));
                out.append(m.group(2));
            } else {
                out.append(in[i]);
            }

            if (i + 1 != in.length) {
                out.append("\n");
            }
        }

        return out.toString();
    }

    public void sendMessage(@NotNull Player player, String messageId, @NotNull String... placeholderReplacements) {
        String message = getRawMessage(messageId);

        // Don't send empty messages
        if (message == null || message.isEmpty()) {
            return;
        }

        // Replace placeholders
        int replacementIndexer = 1;
        for (String replacement : placeholderReplacements) {
            String replacementString = "%" + replacementIndexer + "%";
            message = message.replace(replacementString, replacement);
            replacementIndexer = replacementIndexer + 1;
        }

        if (player instanceof ConsolePlayer) {
            sendMineDownToConsole(message);
            return;
        }

        player.sendMessage(new MineDown(message));
    }

    public void sendMessage(Player player, String messageId) {
        sendMessage(player, messageId, new String[]{});
    }

    public void sendCustomMessage(Player player, String message) {
        if (player instanceof ConsolePlayer) {
            sendMineDownToConsole(message);
            return;
        }
        player.sendMessage(new MineDown(message));
    }

    public void sendFormattedChannelMessage(Player target, Player sender, Channel channel, String message) {
        final TextComponent.Builder componentBuilder = Component.text()
                .append(new MineDown(Placeholders.replace(sender, channel.format, plugin)).toComponent());
        if (sender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(Component.text(message));
        }
        target.sendMessage(componentBuilder.build());
    }

    public void sendFormattedOutboundPrivateMessage(Player messageSender, ArrayList<Player> messageRecipients, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        if (messageRecipients.size() == 1) {
            componentBuilder.append(new MineDown(Placeholders.replace(messageRecipients.get(0),
                    Settings.outboundMessageFormat, plugin)).toComponent());
        } else {
            componentBuilder.append(new MineDown(Placeholders.replace(messageRecipients.get(0), Settings.groupOutboundMessageFormat, plugin)
                    .replace("%group_amount_subscript%", convertToUnicodeSubScript(messageRecipients.size() - 1))
                    .replace("%group_amount%", Integer.toString(messageRecipients.size() - 1))
                    .replace("%group_members_comma_separated%", getGroupMemberList(messageRecipients, ","))
                    .replace("%group_members%", MineDown.escape(getGroupMemberList(messageRecipients, "\n"))))
                    .toComponent());
        }
        if (messageSender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(Component.text(message));
        }

        if (messageSender instanceof ConsolePlayer) {
            plugin.getConsoleAudience().sendMessage(componentBuilder.build());
            return;
        }
        messageSender.sendMessage(componentBuilder.build());
    }

    public void sendFormattedInboundPrivateMessage(ArrayList<Player> messageRecipients, Player messageSender, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        if (messageRecipients.size() == 1) {
            componentBuilder.append(new MineDown(Placeholders.replace(messageSender, Settings.inboundMessageFormat,
                    plugin)).toComponent());
        } else {
            componentBuilder.append(new MineDown(Placeholders.replace(messageSender, Settings.groupInboundMessageFormat, plugin)
                    .replace("%group_amount_subscript%", convertToUnicodeSubScript(messageRecipients.size() - 1))
                    .replace("%group_amount%", Integer.toString(messageRecipients.size() - 1))
                    .replace("%group_members_comma_separated%", getGroupMemberList(messageRecipients, ","))
                    .replace("%group_members%", MineDown.escape(getGroupMemberList(messageRecipients, "\n"))))
                    .toComponent());
        }
        if (messageSender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(Component.text(message));
        }
        for (final Player recipient : messageRecipients) {
            if (recipient instanceof ConsolePlayer) {
                plugin.getConsoleAudience().sendMessage(componentBuilder.build());
                return;
            }
            recipient.sendMessage(componentBuilder.build());
        }
    }

    public void sendFormattedLocalSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                             Channel channel, String message) {
        final TextComponent.Builder componentBuilder = Component.text()
                .append(new MineDown(Placeholders.replace(sender, Settings.localSpyFormat, plugin)
                                             .replace("%spy_color%", spyColor.colorCode)
                                             .replace("%channel%", channel.id) +
                                     MineDown.escape(message)).toComponent());
        spy.sendMessage(componentBuilder.build());
    }

    public void sendFormattedSocialSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                              ArrayList<Player> receivers, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        if (receivers.size() == 1) {
            final Player receiver = receivers.get(0);
            componentBuilder.append(new MineDown(
                    Placeholders.replace(receiver, Placeholders.replace(sender, Settings.socialSpyFormat.replace("%sender_", "%"), plugin)
                            .replace("%receiver_", "%"), plugin).replace("%spy_color%", spyColor.colorCode) + MineDown.escape(message)).toComponent());
        } else {
            final Player firstReceiver = receivers.get(0);
            String md = Placeholders.replace(firstReceiver,
                            Placeholders.replace(sender,
                                            Settings.socialSpyGroupFormat.replace("%sender_", "%"),
                                            plugin)
                                    .replace("%receiver_", "%"), plugin)
                                .replace("%group_amount_subscript%", convertToUnicodeSubScript(receivers.size() - 1))
                                .replace("%group_amount%", Integer.toString(receivers.size() - 1))
                                .replace("%group_members_comma_separated%", getGroupMemberList(receivers, ","))
                                .replace("%group_members%", MineDown.escape(getGroupMemberList(receivers, "\n")))
                                .replace("%spy_color%", spyColor.colorCode) + MineDown.escape(message);

            componentBuilder.append(new MineDown(md).toComponent());
        }
        spy.sendMessage(componentBuilder.build());
    }

    public void sendFormattedBroadcastMessage(Player player, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        componentBuilder.append(new MineDown(Settings.broadcastMessageFormat).toComponent());
        componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        player.sendMessage(componentBuilder.build());
    }

    private void sendMineDownToConsole(@NotNull String mineDown) {
        plugin.getConsoleAudience().sendMessage(Component.text(extractMineDownLinks(mineDown)));
    }

    // Returns a newline separated list of player names
    public final String getGroupMemberList(ArrayList<Player> players, String delimiter) {
        final StringJoiner memberList = new StringJoiner(delimiter);
        for (Player player : players) {
            memberList.add(player.getName());
        }
        return memberList.toString();
    }

    // Get the corresponding subscript unicode character from a normal one
    public final String convertToUnicodeSubScript(int number) {
        final String numberString = Integer.toString(number);
        StringBuilder subScriptNumber = new StringBuilder();
        for (String digit : numberString.split("")) {
            subScriptNumber.append(switch (digit) {
                case "0" -> "₀";
                case "1" -> "₁";
                case "2" -> "₂";
                case "3" -> "₃";
                case "4" -> "₄";
                case "5" -> "₅";
                case "6" -> "₆";
                case "7" -> "₇";
                case "8" -> "₈";
                case "9" -> "₉";
                default -> "";
            });
        }
        return subScriptNumber.toString();
    }
}
