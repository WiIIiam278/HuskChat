# [![HuskChat Banner](images/banner-graphic.png)](https://github.com/WiIIiam278/HuskChat)
[![GitHub CI](https://img.shields.io/github/workflow/status/WiIIiam278/HuskChat/Java%20CI?logo=github)](https://github.com/WiIIiam278/HuskChat/actions/workflows/java_ci.yml)
[![JitPack API](https://img.shields.io/jitpack/version/net.william278/HuskChat?color=%2300fb9a&label=api&logo=gradle)](https://jitpack.io/#net.william278/HuskChat)
[![Support Discord](https://img.shields.io/discord/818135932103557162.svg?label=&logo=discord&logoColor=fff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/tVYhJfyDWG)

[Documentation, Guides & API](https://william278.net/docs/huskchat/) · [Resource Page](https://www.spigotmc.org/resources/huskchat.94496/) · [Bug Reports](https://github.com/WiIIiam278/HuskChat/issues)

**HuskChat** is a no-frills, simple and customisable cross-server chat system for Minecraft networks running BungeeCord and Velocity frontend servers.&dagger; 

It's designed to be easy to configure with an elegant out-of-box setup, while also being highly configurable, suiting a variety of use cases by allowing you to define channels and manage who can send and receive messages within them.

&dagger; _This plugin does not support Chat Reporting. Velocity 1.19.2+ users will need to use a fork that disables signed chat message enforcing for the time being. ([more&hellip;](https://github.com/WiIIiam278/HuskChat/issues/72))_

## Features
* Works great out of the box, install on your Velocity or BungeeCord-based proxy and use right away
* Hooks with LuckPerms to display user prefixes and suffixes
* Private messaging and replying commands - including group private messages
* Define who can send and receive messages in channels
* Define shortcut commands to let players quickly switch channels
* Machine learning powered profanity filter
* Customisable spam limiting filter, anti-advertising & special emoji
* Utilise modern 1.16+ formatting, with RGB and Gradient support via [MineDown](https://github.com/Phoenix616/MineDown)

## Commands
- `/channel` (and make shortcuts for each channel)
- `/msg`, `/r` (including group messages)
- `/socialspy`, `/localspy`
- `/broadcast`
- [Full list & permissions](https://william278.net/docs/huskchat/Commands-and-Permissions)

## Channels
Channels are what players talk in and can be switched between using the /channel command or specialised channel shortcut
command. By default, HuskChat has the following channels setup, perfect for a typical proxy server setup:

* `local` - Local scoped channel with `/local`, `/l` shortcut commands, for sending messages to players on the same server.
* `global` (default channel) - Global scoped channel with `/global`, `/g` shortcut commands, for sending messages across the network.
* `staff` - Global scoped channel with `/staff`, `/sc` shortcut commands. Great for letting staff communicate easily. Players need the `huskchat.channel.staff.send` and `huskchat.channel.staff.receive` permissions to send and receive messages in this channel respectively.
* `helpop` - Global scoped channel with `/helpop` shortcut command. Great for letting players easily contact staff. Players need the `huskchat.channel.helpop.receive` permission to receive messages in this channel.

[Channels are fully customizable](https://william278.net/docs/huskchat/Channels), including formatting, permission-restricting, broadcast scope, shortcut command amd more.

## Building
To build HuskHomes, you'll need python (>=`v3.6`) with associated packages installed; `jep` and `alt-profanity-check`. 
You can install these with `pip install jep` and `pip install alt-profanity-check`. These are needed to run the profanity filter tests.

Then, simply run the following in the root of the repository:
```
./gradlew clean build
```

## License
HuskHomes is licensed under [Apache-2.0 License](https://github.com/WiIIiam278/HuskChat/blob/master/LICENSE).

## Translation
Translations of the plugin locales are welcome to help make the plugin more accessible. Please submit a pull request with your translations as a `.yml` file.

- [Locales Directory](https://github.com/WiIIiam278/HuskChat/tree/master/common/src/main/resources/languages)
- [English Locales](https://github.com/WiIIiam278/HuskChat/tree/master/common/src/main/resources/languages/en-gb.yml)

## bStats
This plugin uses bStats to provide me with metrics about its usage:
* [View BungeeCord metrics](https://bstats.org/plugin/bungeecord/HuskChat/11882)
* [View Velocity metrics](https://bstats.org/plugin/velocity/HuskChat%20-%20Velocity/14187)

You can turn metric collection off by navigating to `plugins/bStats/config.yml` and editing the config to disable plugin
metrics.

## Links
- [Documentation, Guides & API](https://william278.net/docs/huskchat/)
- [Resource Page](https://www.spigotmc.org/resources/huskchat.94496/)
- [Bug Reports](https://github.com/WiIIiam278/HuskChat/issues)
- [Discord Support](https://discord.gg/tVYhJfyDWG)


---
&copy; [William278](https://william278.net/), 2022. Licensed under the Apache-2.0 License.
