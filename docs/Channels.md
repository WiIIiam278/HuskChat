Channels are what players talk in and can be switched between using the /channel command or specialized channel shortcut
command. By default, HuskChat has the following channels setup, perfect for a typical setup:

* `local` - Local-scoped channel with `/local`, `/l` shortcut commands, for sending messages to players on the same server.
* `global` (default channel) - Global-scoped channel with `/global`, `/g` shortcut commands, for sending messages across the network.
* `staff` - Global-scoped channel with `/staff`, `/sc` shortcut commands. Great for letting staff communicate easily. Players need the `huskchat.channel.staff.send` and `huskchat.channel.staff.receive` permissions to send and receive messages in this channel respectively.
* `helpop` - Global-scoped channel with `/helpop` shortcut command. Great for letting players easily contact staff. Players need the `huskchat.channel.helpop.receive` permission to receive messages in this channel.

### Channel definition

To define custom channels, put them under the `channels:` section of `config.yml`. Below is the specification for how
this should be laid out, using a typical `staff` channel as an example. Keys marked as (Required) should be present.
Fields not marked as required will be set to a default value.

```yaml
channels:
  #...
  staff: # (Required) The ID of the channel. To switch to this channel, users would execute /channel staff
    format: '&e[Staff] %name%: &7' # (Required) Display format of the channel - See below
    broadcast_scope: GLOBAL # Broadcast scope of the channel - See below
    log_to_console: true # Whether messages sent to this channel should be logged to the proxy console
    filtered: false # Whether messages sent to this channel should be filtered and replaced (see below)
    permissions:
      send: 'huskchat.channel.staff.send' # Permission required to see channel messages
      receive: 'huskchat.channel.staff.receive' # Permission required to switch to & send messages
    shortcut_commands: # List of shortcut commands that users can use to quickly use the channel
      - /staff
      - /sc
    restricted_servers: # List of servers where messages in this channel can't be sent or received
      - hub
  #...
```

### Channel scope

Channel scope defines the scope by which messages are broadcast and handled by HuskChat. The following options are
available:

#### Proxy scopes
These scopes are available when running HuskChat on a proxy server (Velocity or BungeeCord/Waterfall)

* `GLOBAL` - Message is broadcast globally to those with permissions via the proxy
* `LOCAL` - Message is broadcast via the proxy to players who have permission and are on the same server as the source
* `PASSTHROUGH` - Message is not handled by the proxy and is instead passed to the backend server
* `GLOBAL_PASSTHROUGH` - Message is broadcast globally to those with permissions via the proxy and is additionally passed to the backend server
* `LOCAL_PASSTHROUGH` - Message is broadcast via the proxy to players who have permission and are on the same server as
  the source and is additionally passed to the backend server

#### Single-server scopes
These scopes are available when running HuskChat on a single-server Spigot server

* `GLOBAL` - Message is broadcast to everyone on the server
* `PASSTHROUGH` - Message is not handled by HuskChat; chat will be delegated to other/the vanilla chat handlers
* `GLOBAL_PASSTHROUGH` - Message is broadcast to everyone on the server and is additionally passed to other/the vanilla chat handlers (the event is not cancelled)

On a single server setup, the `LOCAL` and `LOCAL_PASSTHROUGH` scopes duplicate the `GLOBAL` and `GLOBAL_PASSTHROUGH` scopes.

### Default channels

You must define a `default_channel` in config.yml that players will be put in when they join.

Additionally, you can define server specific defaults in the `server_default_channels` section. When a player changes to
a server with a server_default_channel assigned, the player will automatically switch to the specified channel

```yaml
server_default_channels:
  uhc: minigames
  bedwars: minigames
```

On a single-server setup, this is ignored.

### Restricted channels

If you'd like to prevent players from using certain channels in certain servers, you can define `restricted_servers` in
each channel (see the channel definition above for an example). Players are unable to send or receive any messages in a
channel if they are connected to a server where it is restricted.

Additionally, if a player changes server to one where their current channel is restricted, it will change to
the `default_channel` unless it has an overriding server default channel as outlined above.

You can also restrict use of the `/msg` and `/r` commands in certain servers through the `restricted_servers` section
under `message_command`.

On a single-server setup, this is ignored.
