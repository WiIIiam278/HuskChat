package me.william278.huskchat.commands;

import me.william278.huskchat.HuskChat;
import me.william278.huskchat.MessageManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class ReplyCommand extends Command {

    private final static String PERMISSION = "huskchat.command.msg.reply";

    public ReplyCommand() {
        super("r", PERMISSION, "reply");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            if (args.length >= 1) {
                UUID lastPlayerId = HuskChat.getLastMessenger(player.getUniqueId());
                if (lastPlayerId == null) {
                    MessageManager.sendMessage(player, "error_reply_no_messages");
                    return;
                }
                ProxiedPlayer lastPlayer = ProxyServer.getInstance().getPlayer(lastPlayerId);
                if (lastPlayer == null) {
                    MessageManager.sendMessage(player, "error_reply_not_online");
                    return;
                }
                StringBuilder message = new StringBuilder();
                for (String arg : args) {
                    message.append(arg).append(" ");
                }
                final String messageToSend = message.toString();
                final String targetPlayerName = lastPlayer.getName();
                MessageCommand.sendPrivateMessage(player, targetPlayerName, messageToSend);
            } else {
                MessageManager.sendMessage(player, "error_invalid_syntax", "/r <message>");
            }
        }
    }
}
