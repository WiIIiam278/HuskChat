package net.william278.huskchat.bungeecord.message;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.bungeecord.player.BungeePlayer;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.util.PlaceholderReplacer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BungeeMessageManager extends MessageManager {

    private static final HuskChatBungee plugin = HuskChatBungee.getInstance();

    public BungeeMessageManager() throws IOException {
        super(YamlDocument.create(new File(plugin.getDataFolder(), "messages-" + Settings.language + ".yml"),
                plugin.getResourceAsStream("languages/" + Settings.language + ".yml")));
    }

    @Override
    public void sendMessage(Player player, String messageId, String... placeholderReplacements) {
        String message = getRawMessage(messageId);

        // Don't send empty messages
        if (message == null) {
            return;
        }
        if (message.isEmpty()) {
            return;
        }

        int replacementIndexer = 1;

        // Replace placeholders
        for (String replacement : placeholderReplacements) {
            String replacementString = "%" + replacementIndexer + "%";
            message = message.replace(replacementString, replacement);
            replacementIndexer = replacementIndexer + 1;
        }

        if (player instanceof ConsolePlayer) {
            sendMineDownToConsole(message);
            return;
        }

        // Convert to baseComponents[] via MineDown formatting and send
        String finalMessage = message;
        BungeePlayer.adaptBungee(player).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(new MineDown(finalMessage).replace().toComponent()));
    }

    @Override
    public void sendMessage(Player player, String messageId) {
        final String message = getRawMessage(messageId);

        // Don't send empty messages
        if (message == null) {
            return;
        }
        if (message.isEmpty()) {
            return;
        }

        if (player instanceof ConsolePlayer) {
            sendMineDownToConsole(message);
            return;
        }

        // Convert to baseComponents[] via MineDown formatting and send
        BungeePlayer.adaptBungee(player).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(new MineDown(message).replace().toComponent()));
    }

    @Override
    public void sendCustomMessage(Player player, String message) {
        if (player instanceof ConsolePlayer) {
            sendMineDownToConsole(message);
            return;
        }

        BungeePlayer.adaptBungee(player).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(new MineDown(message).replace().toComponent()));
    }

    @Override
    public void sendFormattedChannelMessage(Player target, Player sender, Channel channel, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(new MineDown(PlaceholderReplacer.replace(sender, channel.format, plugin))
                .toComponent());
        if (sender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING)
                    .toComponent());
        } else {
            componentBuilder.append(message);
        }
        BungeePlayer.adaptBungee(target).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(componentBuilder.create()));
    }

    @Override
    public void sendFormattedOutboundPrivateMessage(Player messageSender, ArrayList<Player> messageRecipients, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        if (messageRecipients.size() == 1) {
            componentBuilder.append(new MineDown(PlaceholderReplacer.replace(messageRecipients.get(0), Settings.outboundMessageFormat, plugin))
                    .toComponent());
        } else {
            componentBuilder.append(new MineDown(
                    PlaceholderReplacer.replace(messageRecipients.get(0), Settings.groupOutboundMessageFormat, plugin)
                            .replaceAll("%group_amount_subscript%", convertToUnicodeSubScript(messageRecipients.size() - 1))
                            .replaceAll("%group_amount%", Integer.toString(messageRecipients.size() - 1))
                            .replaceAll("%group_members_comma_separated%", getGroupMemberList(messageRecipients, ","))
                            .replaceAll("%group_members%", getGroupMemberList(messageRecipients, "\n")))
                    .toComponent());
        }
        if (messageSender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(message);
        }

        if (messageSender instanceof ConsolePlayer) {
            plugin.getProxy().getConsole().sendMessage(componentBuilder.create());
            return;
        }
        BungeePlayer.adaptBungee(messageSender).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(componentBuilder.create()));
    }

    @Override
    public void sendFormattedInboundPrivateMessage(ArrayList<Player> messageRecipients, Player messageSender, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        if (messageRecipients.size() == 1) {
            componentBuilder.append(new MineDown(PlaceholderReplacer.replace(messageSender, Settings.inboundMessageFormat, plugin))
                    .toComponent());
        } else {
            componentBuilder.append(new MineDown(
                    PlaceholderReplacer.replace(messageSender, Settings.groupInboundMessageFormat, plugin)
                            .replaceAll("%group_amount_subscript%", convertToUnicodeSubScript(messageRecipients.size() - 1))
                            .replaceAll("%group_amount%", Integer.toString(messageRecipients.size() - 1))
                            .replaceAll("%group_members_comma_separated%", getGroupMemberList(messageRecipients, ","))
                            .replaceAll("%group_members%", getGroupMemberList(messageRecipients, "\n")))
                    .toComponent());
        }
        if (messageSender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(message);
        }
        for (Player recipient : messageRecipients) {
            if (recipient instanceof ConsolePlayer) {
                plugin.getProxy().getConsole().sendMessage(componentBuilder.create());
                continue;
            }

            BungeePlayer.adaptBungee(recipient).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(componentBuilder.create()));
        }
    }

    @Override
    public void sendFormattedLocalSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                             Channel channel, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder()
                .append(new MineDown(PlaceholderReplacer.replace(sender, Settings.localSpyFormat, plugin)
                        .replaceAll("%spy_color%", spyColor.colorCode)).toComponent())
                .append(message);
        BungeePlayer.adaptBungee(spy).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(componentBuilder.create()));
    }

    @Override
    public void sendFormattedSocialSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                              ArrayList<Player> receivers, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        if (receivers.size() == 1) {
            final Player receiver = receivers.get(0);
            componentBuilder.append(new MineDown(PlaceholderReplacer.replace(receiver,
                                    PlaceholderReplacer.replace(sender,
                                                    Settings.socialSpyFormat.replaceAll("%sender_", "%"),
                                                    plugin)
                                            .replaceAll("%receiver_", "%"), plugin)
                            .replaceAll("%spy_color%", spyColor.colorCode)).toComponent())
                    .append(message);
        } else {
            final Player firstReceiver = receivers.get(0);
            componentBuilder.append(new MineDown(PlaceholderReplacer.replace(firstReceiver,
                                    PlaceholderReplacer.replace(sender,
                                                    Settings.socialSpyGroupFormat.replaceAll("%sender_", "%"),
                                                    plugin)
                                            .replaceAll("%receiver_", "%"), plugin)
                            .replaceAll("%group_amount_subscript%", convertToUnicodeSubScript(receivers.size() - 1))
                            .replaceAll("%group_amount%", Integer.toString(receivers.size() - 1))
                            .replaceAll("%group_members%", getGroupMemberList(receivers, "\n"))
                            .replaceAll("%group_members_comma_separated%", getGroupMemberList(receivers, ","))
                            .replaceAll("%spy_color%", spyColor.colorCode)).toComponent())
                    .append(message);
        }
        BungeePlayer.adaptBungee(spy).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(componentBuilder.create()));
    }

    @Override
    public void sendFormattedBroadcastMessage(Player player, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(new MineDown(Settings.broadcastMessageFormat).toComponent());
        componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        BungeePlayer.adaptBungee(player).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(componentBuilder.create()));
    }

    private void sendMineDownToConsole(String mineDown) {
        plugin.getProxy().getConsole().sendMessage(new MineDown(extractMineDownLinks(mineDown)).toComponent());
    }
}