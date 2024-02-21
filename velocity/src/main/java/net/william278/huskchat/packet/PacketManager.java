package net.william278.huskchat.packet;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.velocity.factory.VelocityPacketEventsBuilder;
import net.william278.huskchat.VelocityHuskChat;
import org.jetbrains.annotations.NotNull;

public class PacketManager {

    private final VelocityHuskChat plugin;

    public PacketManager(@NotNull VelocityHuskChat plugin) {
        this.plugin = plugin;
    }

    public void load() {
        PacketEvents.setAPI(VelocityPacketEventsBuilder.build(plugin.getProxyServer(), plugin.getContainer()));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getEventManager().registerListener(new PlayerPacketListener(plugin));
    }
}
