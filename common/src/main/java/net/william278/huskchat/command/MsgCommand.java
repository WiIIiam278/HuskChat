package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.PrivateMessage;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MsgCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.msg";

    public MsgCommand(HuskChat implementor) {
        super(Settings.messageCommandAliases.get(0), PERMISSION, implementor, Settings.getAliases(Settings.messageCommandAliases));
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (player instanceof ConsolePlayer) {
            implementor.getLoggingAdapter().log(Level.INFO, implementor.getMessageManager().getRawMessage("error_in_game_only"));
            return;
        }
        if (args.length >= 2) {
            StringJoiner message = new StringJoiner(" ");
            int messageWordCount = 0;
            for (String arg : args) {
                if (messageWordCount >= 1) {
                    message.add(arg);
                }
                messageWordCount++;
            }
            final String targetPlayerUsername = args[0];
            final String messageToSend = message.toString();
            new PrivateMessage(player, targetPlayerUsername, messageToSend, implementor).dispatch();
        } else {
            implementor.getMessageManager().sendMessage(player, "error_invalid_syntax", "/msg <player> <message>");
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        if (!player.hasPermission(PERMISSION)) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            final ArrayList<String> userNames = new ArrayList<>();
            for (Player connectedPlayer : implementor.getOnlinePlayers()) {
                if (!player.getUuid().equals(connectedPlayer.getUuid())) {
                    userNames.add(connectedPlayer.getName());
                }
            }
            return userNames.stream().filter(val -> val.startsWith(args[0]))
                    .sorted().collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
