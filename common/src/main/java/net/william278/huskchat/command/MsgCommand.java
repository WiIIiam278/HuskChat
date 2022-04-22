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
        if (player.hasPermission(permission)) {
            if (args.length >= 2) {
                StringJoiner message = new StringJoiner(" ");
                int messageWordCount = 0;
                for (String arg : args) {
                    if (messageWordCount >= 1) {
                        message.add(arg);
                    }
                    messageWordCount++;
                }
                final List<String> targetPlayers = getTargetPlayers(args[0]);
                final String messageToSend = message.toString();
                new PrivateMessage(player, targetPlayers, messageToSend, implementor).dispatch();
            } else {
                implementor.getMessageManager().sendMessage(player, "error_invalid_syntax", "/msg <player> <message>");
            }
        } else {
            implementor.getMessageManager().sendMessage(player, "error_no_permission");
        }
    }

    // Parses a string-separated list of target players
    private List<String> getTargetPlayers(String playerList) {
        if (!playerList.contains(",")) {
            return Collections.singletonList(playerList);
        }
        return List.of(playerList.split(","));
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        if (!player.hasPermission(PERMISSION)) {
            return Collections.emptyList();
        }
        if (args.length <= 1) {
            final ArrayList<String> userNames = new ArrayList<>();
            for (Player connectedPlayer : implementor.getOnlinePlayers()) {
                if (!player.getUuid().equals(connectedPlayer.getUuid())) {
                    userNames.add(connectedPlayer.getName());
                }
            }
            String currentText = (args.length >= 1) ? args[0] : "";
            String precursoryText = "";
            if (currentText.contains(",")) {
                currentText = currentText.split(",")[currentText.split(",").length-1];
                precursoryText = !args[0].replace(currentText, "").startsWith(",") ? args[0].replace(currentText, "") : "";
            }

            final String completionFilter = currentText;
            final ArrayList<String> prependedUsernames = new ArrayList<>();
            for (String username : userNames.stream().filter(val -> val.toLowerCase().startsWith(completionFilter.toLowerCase())).sorted().toList()) {
                prependedUsernames.add(precursoryText + username);
            }
            return prependedUsernames;
        } else {
            return Collections.emptyList();
        }
    }

}
