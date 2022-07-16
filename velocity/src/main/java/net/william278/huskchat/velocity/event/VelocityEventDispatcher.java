package net.william278.huskchat.velocity.event;

import com.velocitypowered.api.proxy.ProxyServer;
import net.william278.huskchat.event.EventDispatcher;
import net.william278.huskchat.event.IBroadcastMessageEvent;
import net.william278.huskchat.event.IChatMessageEvent;
import net.william278.huskchat.event.IPrivateMessageEvent;
import net.william278.huskchat.player.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class VelocityEventDispatcher implements EventDispatcher {
    private final ProxyServer server;

    public VelocityEventDispatcher(ProxyServer server) {
        this.server = server;
    }

    @Override
    public CompletableFuture<IChatMessageEvent> dispatchChatMessageEvent(Player player, String message, String channelId) {
        return server.getEventManager().fire(new ChatMessageEvent(player, message, channelId));
    }

    @Override
    public CompletableFuture<IPrivateMessageEvent> dispatchPrivateMessageEvent(Player sender, ArrayList<Player> receivers, String message) {
        return server.getEventManager().fire(new PrivateMessageEvent(sender, receivers, message));
    }

    @Override
    public CompletableFuture<IBroadcastMessageEvent> dispatchBroadcastMessageEvent(Player sender, String message) {
        return server.getEventManager().fire(new BroadcastMessageEvent(sender, message));
    }
}
