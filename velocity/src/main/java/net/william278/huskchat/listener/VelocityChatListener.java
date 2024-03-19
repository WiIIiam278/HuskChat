package net.william278.huskchat.listener;

import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.user.VelocityUser;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface VelocityChatListener {

    default boolean handlePlayerChat(PlayerChatEvent e) {
        final VelocityUser player = VelocityUser.adapt(e.getPlayer(), plugin());
        final Optional<Channel> channel = plugin().getChannels().getChannel(
                plugin().getUserCache().getPlayerChannel(player.getUuid())
        );
        if (channel.isEmpty()) {
            plugin().getLocales().sendMessage(player, "error_no_channel");
            return false;
        }

        // Send the chat message, determine if the event should be canceled
        return !new ChatMessage(channel.get(), player, e.getMessage(), plugin()).dispatch();
    }

    @NotNull
    HuskChat plugin();

}
