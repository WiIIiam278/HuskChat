package net.william278.huskchat.placeholderparser;

import net.william278.huskchat.player.Player;

import java.util.concurrent.CompletableFuture;

public interface Placeholders {

    public CompletableFuture<String> parsePlaceholders(String stringToParse, Player player);

}
