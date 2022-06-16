package net.william278.huskchat.bungeecord.event;

import net.md_5.bungee.api.ProxyServer;
import net.william278.huskchat.event.EventDispatcher;
import net.william278.huskchat.event.IBroadcastMessageEvent;
import net.william278.huskchat.event.IChatMessageEvent;
import net.william278.huskchat.event.IPrivateMessageEvent;
import net.william278.huskchat.player.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class EventDispatcherBungee implements EventDispatcher {
    private ProxyServer server;

    public EventDispatcherBungee(ProxyServer server) {
        this.server = server;
    }

    // In order to keep compatibility with the Velocity implementation, the Bungee events also return CompletableFuture
    @Override
    public CompletableFuture<IChatMessageEvent> fireChatMessageEvent(Player sender, String message, String channelId) {
        CompletableFuture<IChatMessageEvent> completableFuture = new CompletableFuture<>();
        completableFuture.complete(server.getPluginManager().callEvent(new ChatMessageEvent(sender, message, channelId)));
        return completableFuture;
    }

    @Override
    public CompletableFuture<IPrivateMessageEvent> firePrivateMessageEvent(Player sender, ArrayList<Player> receivers, String message) {
        CompletableFuture<IPrivateMessageEvent> completableFuture = new CompletableFuture<>();
        completableFuture.complete(server.getPluginManager().callEvent(new PrivateMessageEvent(sender, receivers, message)));
        return completableFuture;
    }

    @Override
    public CompletableFuture<IBroadcastMessageEvent> fireBroadcastMessageEvent(String message) {
        CompletableFuture<IBroadcastMessageEvent> completableFuture = new CompletableFuture<>();
        completableFuture.complete(server.getPluginManager().callEvent(new BroadcastMessageEvent(message)));
        return completableFuture;
    }
}
