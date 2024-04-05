HuskChat supports displaying messages on Discord, either through webhooks for one-way communication or by using Spicord for two-way communication (some setup required).

To enable Discord support, set `enabled` to `true` in the `discord:` section of [`config.yml`](config-files). You can then configure the bot and channel webhooks.

## Webhooks
Webhooks are a simple way to send messages to a Discord channel. You can create a webhook for a channel by going to the channel settings, then Integrations, then Webhooks. You can then create a webhook and copy the URL. In the HuskChat config, you can then add the webhook URL to the `channel_webhooks` section of the config.

## Spicord
Spicord is a plugin that allows for two-way communication between Discord and Minecraft. You can find more information about Spicord [here](https://www.spigotmc.org/resources/spicord.64918/). Spicord can be installed on BungeeCord, Velocity, or Paper.

> **Why not support DiscordSRV?** DiscordSRV does not support Velocity/Bungee, and Spicord does :-)

### Installing Spicord
1. Download the Spicord plugin and place it in the plugins folder of your server alongside HuskChat.
2. Start the server.
3. Open the config.toml file located in the `plugins/Spicord` directory using a text editor.
   * Insert your bot token in the designated field (see below how to get a token)
   * Change the value of the enabled option to true
   * Add `huskchat` to the `addons` section of your bot
4. Restart your server.

Your Spicord `config.toml` file should contain a bot like this:
```toml
  name = "Server Chat"
  enabled = true
  token = "[YOUR TOKEN]"
  command_support = true
  command_prefix = "-"
  addons = [
    "spicord::info",
    "spicord::plugins",
    "spicord::players",
    "huskchat"
  ]
```

### Creating a bot
Here's how to create a bot and add it to your Discord server (Taken from [Spicord's documentation](https://github.com/Spicord/Spicord/blob/v5/tutorial/CREATE-A-BOT.md)):

1. Log-in into the [Discord Developer Portal](https://discord.com/developers/applications)
2. Click **New Application** and choose a name for your bot
3. You will see the information of your application and you will need to copy the numbers below **Client ID**, you will need it to invite your bot to your server
4. Switch to the **Bot** tab located at the left of the page, and then click **Add Bot > Yes, do it!**
5. You will see your bot information and there you can change its profile picture and name
6. Click the **Copy** button below the **Token** section, you will need to put it in the Spicord configuration
7. To invite your bot, go to `https://discord.com/oauth2/authorize?scope=bot&permissions=8&client_id=YOUR_ID` but before replace `YOUR_ID` with the ID you copied in the 3rd step, this will generate the invite url for your bot and redirect you to it, the generated url will make your bot have Administrator permission

Note that you are required to enable the following gateway intents for your bot on the developer panel, otherwise it won't boot up:
![Gateway intents](https://raw.githubusercontent.com/WiIIiam278/HuskChat/master/images/spicord-bot-intents.png)

### Configuring HuskChat
Once you have created your bot and invited it to your server, you can configure HuskChat to use it. In the `discord:` section of the config, set `enabled` to `true` and `spicord.enabled` to `true`. You can then configure the bot and channel IDs. 

To get the ID of a channel, ensure Developer Mode is enabled in your Discord settings, then right click a channel and select "Copy ID". All you need to do then is paste the ID into the config and map them to the corresponding in-game channel!

Restart your server, and enjoy!
