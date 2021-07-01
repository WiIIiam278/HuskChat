package me.william278.huskchat.commands;

import me.william278.huskchat.HuskChat;
import me.william278.huskchat.MessageManager;
import me.william278.huskchat.censor.CensorUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class MessageCommand extends Command implements TabExecutor {

    private final static String PERMISSION = "huskchat.command.msg";

    public MessageCommand() {
        super("msg", PERMISSION, "m", "message", "w", "whisper", "t", "tell", "pm");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            if (args.length >= 2) {
                StringBuilder message = new StringBuilder();
                int i = 0;
                for (String arg : args) {
                    if (i >= 1) {
                        message.append(arg).append(" ");
                    }
                    i++;
                }
                final String targetPlayerUsername = args[0];
                final String messageToSend = message.toString();
                sendPrivateMessage(player, targetPlayerUsername, messageToSend);
            } else {
                MessageManager.sendMessage(player, "error_invalid_syntax", "/msg <player> <message>");
            }
        }
    }

    // Send a private message
    public static void sendPrivateMessage(ProxiedPlayer sender, String targetUsername, String message) {
        if (targetUsername.equalsIgnoreCase(sender.getName())) {
            MessageManager.sendMessage(sender, "error_cannot_message_self");
            return;
        }
        String messageToSend;
        if (HuskChat.getConfig().isCensorPrivateMessages()) {
            messageToSend = CensorUtil.censor(message);
        } else {
            messageToSend = message;
        }
        for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
            if (target.getName().equalsIgnoreCase(targetUsername)) {
                // Show that the message has been sent
                HuskChat.setLastMessenger(sender.getUniqueId(), target.getUniqueId());
                sender.sendMessage(HuskChat.getConfig().getFormattedOutboundPrivateMessage(target, sender, messageToSend));

                // Show the received message
                HuskChat.setLastMessenger(target.getUniqueId(), sender.getUniqueId());
                target.sendMessage(HuskChat.getConfig().getFormattedInboundPrivateMessage(sender, messageToSend));

                // Log to console if enabled
                if (HuskChat.getConfig().isLogMessagesToConsole()) {
                    ProxyServer.getInstance().getLogger().info("[MSG] " + sender.getName() + " → " + target.getName() + ": " + messageToSend);
                }
                return;
            }
        }
        MessageManager.sendMessage(sender, "error_player_not_found");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            if (!player.hasPermission(PERMISSION)) {
                return Collections.emptyList();
            }
            if (args.length == 1) {
                final ArrayList<String> userNames = new ArrayList<>();
                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if (!proxiedPlayer.getName().equalsIgnoreCase(player.getName())) {
                        userNames.add(proxiedPlayer.getName());
                    }
                }
                return userNames.stream().filter(val -> val.startsWith(args[0]))
                        .sorted().collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }
}
