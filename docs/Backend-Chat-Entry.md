> **Note:** The information of this page does not apply to those running HuskChat on a single-server Spigot setup.

This page provides information on using HuskChat on the proxy with backend (Spigot) plugins that rely on using chat for data entry.

## Background
Plugins that rely on users entering things in chat to set names are not using the chat API in the way it was intended. Data entry should be done through commands, not through chat. Especially with the arrival of Minecraft 1.19.1, plugins should move away from relying on this (even if it is convenient). 

Anvil menus, command entry, sign menus, and other methods of data entry should be used. Some plugins, like *QuickShopReremake* provide command alternatives (`/i <amount>`) for data entry for this reason exactly.

## Things to know
* HuskChat is a proxy plugin. It sits on your proxy (Bungeecord, Waterfall, Velocity, etc) - *not* your "backend" Spigot server. This means that when someone types something into chat, HuskChat processes it on behalf of the Spigot server the player is connected to; HuskChat on your proxy won't let that message be passed on to the backend server. This means that plugins that rely on chat input won't work.
* However, there is a workaround. HuskChat lets you set the "broadcast scope" of a channel, which affects who sees the chat message sent by players. HuskChat provides three special scopes, ideal for handling situations such as this—`passthrough`, `local_passthrough` and `global_passthrough`.
  1. Create a channel with a `passthrough` scope.
  2. Define a convenient shortcut command for this channel (`/i`, for instance)
  3. Install [CancelChat](https://github.com/WiIIiam278/CancelChat/releases) - it's super light weight, don't worry) on your backend. If you don't do this, then chat messages will appear unformatted as your backend will process and dispatch the message as it normally would. 
  4. Save your config and restart the server. When prompted to enter data, switch to the channel using `/i` and enter the data. Players can then change their channel back as they normally would.
  5. See also: [Channel Specification](https://william278.net/docs/huskchat/Channels)