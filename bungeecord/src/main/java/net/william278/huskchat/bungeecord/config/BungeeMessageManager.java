package net.william278.huskchat.bungeecord.config;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.bungeecord.player.BungeePlayer;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.util.PlaceholderReplacer;

public class BungeeMessageManager extends MessageManager {

    private static final HuskChatBungee plugin = HuskChatBungee.getInstance();

    public BungeeMessageManager() {
        super(new BungeeConfigFile("languages/" + Settings.language + ".yml",
                "messages-" + Settings.language + ".yml"));
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

        // Convert to baseComponents[] via MineDown formatting and send
        BungeePlayer.adaptBungee(player).sendMessage(new MineDown(message).replace().toComponent());
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

        // Convert to baseComponents[] via MineDown formatting and send
        BungeePlayer.adaptBungee(player).sendMessage(new MineDown(message).replace().toComponent());
    }

    @Override
    public void sendCustomMessage(Player player, String message) {
        BungeePlayer.adaptBungee(player).sendMessage(new MineDown(message).toComponent());
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
        BungeePlayer.adaptBungee(target).sendMessage(componentBuilder.create());
    }

    @Override
    public void sendFormattedOutboundPrivateMessage(Player recipient, Player sender, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(new MineDown(PlaceholderReplacer.replace(recipient, Settings.outboundMessageFormat, plugin))
                .toComponent());
        if (sender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING)
                    .toComponent());
        } else {
            componentBuilder.append(message);
        }
        BungeePlayer.adaptBungee(recipient).sendMessage(componentBuilder.create());
    }

    @Override
    public void sendFormattedInboundPrivateMessage(Player recipient, Player sender, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(new MineDown(PlaceholderReplacer.replace(sender, Settings.inboundMessageFormat, plugin))
                .toComponent());
        if (sender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING)
                    .toComponent());
        } else {
            componentBuilder.append(message);
        }
        BungeePlayer.adaptBungee(recipient).sendMessage(componentBuilder.create());
    }

    @Override
    public void sendFormattedLocalSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                             Channel channel, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder()
                .append(new MineDown(PlaceholderReplacer.replace(sender, Settings.localSpyFormat, plugin)
                        .replaceAll("%spy_color%", spyColor.colorCode)).toComponent())
                .append(message);
        BungeePlayer.adaptBungee(spy).sendMessage(componentBuilder.create());
    }

    @Override
    public void sendFormattedSocialSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                              Player receiver, String message) {
        final ComponentBuilder componentBuilder = new ComponentBuilder()
                .append(new MineDown(PlaceholderReplacer.replace(receiver,
                                PlaceholderReplacer.replace(sender,
                                                Settings.socialSpyFormat.replaceAll("%sender_", "%"),
                                                plugin)
                                        .replaceAll("%receiver_", "%"), plugin)
                        .replaceAll("%receiever_name%", receiver.getName())
                        .replaceAll("%spy_color%", spyColor.colorCode)).toComponent())
                .append(message);
        BungeePlayer.adaptBungee(spy).sendMessage(componentBuilder.create());
    }
}