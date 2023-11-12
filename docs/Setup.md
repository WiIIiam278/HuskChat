This will walk you through installing HuskChat on your Velocity or BungeeCord/Waterfall-based proxy server.

## Requirements
* Java 16+
* A Spigot (1.16.5+) Minecraft server (for single-server setups) OR a Velocity (recommended) or BungeeCord/Waterfall-based proxy server

> **Warning:** The Spigot plugin "NoChatReports" will not work with HuskChat. Use FreedomChat instead if you wish to eliminate chat report warnings.

## Single-server Setup Instructions
1. Turn off your Spigot server
2. Download the [latest version of HuskChat](https://github.com/WiIIiam278/HuskChat/releases/latest).
3. Add the jar file to your Spigot server's `~/plugins` folder
4. Start your Spigot server. Let HuskChat generate its config files, then stop the server again.
5. Modify your HuskChat config files as needed, then start your Spigot server again.

## Multi-server Setup Instructions
### Velocity Installation
1. Turn off your Velocity proxy
2. Download the [latest version of HuskChat](https://github.com/WiIIiam278/HuskChat/releases/latest). 
3. Download the [latest version of SignedVelocity](https://modrinth.com/plugin/signedvelocity) for Velocity
4. Add both jar files to your Velocity proxy server's `~/plugins` folder. 
   1. Additionally, download [SignedVelocity](https://modrinth.com/plugin/signedvelocity) for your backend server add the plugin to your `/plugins/~` folders there. Restart your backend servers.
   2. You may alternatively use UnsignedVelocity and VPacketEvents, which only need installing on your proxy if you are using Fabric for your backend servers.
5. Start your Velocity proxy. Let HuskChat generate its config files, then stop the proxy again.
6. Modify your HuskChat config files as needed, then start your Velocity proxy again.

### BungeeCord/Waterfall Installation
> **Warning:** As there's not a good chat message stripping plugin for Bungee proxies at the moment, your mileage may vary with using HuskChat on Minecraft 1.19.1+. We recommend Velocity for it's stronger performance, security & more modern codebase.

1. Turn off your BungeeCord/Waterfall proxy
2. Download the [latest version of HuskChat](https://github.com/WiIIiam278/HuskChat/releases/latest). 
3. Add the jar file to your BungeeCord/Waterfall proxy server's `~/plugins` folder
4. Start your BungeeCord/Waterfall proxy. Let HuskChat generate its config files, then stop the proxy again.
5. Modify your HuskChat config files as needed, then start your BungeeCord/Waterfall proxy again.

## Next Steps
* [[Channels]]
* [[Formatting]]
* [[Commands]]
