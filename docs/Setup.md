> **Note:** HuskChat is a proxy plugin; it is not installed on your Spigot/Paper/Fabric server. It runs on your Velocity/Bungee proxy, which sits atop your Minecraft server network.

This will walk you through installing HuskChat on your Velocity or BungeeCord/Waterfall-based proxy server.

## Requirements
* Java 16+
* A Velocity (recommended) or BungeeCord/Waterfall-based proxy server
* At least one Minecraft server connected to your proxy (running 1.16.5 or above for the best experience)

## Velocity Installation
1. Turn off your Velocity proxy
2. Download the [latest version of HuskChat](https://github.com/WiIIiam278/HuskChat/releases/latest). 
3. Download both the latest version of [UnsignedVelocity](https://modrinth.com/plugin/unsignedvelocity) and [VPacketEvents](https://modrinth.com/plugin/vpacketevents/)
4. Add all three jar files to your Velocity proxy server's `~/plugins` folder
5. Start your Velocity proxy. Let HuskChat generate its config files, then stop the proxy again.
6. Modify your HuskChat config files as needed, then start your Velocity proxy again.

## BungeeCord/Waterfall Installation
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