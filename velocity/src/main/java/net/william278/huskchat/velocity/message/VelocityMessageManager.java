package net.william278.huskchat.velocity.message;

import de.themoep.minedown.adventure.MineDown;
import de.themoep.minedown.adventure.MineDownParser;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.util.PlaceholderReplacer;
import net.william278.huskchat.velocity.HuskChatVelocity;
import net.william278.huskchat.velocity.player.VelocityPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class VelocityMessageManager extends MessageManager {

    private static final HuskChatVelocity plugin = HuskChatVelocity.getInstance();

    public VelocityMessageManager() throws IOException {
        super(YamlDocument.create(new File(plugin.getDataFolder(), "messages-" + Settings.language + ".yml"),
                Objects.requireNonNull(HuskChat.class.getClassLoader().getResourceAsStream("languages/" + Settings.language + ".yml"))));
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
        VelocityPlayer.adaptVelocity(player).ifPresent(user -> user.sendMessage(new MineDown(finalMessage).toComponent()));
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
        VelocityPlayer.adaptVelocity(player).ifPresent(user -> user.sendMessage(new MineDown(message).toComponent()));
    }

    @Override
    public void sendCustomMessage(Player player, String message) {
        if (player instanceof ConsolePlayer) {
            sendMineDownToConsole(message);
            return;
        }
        VelocityPlayer.adaptVelocity(player).ifPresent(user -> user.sendMessage(new MineDown(message).toComponent()));
    }

    @Override
    public void sendFormattedChannelMessage(Player target, Player sender, Channel channel, String message) {
        final TextComponent.Builder componentBuilder = Component.text();

        if (sender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(PlaceholderReplacer.replace(sender, channel.format, plugin) + message)
                    .toComponent());
        } else {
            componentBuilder.append(new MineDown(PlaceholderReplacer.replace(sender, channel.format, plugin) + MineDown.escape(message)).toComponent());
        }
        VelocityPlayer.adaptVelocity(target).ifPresent(user -> user.sendMessage(componentBuilder.build()));
    }

    @Override
    public void sendFormattedOutboundPrivateMessage(Player messageSender, ArrayList<Player> messageRecipients, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        if (messageRecipients.size() == 1) {
            componentBuilder.append(new MineDown(PlaceholderReplacer.replace(messageRecipients.get(0), Settings.outboundMessageFormat, plugin))
                    .toComponent());
        } else {
            componentBuilder.append(new MineDown(
                    PlaceholderReplacer.replace(messageRecipients.get(0), Settings.groupOutboundMessageFormat, plugin)
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
            plugin.getProxyServer().getConsoleCommandSource().sendMessage(componentBuilder);
            return;
        }
        VelocityPlayer.adaptVelocity(messageSender).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(componentBuilder));
    }

    @Override
    public void sendFormattedInboundPrivateMessage(ArrayList<Player> messageRecipients, Player messageSender, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        if (messageRecipients.size() == 1) {
            componentBuilder.append(new MineDown(PlaceholderReplacer.replace(messageSender, Settings.inboundMessageFormat, plugin))
                    .toComponent());
        } else {
            componentBuilder.append(new MineDown(
                    PlaceholderReplacer.replace(messageSender, Settings.groupInboundMessageFormat, plugin)
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
        for (Player recipient : messageRecipients) {
            if (recipient instanceof ConsolePlayer) {
                plugin.getProxyServer().getConsoleCommandSource().sendMessage(componentBuilder);
                continue;
            }
            VelocityPlayer.adaptVelocity(recipient).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(componentBuilder));
        }
    }

    @Override
    public void sendFormattedLocalSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                             Channel channel, String message) {
        final TextComponent.Builder componentBuilder = Component.text()
                .append(new MineDown(PlaceholderReplacer.replace(sender, Settings.localSpyFormat, plugin)
                        .replace("%spy_color%", spyColor.colorCode)
                        .replace("%channel%", channel.id) +
                        MineDown.escape(message)).toComponent());
        VelocityPlayer.adaptVelocity(spy).ifPresent(user -> user.sendMessage(componentBuilder.build()));
    }

    @Override
    public void sendFormattedSocialSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                              ArrayList<Player> receivers, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        if (receivers.size() == 1) {
            final Player receiver = receivers.get(0);
            componentBuilder.append(new MineDown(PlaceholderReplacer.replace(receiver,
                            PlaceholderReplacer.replace(sender,
                                            Settings.socialSpyFormat.replace("%sender_", "%"),
                                            plugin)
                                    .replace("%receiver_", "%"), plugin)
                    .replace("%spy_color%", spyColor.colorCode) + MineDown.escape(message)).toComponent());
        } else {
            final Player firstReceiver = receivers.get(0);
            String md = PlaceholderReplacer.replace(firstReceiver,
                            PlaceholderReplacer.replace(sender,
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
        VelocityPlayer.adaptVelocity(spy).ifPresent(bungeePlayer -> bungeePlayer.sendMessage(componentBuilder));
    }

    @Override
    public void sendFormattedBroadcastMessage(Player player, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        componentBuilder.append(new MineDown(Settings.broadcastMessageFormat).toComponent());
        componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        VelocityPlayer.adaptVelocity(player).ifPresent(user -> user.sendMessage(componentBuilder.build()));
    }

    private void sendMineDownToConsole(String mineDown) {
        plugin.getProxyServer().getConsoleCommandSource().sendMessage(new MineDown(extractMineDownLinks(mineDown)).toComponent());
    }
}