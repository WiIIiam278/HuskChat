package net.william278.huskchat.velocity.config;

import de.themoep.minedown.adventure.MineDown;
import de.themoep.minedown.adventure.MineDownParser;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.util.PlaceholderReplacer;
import net.william278.huskchat.velocity.HuskChatVelocity;
import net.william278.huskchat.velocity.player.VelocityPlayer;

import java.io.File;
import java.io.IOException;
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

        // Convert to baseComponents[] via MineDown formatting and send
        VelocityPlayer.adaptVelocity(player).ifPresent(user -> user.sendMessage(new MineDown(message).toComponent()));
    }

    @Override
    public void sendCustomMessage(Player player, String message) {
        VelocityPlayer.adaptVelocity(player).ifPresent(user -> user.sendMessage(new MineDown(message).toComponent()));
    }

    @Override
    public void sendFormattedChannelMessage(Player target, Player sender, Channel channel, String message) {
        final TextComponent.Builder componentBuilder = Component.text()
                .append(new MineDown(PlaceholderReplacer.replace(sender, channel.format, plugin))
                        .toComponent());
        if (sender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING)
                    .toComponent());
        } else {
            componentBuilder.append(Component.text(message));
        }
        VelocityPlayer.adaptVelocity(target).ifPresent(user -> user.sendMessage(componentBuilder.build()));
    }

    @Override
    public void sendFormattedOutboundPrivateMessage(Player messageSender, Player messageRecipient, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        componentBuilder.append(new MineDown(PlaceholderReplacer.replace(messageRecipient, Settings.outboundMessageFormat, plugin))
                .toComponent());
        if (messageSender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(Component.text(message));
        }
        VelocityPlayer.adaptVelocity(messageSender).ifPresent(user -> user.sendMessage(componentBuilder.build()));
    }

    @Override
    public void sendFormattedInboundPrivateMessage(Player messageRecipient, Player messageSender, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        componentBuilder.append(new MineDown(PlaceholderReplacer.replace(messageSender, Settings.inboundMessageFormat, plugin))
                .toComponent());
        if (messageSender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(Component.text(message));
        }
        VelocityPlayer.adaptVelocity(messageRecipient).ifPresent(user -> user.sendMessage(componentBuilder.build()));
    }

    @Override
    public void sendFormattedLocalSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                             Channel channel, String message) {
        final TextComponent.Builder componentBuilder = Component.text()
                .append(new MineDown(PlaceholderReplacer.replace(sender, Settings.localSpyFormat, plugin)
                        .replaceAll("%spy_color%", spyColor.colorCode) + MineDown.escape(message)).toComponent());
        VelocityPlayer.adaptVelocity(spy).ifPresent(user -> user.sendMessage(componentBuilder.build()));
    }

    @Override
    public void sendFormattedSocialSpyMessage(Player spy, PlayerCache.SpyColor spyColor, Player sender,
                                              Player receiver, String message) {
        final TextComponent.Builder componentBuilder = Component.text()
                .append(new MineDown(PlaceholderReplacer.replace(receiver,
                                PlaceholderReplacer.replace(sender,
                                                Settings.socialSpyFormat.replaceAll("%sender_", "%"),
                                                plugin)
                                        .replaceAll("%receiver_", "%"), plugin)
                        .replaceAll("%receiever_name%", MineDown.escape(receiver.getName()))
                        .replaceAll("%spy_color%", spyColor.colorCode) + MineDown.escape(message)).toComponent());
        VelocityPlayer.adaptVelocity(spy).ifPresent(user -> user.sendMessage(componentBuilder.build()));
    }

    @Override
    public void sendFormattedBroadcastMessage(Player player, String message) {
        final TextComponent.Builder componentBuilder = Component.text();
        componentBuilder.append(new MineDown(Settings.broadcastMessageFormat).toComponent());
        componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        VelocityPlayer.adaptVelocity(player).ifPresent(user -> user.sendMessage(componentBuilder.build()));
    }
}