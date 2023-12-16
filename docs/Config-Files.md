This page contains the configuration structure for HuskChat.

## Configuration structure
📁 `plugins/HuskChat/`
- 📄 `config.yml`: General plugin configuration
- 📄 `spies.yml`: Cache of users currently in [[social and local spy]] mode
- 📄 `messages-xx-xx.yml`: Plugin locales, formatted in MineDown (see [[Translations]])

## Example files
<details>
<summary>config.yml</summary>

```yaml
#  ------------------------------
# |        HuskChat Config      |
# |    Developed by William278  |
#  ------------------------------
# Configuration guide: https://github.com/WiIIiam278/HuskChat/wiki
# To modify individual message locales, see the messages file.

# General options. Default channel should match one of the channels below.
language: 'en-gb'
check_for_updates: true
default_channel: global
channel_log_format: '[CHAT] [%channel%] %sender%: '
channel_command_aliases: # Must contain at least one item; the first being the primary alias of the command
  - /channel
  - /c

# Placeholder configuration
placeholders:
  use_papi: true # For proxy setups, requires PAPIProxyBridge on both the proxy/backend servers
  cache_time: 3000 # If using PAPIProxyBridge, how long to cache placeholders for in milliseconds

# Chat channel configuration.
# - You can edit the default channels and make your own if you would like.
# - Channels that have permissions set require them to send and receive messages respectively.
channels:
  local:
    format: '%fullname%&r&f: '
    broadcast_scope: LOCAL
    log_to_console: true
    shortcut_commands:
      - /local
      - /l
  global:
    format: '&#00fb9a&[G]&r&f %fullname%&r&f: '
    broadcast_scope: GLOBAL
    log_to_console: true
    shortcut_commands:
      - /global
      - /g
    #restricted_servers: # Set where it is not possible to use this channel
    #  - minigame
  staff:
    format: '&e[Staff] %name%: &7'
    broadcast_scope: GLOBAL
    log_to_console: true
    filtered: false
    permissions:
      send: 'huskchat.channel.staff.send'
      receive: 'huskchat.channel.staff.receive'
    shortcut_commands:
      - /staff
      - /sc
  helpop:
    format: '&#00fb9a&[HelpOp] %name%:&7 '
    broadcast_scope: GLOBAL
    log_to_console: true
    filtered: false
    permissions:
      receive: 'huskchat.channel.helpop.receive'
    shortcut_commands:
      - /helpop
      - /helpme

# Server Default Channel Configuration
# - Set a channel that the player will automatically be set to when changing servers
# - Players will still be able to switch channels and use /msg and /r unless you add servers to the restricted_servers list
#server_default_channels:
#  minigame: local

# Options for the /msg and /r commands
message_command:
  enabled: true
  msg_aliases:
    - /msg
    - /m
    - /tell
    - /whisper
    - /w
    - /pm
  reply_aliases:
    - /reply
    - /r
  log_to_console: true
  log_format: '[MSG] [%sender% -> %receiver%]: '
  group_messages: # Whether to allow sending and replying to a message in a group (/msg User1,User2 <message>)
    enabled: true
    max_size: 10
  format: # Formats for the /msg command (MineDown syntax). A separate format is used for group private messages.
    inbound: '&#00fb9a&%name% &8→ &#00fb9a&You&8:&f '
    outbound: '&#00fb9a&You &8→ &#00fb9a&%name%&8:&f '
    group_inbound: '&#00fb9a&%name% &8→ &#00fb9a&You [₍₊%group_amount_subscript%₎](gray show_text=&7%group_members% suggest_command=/msg %group_members_comma_separated% )&8:&f '
    group_outbound: '&#00fb9a&You &8→ &#00fb9a&%name% [₍₊%group_amount_subscript%₎](gray show_text=&7%group_members% suggest_command=/msg %group_members_comma_separated% )&8:&f '
  #restricted_servers: # Set where /msg and /r cannot be used
  #  - hub

# Options for the /socialspy command
social_spy:
  enabled: true
  format: '&e[Spy] &7%name% &8→ &7%receiver_name%:%spy_color% '
  group_format: '&e[Spy] &7%name% &8→ &7%receiver_name% [₍₊%group_amount_subscript%₎](gray show_text=&7%group_members% suggest_command=/msg %group_members_comma_separated% ):%spy_color% '
  socialspy_aliases:
    - /socialspy
    - /ss
# Options for the /localspy command
local_spy:
  enabled: true
  format: '&e[Spy] &7[%channel%] %name%&8:%spy_color% '
  localspy_aliases:
    - /localspy
    - /ls
  #excluded_local_channels: # Channels to exclude from local spying
  #  - local
# Options for the /broadcast command
broadcast_command:
  enabled: true
  broadcast_aliases:
    - /broadcast
    - /alert
  format: '&6[Broadcast]&e '
  log_to_console: true
  log_format: '[BROADCAST]: '

# Chat filter options
chat_filters:
  # Filters against IP addresses and links
  advertising_filter:
    enabled: true
    private_messages: false
    broadcast_messages: false
    channels:
      - global
      - local
  # Filters against CAPS use; specify the maximum % a message can contain capital letters
  caps_filter:
    enabled: true
    max_caps_percentage: 0.4
    private_messages: false
    broadcast_messages: false
    channels:
      - global
      - local
  # Filters against users sending messages too quickly (configure how many messages users can send over a period of seconds)
  spam_filter:
    enabled: true
    period_seconds: 4
    messages_per_period: 3
    private_messages: false
    broadcast_messages: false
    channels:
      - global
      - local
  # Filters against profanity using machine learning. Requires Python 3.8+ on the server with jep and alt-profanity-check installed.
  profanity_filter:
    enabled: false
    library_path: '' # Define a directory path for the jep library
    mode: AUTOMATIC # Filter rule - AUTOMATIC or TOLERANCE.
    tolerance: 0.78 # If using TOLERANCE mode, the algorithm will determine a profanity probability and compare with this to filter.
    private_messages: false
    broadcast_messages: false
    channels:
      - global
      - local
  repeat_filter:
    enabled: true
    previous_messages_to_check: 2
    private_messages: false
    broadcast_messages: false
    channels:
      - global
      - local
  # Filters against the use of non-ASCII (Unicode) characters
  ascii_filter:
    enabled: false
    private_messages: false
    broadcast_messages: false
    channels:
      - global
      - local

# Chat message replacer options
message_replacers:
  # Replaces text emoticons in messages with the correct emoji
  emoji_replacer:
    enabled: true
    case_insensitive: false
    private_messages: true
    broadcast_messages: true
    channels:
      - global
      - local
      - helpop
      - staff
    emoji: # Emote character options: https://gist.githubusercontent.com/WiIIiam278/b74a6af6d9670350a60ad09d63b67169/raw/d8b596471c812eb2b68638469cb779d928bd733f/minecraft_unicode_characters.txt
      ':)': '☺'
      ':smile:': '☺'
      ':-)': '☺'
      ':(': '☹'
      ':frown:': '☹'
      ':-(': '☹'
      ':fire:': '🔥'


# Options for customizing player join and quit messages
join_and_quit_messages:
  join:
    enabled: false
    # Use the huskchat.join_message.[text] permission to override this per-group if needed
    format: '&e%name% joined the network'
  quit:
    enabled: false
    # Use the huskchat.quit_message.[text] permission to override this per-group if needed
    format: '&e%name% left the network'
  broadcast_scope: GLOBAL # Note that on Velocity/Bungee, PASSTHROUGH modes won't cancel local join/quit messages

# Discord webhook options, which lets you send messages to a Discord channel
discord:
  enabled: false
  format_style: 'inline' # Format style to display Discord messages in - 'inline' or 'embedded'
  channel_webhooks:
    global: 'https://discord.com/api/webhooks/channel_id/secret'
  spicord:
    enabled: true  # If Spicord is installed, use that for two-way message support
    username_format: '@%discord_handle%' # Format of Discord users in-game. Note this doesn't support other placeholders
    #receive_channel_map: # Send in-game messages on these channels to a specified Discord channel (by numeric ID)
    #  global: '123456789012345678'
    #send_channel_map: # Send Discord messages on these channels (by numeric ID) to a specified in-game channel
    #  '123456789012345678': 'global'

# If you want %servername% to show something other than the server name,
# set it here and uncomment this section
#server_name_replacement:
#  lobby: hub

# Version of the config file
config-version: 2
```
</details>