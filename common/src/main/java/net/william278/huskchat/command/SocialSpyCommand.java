package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SocialSpyCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.socialspy";

    public SocialSpyCommand(HuskChat implementor) {
        super("socialspy", PERMISSION, implementor);
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (args.length == 1) {
            PlayerCache.SpyColor color = PlayerCache.SpyColor.DEFAULT_SPY_COLOR;
            Optional<PlayerCache.SpyColor> selectedColor = PlayerCache.SpyColor.getColor(args[0]);
            if (selectedColor.isPresent()) {
                color = selectedColor.get();
            }
            PlayerCache.setSocialSpy(player, color);
            implementor.getMessageManager().sendMessage(player, "social_spy_toggled_on_color");
            return;
        }
        if (PlayerCache.isSocialSpying(player)) {
            PlayerCache.setSocialSpy(player);
            implementor.getMessageManager().sendMessage(player, "social_spy_toggled_on");
        } else {
            PlayerCache.removeSocialSpy(player);
            implementor.getMessageManager().sendMessage(player, "social_spy_toggled_off");
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

}
