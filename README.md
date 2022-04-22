[![HuskChat Banner](images/banner-graphic.png)](https://github.com/WiIIiam278/HuskChat)

# HuskChat

[![Gradle CI](https://github.com/WiIIiam278/HuskChat/actions/workflows/tests.yml/badge.svg)](https://github.com/WiIIiam278/HuskChat/actions/workflows/tests.yml)
[![Discord](https://img.shields.io/discord/818135932103557162?color=7289da&logo=discord)](https://discord.gg/tVYhJfyDWG)

**HuskChat** is a no-frills, simple and customisable cross-server chat system for Minecraft networks running BungeeCord
and Velocity frontend servers.

HuskChat is easy to configure with an elegant out-of-box setup, while also being highly configurable, suiting a variety
of use cases by allowing you to define channels and manage who can send and receive messages within them.

## Features

* Works great out of the box, install on your Velocity or BungeeCord-based proxy and use right away
* Hooks with LuckPerms to display user prefixes and suffixes
* Private messaging and replying commands
* Define who can send and receive messages in channels
* Define shortcut commands to let players quickly switch channels
* Machine learning powered profanity filter
* Customisable spam limiting filter, anti-advertising & special emoji
* Utilise modern 1.16+ formatting, with RGB and Gradient support via [MineDown](https://github.com/Phoenix616/MineDown)

## Setup

1. Download `HuskChat.jar`
2. Drag it into the `/plugins/` folder on your proxy server (BungeeCord or Velocity; 1.16+; Java 16+. Derivatives like
   Waterfall should work fine too)
3. Restart your proxy. Once the config and message files have generated, make changes as appropriate and restart your
   proxy again

## Commands

| Command           | Usage                             | Aliases                                            | Description                                              | Permission                                                  |
|-------------------|-----------------------------------|----------------------------------------------------|----------------------------------------------------------|-------------------------------------------------------------|
| `/channel`        | `/channel <channel_id> [message]` | `/c`                                               | Send a message or switch to a chat channel               | `huskchat.command.channel`                                  |
| `/huskchat`       | `/huskchat <about/reload>`        | N/A                                                | View plugin information and reload                       | `huskchat.command.huskchat`                                 |
| `/msg`            | `/msg <player> <message>`         | `/m`, `/tell`, `/w`, `/whisper`, `/message`, `/pm` | Send a private message to a player                       | `huskchat.command.msg`                                      |
| `/reply`          | `/reply <message>`                | `/r`                                               | Quickly reply to a private message                       | `huskchat.command.msg.reply`                                |
| `/socialspy`      | `/socialspy`                      | `/ss`                                              | Lets you view other users' private messages              | `huskchat.command.socialspy`                                |
| `/localspy`       | `/localspy`                       | `/ls`                                              | Lets you view messages sent in other local chat channels | `huskchat.command.localspy`                                 |
| `/broadcast`      | `/broadcast`                      | `/alert`                                           | Lets you send a broadcast across the server              | `huskchat.command.broadcast`                                |
| Shortcut commands | `/<shortcut_command> <message>`   | N/A                                                | Quickly send a message in or switch to a chat channel    | Channel send permission, e.g. `huskchat.channel.staff.send` |

## Channels & Out-of-box experience

Channels are what players talk in and can be switched between using the /channel command or specialised channel shortcut
command. By default, HuskSync has the following channels setup, perfect for a typical proxy server setup:

* `local` - Local scoped channel with `/local`, `/l` shortcut commands, for sending messages to players on the same
  server.
* `global` (default channel) - Global scoped channel with `/global`, `/g` shortcut commands, for sending messages across
  the network.
* `staff` - Global scoped channel with `/staff`, `/sc` shortcut commands. Great for letting staff communicate easily.
  Players need the `huskchat.channel.staff.send`
  and `huskchat.channel.staff.receive` permissions to send and receive messages in this channel respectively.
* `helpop` - Global scoped channel with `/helpop` shortcut command. Great for letting players easily contact staff.
  Players need the `huskchat.channel.helpop.receive`
  permission to receive messages in this channel.

### Channel definition

To define custom channels, put them under the `channels:` section of `config.yml`. Below is the specification for how
this should be laid out, using a typical `staff` channel as an example. Keys marked as (Required) should be present.
Fields not marked as required will be set to a default value.

```yaml
channels:
  ...
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
  ...
```

### Channel scope

Channel scope defines the scope by which messages are broadcast and handled by HuskChat. The following options are
available:

* `GLOBAL` - Message is broadcast globally to those with permissions via the proxy
* `LOCAL` - Message is broadcast via the proxy to players who have permission and are on the same server as the source
* `PASSTHROUGH` - Message is not handled by the proxy and is instead passed to the backend server
* `GLOBAL_PASSTHROUGH` - Message is broadcast via the proxy to players who have permission and are on the same server as
  the source and is additionally passed to the backend server
* `LOCAL_PASSTHROUGH` - Message is broadcast globally to those with permissions via the proxy and is additionally passed
  to the backend server

### Default channels

You must define a `default_channel` in config.yml that players will be put in when they join.

Additionally, you can define server specific defaults in the `server_default_channels` section. When a player changes to
a server with a server_default_channel assigned, the player will automatically switch to the specified channel

```yaml
server_default_channels:
  uhc: minigames
  bedwars: minigames
```

### Restricted channels

If you'd like to prevent players from using certain channels in certain servers, you can define `restricted_servers` in
each channel (see the channel definition above for an example). Players are unable to send or receive any messages in a
channel if they are connected to a server where it is restricted.

Additionally, if a player changes server to one where their current channel is restricted, it will change to
the `default_channel` unless it has an overriding server default channel as outlined above.

You can also restrict use of the `/msg` and `/r` commands in certain servers through the `restricted_servers` section
under `message_command`.

### Channel format & placeholders

The channel format defines how chat messages should be formatted. The message content itself is always appended after
the formatting. Note that uncleared formatting will persist and apply to the message contents. You can make use
of [MineDown formatting](https://github.com/Phoenix616/MineDown) to use modern (1.16+) hexadecimal colors and easily
make use of advanced effects such as gradients. You can embed this formatting within the prefix and suffix contents of
LuckPerms groups as well. Within the channel formatting you can make use of the following placeholders to format text.

#### Regular placeholders

* `%name%` - Username
* `%fullname%` - LuckPerms prefix, username & LuckPerms suffix
* `%prefix%` - LuckPerms prefix
* `%suffix%` - LuckPerms suffix
* `%ping%` - User's ping
* `%uuid%` - User's uuid
* `%servername%` - Server the user is on
* `%serverplayercount%` - Number of players on the server the user is on

#### Time placeholders

These display the current time

* `%timestamp%` - yyyy/MM/dd HH:mm:ss
* `%time%` - HH:mm:ss
* `%short_time%` - HH:mm
* `%date%` - yyyy/MM/dd
* `%british_date%` - dd/MM/yyyy
* `%day%` - dd
* `%month%` - MM
* `%year%` - yyyy

### Channel Filters & Replacers

The `filtered:` property of a channel lets you specify whether a message sent to a channel should be filtered first by
the enabled filters and message replacers defined in the `chat_filters` and `message_replacers` section of
the `config.yml` file. To use a filter, ensure the channel you want to be filtered has `filtered` enabled and that the
chat filters are correctly enabled and configured.

Chat filters will prevent a user from sending a message based on certain conditions. Message replacers will alter the
contents of the message, such as by replacing certain character combinations with emoji.

#### Filters

* `advertising_filter` - Prevents players from sending messages that contain IP or web addresses.
* `caps_filter` - Prevents players from sending messages that are comprised of over a certain specifiable percentage (as
  a decimal number, 0.0 to 1.0 representing 0% to 100%)
* `spam_filter` - Prevents players from sending messages too fast in chat (i.e. rate limits them). Specify how many
  messages players should be able to send in a period.
* `profanity_filter` - Uses a profanity-check machine learning algorithm to determine if a message contains English
  profanity. See below for more information on how to set this up as it requires a bit more work.
* `repeat_filter` - Prevents players from sending repeat messages. Checks against a specifiable number of the players
  previous messages.
* `ascii_filter` - Prevents players from using non-ASCII (i.e. Unicode/UTF-8) characters in chat. If your server is
  international, you probably want to turn this off.

#### Replacers

* `emoji_replacer` - Replaces certain character strings with the correct Unicode emoji. Note that if you have
  the `ascii_filter` enabled, this will still work and display unicode emoji characters in chat.

#### Profanity Check filter

The profanity check filter uses a Python machine learning algorithm (alt-profanity-check) that uses Scikit-learn to
predict whether messages contain profanity. It's imperfect and unable to catch elongated or modified slurs, but it's
quite effective (and let's face it, if people are going to be bad actors and use bad language, they'll find a way around
any swear filter).

If you're on a **shared host**, unfortunately you probably won't be able to use this feature unless your host is utterly
amazing and doesn't mind helping you with this. Note that due to the complexities associated with doing this feature, I
consider this feature for **advanced users only**, and I can't provide support for setting it up beyond directing you
here.

The profanity checker is only trained on English words. To use it, you'll need to install Python 3.8+ and Jep onto your
server and ensure that the Jep driver is correctly present in your Java classpath for your system. You can install Jep
with `pip install jep`. In addition to Jep, you will also need to run `pip install alt-profanity-check` to install the
profanity checker and prerequisites.

You will then need to make sure HuskChat recognises your Jep driver by specifying the path to it if your system doesn't
do so automatically. The name of the Jep driver varies based on platform. It's `libjep.so` on Linux, `libjep.jnilib` on
macOS, and `jep.dll` on Windows.

You can do this in one of a few ways:

* Adding Jep's library path to your Java library environment variable
    * On Linux, this is `LD_LIBRARY_PATH`.
    * On macOS, this is `DYLD_LIBRARY_PATH`.
    * On Windows, this is `PATH`.
* Adding Jep's driver to your startup command by adding the `-Djava.library.path=<path>` argument
* Adding Jep's driver path to the `library_path` config option provided by HuskChat.

Your path should point to the folder containing the jep driver, not the driver itself. If you get an error when starting
the profanity filter, you can try
[troubleshooting through Jep's guide](https://github.com/ninia/jep/wiki/FAQ#how-do-i-fix-unsatisfied-link-error-no-jep-in-javalibrarypath)
.

Once you've set up the prerequisites and the server is starting the profanity checker without issue, you can change the
settings. By default, the checker will use `AUTOMATIC` mode to determine if the message contains profanity, but if you'd
like to fine tune how sensitive the checker is, you can set `mode` to `TOLERANCE` and change the `tolerance` value
below. Lower values mean the checker will be more strict.

### Social and Local Spy

HuskChat provides `/socialspy` and `/localspy` commands. Players with permission can toggle `socialspy` to see the
private messages of users. The `/localspy` command allows players with permission to view messages sent in Local chat
channels on other servers, including ones that passthrough messages locally.

#### Bypassing Social Spy

If you have the `huskchat.command.socialspy.bypass` permission, you can bypass having your messages be spied on. Note
though that other players with the `huskchat.command.socialspy.bypass` permission bypass the bypass and will be able to
see your private messages.

## Building

To build HuskChat, run the following in the root of the project directory:

```
./gradlew clean build
```

## bStats

This plugin uses bStats to provide me with metrics about its usage:

* [View BungeeCord metrics](https://bstats.org/plugin/bungeecord/HuskChat/11882)
* [View Velocity metrics](https://bstats.org/plugin/velocity/HuskChat%20-%20Velocity/14187)

You can turn metric collection off by navigating to `plugins/bStats/config.yml` and editing the config to disable plugin
metrics.

## Support

* Report bugs: [Click here](https://github.com/WiIIiam278/HuskChat/issues)
* Discord support: Join the [HuskHelp Discord](https://discord.gg/tVYhJfyDWG)!
