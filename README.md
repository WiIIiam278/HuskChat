[![HuskChat Banner](images/banner-graphic.png)](https://github.com/WiIIiam278/HuskChat)
# HuskChat
[![Maven CI](https://github.com/WiIIiam278/HuskChat/actions/workflows/gradle.yml/badge.svg)](https://github.com/WiIIiam278/HuskHomes2/actions/workflows/maven.yml)
[![Discord](https://img.shields.io/discord/818135932103557162?color=7289da&logo=discord)](https://discord.gg/tVYhJfyDWG)

**HuskChat** is a no-frills, simple and customisable cross-server chat system for Minecraft networks running BungeeCord and Velocity frontend servers. 

HuskChat is easy to configure with an elegant out-of-box setup, while also being highly configurable, suiting a variety of use cases by allowing you to define channels and manage who can send and receive messages within them.

## Features
* Works great out of the box, install on your Velocity or BungeeCord-based proxy and use right away
* Hooks with LuckPerms to display user prefixes and suffixes
* Private messaging and replying commands
* Define who can send and receive messages in channels
* Define shortcut commands to let players quickly switch channels
* Utilise modern 1.16+ formatting, with RGB and Gradient support via [MineDown](https://github.com/Phoenix616/MineDown)

## Setup
1. Download `HuskChat.jar`
2. Drag it into the `/plugins/` folder on your proxy server (BungeeCord or Velocity; 1.16+. Derivatives like Waterfall should work fine too)
3. Restart your proxy. Once the config and message files have generated, make changes as appropriate and restart your proxy again

## Commands
| Command           | Usage                             | Aliases                                           | Description                                           | Permission                                                  |
|-------------------|-----------------------------------|---------------------------------------------------|-------------------------------------------------------|-------------------------------------------------------------|
| `/channel`        | `/channel <channel_id> [message]` | `/c`                                              | Send a message or switch to a chat channel            | `huskchat.command.channel`                                  |
| `/huskchat`       | `/huskchat <about/reload>`        | N/A                                               | View plugin information and reload                    | `huskchat.command.huskchat`                                 |
| `/msg`            | `/msg <player> <message>`         | `/m`, `/tell`, `/w`, `/whisper`, `/message`, `/t` | Send a private message to a player                    | `huskchat.command.msg`                                      |
| `/reply`          | `/reply <message>`                | `/r`                                              | Quickly reply to a private message                    | `huskchat.command.msg.reply`                                |
| Shortcut commands | `/<shortcut_command> <message>`   | N/A                                               | Quickly send a message in or switch to a chat channel | Channel send permission, e.g. `huskchat.channel.staff.send` |

## Channels & Out-of-box experience
Channels are what players talk in and can be switched between using the /channel command or specialised channel shortcut command. By default, HuskSync

### Channel definition
To define custom channels, put them under the `channels:` section of `config.yml`. Below is the specification for how this should be laid out, using the default `staff` channel as an example.
```yaml
channels:
  ...
  staff: # The ID of the channel. To switch to this channel, users would execute /channel staff
      format: '&e[Staff] %name%: &7' # Display format of the channel - See below
      broadcast_scope: GLOBAL # Broadcast scope of the channel - See below
      log_to_console: true # Whether messages sent to this channel should be logged to the proxy console
      permissions:
        send: 'huskchat.channel.staff.send' # Permission required to see channel messages
        receive: 'huskchat.channel.staff.receive' # Permission required to switch to & send messages
      shortcut_commands: # List of shortcut commands that users can use to quickly use the channel
        - /staff
        - /sc
  ...
```
### Channel scope
Channel scope defines the scope by which messages are broadcast and handled by HuskChat. The following options are available:
* `GLOBAL` - Message is broadcast globally to those with permissions via the proxy
* `LOCAL` - Message is broadcast via the proxy to players who have permission and are on the same server as the source
* `PASSTHROUGH` - Message is not handled by the proxy and is instead passed to the backend server
* `GLOBAL_PASSTHROUGH` - Message is broadcast via the proxy to players who have permission and are on the same server as the source and is additionally passed to the backend server
* `LOCAL_PASSTHROUGH` - Message is broadcast globally to those with permissions via the proxy and is additionally passed to the backend server

### Channel format & placeholders
The channel format defines how chat messages should be formatted. The message content itself is always appended after the formatting. Note that uncleared formatting will persist and apply to the message contents.
You can make use of [MineDown formatting](https://github.com/Phoenix616/MineDown) to use modern (1.16+) hexadecimal colors and easily make use of advanced effects such as gradients. You can embed this formatting within the prefix and suffix contents of LuckPerms groups as well.
Within the channel formatting you can make use of the following placeholders to format text.

#### Regular placeholders
* `%name%` - Username
* `%fullname%` - LuckPerms prefix, username & LuckPerms suffix
* `%prefix%` - LuckPerms prefix
* `%suffix%` - LuckPerms suffix
* `%ping%` - User's ping
* `%uuid%` -  User's uuid
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

## Building
To build HuskChat, run the following in the root of the project directory:
```
./gradlew clean build
```

## bStats
This plugin uses bStats to provide me with metrics about its usage:
* [View BungeeCord metrics](https://bstats.org/plugin/bungeecord/HuskChat/11882)
* [View Velocity metrics](https://bstats.org/plugin/velocity/HuskChat%20-%20Velocity/14187)

You can turn metric collection off by navigating to `plugins/bStats/config.yml` and editing the config to disable plugin metrics.

## Support
* Report bugs: [Click here](https://github.com/WiIIiam278/HuskChat/issues)
* Discord support: Join the [HuskHelp Discord](https://discord.gg/tVYhJfyDWG)!
