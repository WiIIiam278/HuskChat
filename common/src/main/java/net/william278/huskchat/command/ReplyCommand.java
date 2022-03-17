package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.PrivateMessage;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.logging.Level;

public class ReplyCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.msg.reply";

    public ReplyCommand(HuskChat implementor) {
        super(Settings.messageCommandAliases.get(0), PERMISSION, implementor, Settings.getAliases(Settings.messageCommandAliases));
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (player instanceof ConsolePlayer) {
            implementor.getLoggingAdapter().log(Level.INFO, implementor.getMessageManager().getRawMessage("error_in_game_only"));
            return;
        }
        if (args.length >= 1) {
            final UUID lastPlayerId = PlayerCache.getLastMessenger(player.getUuid());
            if (lastPlayerId == null) {
                implementor.getMessageManager().sendMessage(player, "error_reply_no_messages");
                return;
            }

            final Player lastPlayer = implementor.getPlayer(lastPlayerId);
            if (lastPlayer == null) {
                implementor.getMessageManager().sendMessage(player, "error_reply_not_online");
                return;
            }

            StringJoiner message = new StringJoiner(" ");
            for (String arg : args) {
                message.add(arg);
            }

            final String messageToSend = message.toString();
            final String targetPlayerUsername = lastPlayer.getName();
            new PrivateMessage(player, targetPlayerUsername, messageToSend, implementor).dispatch();
        } else {
            implementor.getMessageManager().sendMessage(player, "error_invalid_syntax", "/r <message>");
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

}