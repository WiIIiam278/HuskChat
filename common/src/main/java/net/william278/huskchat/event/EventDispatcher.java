package net.william278.huskchat.event;

import net.william278.huskchat.player.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface EventDispatcher {
    CompletableFuture<IChatMessageEvent> dispatchChatMessageEvent(Player player, String message, String channelId);
    CompletableFuture<IPrivateMessageEvent> dispatchPrivateMessageEvent(Player sender, ArrayList<Player> receivers, String message);
    CompletableFuture<IBroadcastMessageEvent> dispatchBroadcastMessageEvent(String message);
}
