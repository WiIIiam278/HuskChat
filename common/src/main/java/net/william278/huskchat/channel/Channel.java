package net.william278.huskchat.channel;

import java.util.ArrayList;
import java.util.List;

public class Channel {

    /**
     * String identifier of the channel
     */
    public final String id;

    /**
     * Message format of the channel
     */
    public final String format;

    /**
     * {@link BroadcastScope} of the channel
     */
    public final BroadcastScope broadcastScope;

    /**
     * A {@link ArrayList} of shortcut commands users can execute to access the channel
     */
    public List<String> shortcutCommands = new ArrayList<>();

    /**
     * A {@link ArrayList} of servers where this channel cannot be used
     */
    public List<String> restrictedServers = new ArrayList<>();

    /**
     * Permission node required to switch to and send messages to this channel
     */
    public String sendPermission;

    /**
     * Permission node required to receive messages from this channel. Note that channels with a {@link BroadcastScope} of {@code PASSTHROUGH}, {@code LOCAL_PASSTHROUGH} or {@code GLOBAL_PASSTHROUGH} will pass messages to the backend server, which will not necessarily check for this node, meaning players without permission may receive channel messages.,
     */
    public String receivePermission;

    /**
     * Whether this channel should have its messages logged to console
     */
    public boolean logMessages;

    /**
     * todo Whether this channel should automatically censor its messages
     */
    public boolean censor;

    /**
     * Creates a channel with the specified ID and basic format
     *
     * @param id             The ID of the channel
     * @param format         The channel format
     * @param broadcastScope The {@link BroadcastScope} of this channel
     */
    public Channel(String id, String format, BroadcastScope broadcastScope) {
        this.id = id;
        this.format = format;
        this.broadcastScope = broadcastScope;
    }

    /**
     * The broadcast scope of a channel, defining how messages will be handled
     */
    public enum BroadcastScope {
        /**
         * Message is broadcast globally to those with permissions via the proxy
         */
        GLOBAL(false),

        /**
         * Message is broadcast via the proxy to players who have permission and are on the same server as the source
         */
        LOCAL(false),

        /**
         * Message is not handled by the proxy and is instead passed to the backend server
         */
        PASSTHROUGH(true),

        /**
         * Message is broadcast via the proxy to players who have permission and are on the same server as the source and is additionally passed to the backend server
         */
        LOCAL_PASSTHROUGH(true),

        /**
         * Message is broadcast globally to those with permissions via the proxy and is additionally passed to the backend server
         */
        GLOBAL_PASSTHROUGH(true);

        /**
         * Whether the broadcast should be passed to the backend server
         */
        public final boolean isPassThrough;

        BroadcastScope(boolean isPassThrough) {
            this.isPassThrough = isPassThrough;
        }
    }
}
