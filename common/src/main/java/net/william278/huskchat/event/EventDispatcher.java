package net.william278.huskchat.event;

import net.william278.huskchat.player.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface EventDispatcher {
    CompletableFuture<IChatMessageEvent> fireChatMessageEvent(Player player, String message, String channelId);
    CompletableFuture<IPrivateMessageEvent> firePrivateMessageEvent(Player sender, ArrayList<Player> receivers, String message);
    CompletableFuture<IBroadcastMessageEvent> fireBroadcastMessageEvent(String message);
}
