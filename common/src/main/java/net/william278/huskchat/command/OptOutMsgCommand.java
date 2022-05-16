package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OptOutMsgCommand extends CommandBase {
    private final static String PERMISSION = "huskchat.command.optoutmsg";

    public OptOutMsgCommand(HuskChat implementor) {
        super("optoutmsg", PERMISSION, implementor);
    }

    @Override
    public void onExecute(Player player, String[] args) {
        PlayerCache.getLastMessengers(player.getUuid()).ifPresentOrElse(lastMessengers -> {
            if (lastMessengers.size() <= 1) {
                implementor.getMessageManager().sendMessage(player, "error_last_message_not_group");
                return;
            }

            for (UUID uuid : lastMessengers) {
                PlayerCache.getLastMessengers(uuid).ifPresent(last -> {
                    last.remove(player.getUuid());
                });
            }

            implementor.getMessageManager().sendMessage(player, "removed_from_group_message",
                    lastMessengers.stream().flatMap(u -> implementor.getPlayer(u).stream()).map(Player::getName)
                            .collect(Collectors.joining(", ")));
            lastMessengers.clear();
        }, () -> {
            implementor.getMessageManager().sendMessage(player, "error_no_messages_opt_out");
        });
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return new ArrayList<>();
    }
}
