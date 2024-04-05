package net.william278.huskchat;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PaperHuskChat extends BukkitHuskChat {

    @NotNull
    @Override
    public Audience getAudience(@NotNull UUID user) {
        final Player player = getServer().getPlayer(user);
        return player == null || !player.isOnline() ? Audience.empty() : player;
    }

    @Override
    @NotNull
    public Audience getConsole() {
        return getServer().getConsoleSender();
    }

}
